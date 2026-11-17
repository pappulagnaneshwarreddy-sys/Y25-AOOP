package Practical;


import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class UserAccountSystem {

    // VALUE OBJECT: Email validation and normalization.
    record EmailAddress(String value) {
        EmailAddress {
            if (value == null) {
                throw new IllegalArgumentException("Email is required.");
            }

            value = value.trim().toLowerCase(Locale.ROOT);

            if (!value.matches(
                "^[A-Za-z0-9.!#$%&'*+/=?^_`{|}~-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)+$")) {
                throw new IllegalArgumentException("Invalid email address.");
            }
        }

        @Override
        public String toString() {
            return value;
        }
    }

    // VALUE OBJECT: Phone number validation.
    record PhoneNumber(String value) {
        PhoneNumber {
            if (value == null) {
                throw new IllegalArgumentException("Phone required.");
            }

            value = value.replaceAll("[\\s()-]", "");

            if (!value.matches("\\+?[0-9]{7,15}")) {
                throw new IllegalArgumentException("Invalid phone number.");
            }
        }
    }

    // VALUE OBJECT: Postal address validation.
    record PostalAddress(
        String street,
        String city,
        String postalCode,
        String country
    ) {
        PostalAddress {
            if (street == null || street.isBlank()
                || city == null || city.isBlank()
                || postalCode == null || postalCode.isBlank()
                || country == null || country.isBlank()) {

                throw new IllegalArgumentException(
                    "All address fields are required.");
            }

            street = street.trim();
            city = city.trim();
            postalCode = postalCode.trim();
            country = country.trim();
        }
    }

    // VALUE OBJECT: Unique user ID.
    record UserId(UUID value) {
        UserId {
            Objects.requireNonNull(value);
        }

        static UserId create() {
            return new UserId(UUID.randomUUID());
        }
    }

    // ACCOUNT STATUS.
    enum AccountStatus {
        ACTIVE, SUSPENDED, DELETED
    }

    // EMAIL VERIFICATION STATES.
    sealed interface EmailVerification
        permits PendingVerification, Verified {

        boolean isVerified();
    }

    // PENDING EMAIL VERIFICATION.
    record PendingVerification(
        String token,
        Instant expiresAt
    ) implements EmailVerification {

        PendingVerification {
            if (token == null || token.isBlank()) {
                throw new IllegalArgumentException("Token required.");
            }

            Objects.requireNonNull(expiresAt);
        }

        @Override
        public boolean isVerified() {
            return false;
        }
    }

    // VERIFIED EMAIL.
    record Verified(Instant verifiedAt)
        implements EmailVerification {

        Verified {
            Objects.requireNonNull(verifiedAt);
        }

        @Override
        public boolean isVerified() {
            return true;
        }
    }

    // ENTITY: USER ACCOUNT.
    static final class UserAccount {

        private final UserId id;

        private EmailAddress email;
        private EmailVerification verification;

        private Optional<PhoneNumber> phone;
        private Optional<PostalAddress> address;

        private AccountStatus status;

        UserAccount(
            EmailAddress email,
            Optional<PhoneNumber> phone,
            Optional<PostalAddress> address,
            PendingVerification pending
        ) {
            this.id = UserId.create();

            this.email = Objects.requireNonNull(email);
            this.phone = Objects.requireNonNull(phone);
            this.address = Objects.requireNonNull(address);
            this.verification = Objects.requireNonNull(pending);

            this.status = AccountStatus.ACTIVE;
        }

        public UserId getId() {
            return id;
        }

        public EmailAddress getEmail() {
            return email;
        }

        public boolean isEmailVerified() {
            return verification.isVerified();
        }

        public AccountStatus getStatus() {
            return status;
        }

        public Optional<PhoneNumber> getPhone() {
            return phone;
        }

        public Optional<PostalAddress> getAddress() {
            return address;
        }

        // VERIFY EMAIL.
        private void verifyEmail(
            String suppliedToken,
            Instant now
        ) {
            if (!(verification instanceof PendingVerification pending)) {
                throw new IllegalStateException(
                    "Email is already verified.");
            }

            if (!pending.expiresAt().isAfter(now)) {
                throw new IllegalStateException(
                    "Verification token has expired.");
            }

            if (!constantTimeEquals(
                pending.token(), suppliedToken)) {

                throw new IllegalArgumentException(
                    "Incorrect verification token.");
            }

            verification = new Verified(now);
        }

        // CHANGE EMAIL.
        // New email must be verified again.
        private void replaceEmail(
            EmailAddress newEmail,
            PendingVerification newPending
        ) {
            email = Objects.requireNonNull(newEmail);
            verification = Objects.requireNonNull(newPending);
        }

        // UPDATE PHONE.
        public void updatePhone(Optional<PhoneNumber> newPhone) {
            phone = Objects.requireNonNull(newPhone);
        }

        // UPDATE ADDRESS.
        public void updateAddress(Optional<PostalAddress> newAddress) {
            address = Objects.requireNonNull(newAddress);
        }

        // SUSPEND ACCOUNT.
        public void suspend() {
            if (status != AccountStatus.ACTIVE) {
                throw new IllegalStateException(
                    "Only active accounts can be suspended.");
            }

            status = AccountStatus.SUSPENDED;
        }

        // ACTIVATE ACCOUNT.
        public void activate() {
            if (status != AccountStatus.SUSPENDED) {
                throw new IllegalStateException(
                    "Only suspended accounts can be activated.");
            }

            status = AccountStatus.ACTIVE;
        }

        // DELETE ACCOUNT.
        public void delete() {
            if (status == AccountStatus.DELETED) {
                throw new IllegalStateException(
                    "Account already deleted.");
            }

            status = AccountStatus.DELETED;
        }

        // LOGIN.
        public void login() {
            if (status != AccountStatus.ACTIVE) {
                throw new IllegalStateException(
                    "Login denied: account is " + status);
            }

            if (!verification.isVerified()) {
                throw new IllegalStateException(
                    "Email is not verified.");
            }

            System.out.println(
                "Login successful for " + email);
        }
    }

    // REPOSITORY: EMAIL UNIQUENESS.
    static final class UserRepository {

        private final Map<String, UserAccount> usersByEmail =
            new HashMap<>();

        // REGISTER USER.
        public synchronized UserAccount register(
            EmailAddress email,
            Optional<PhoneNumber> phone,
            Optional<PostalAddress> address,
            String token,
            Instant expiry
        ) {
            String key = email.value();

            if (usersByEmail.containsKey(key)) {
                throw new IllegalStateException(
                    "Email already registered.");
            }

            UserAccount user = new UserAccount(
                email,
                phone,
                address,
                new PendingVerification(token, expiry)
            );

            usersByEmail.put(key, user);

            return user;
        }

        // VERIFY REGISTERED USER.
        public synchronized void verifyEmail(
            EmailAddress email,
            String token,
            Instant now
        ) {
            UserAccount user =
                usersByEmail.get(email.value());

            if (user == null) {
                throw new IllegalArgumentException(
                    "User not found.");
            }

            user.verifyEmail(token, now);
        }

        // CHANGE REGISTERED EMAIL.
        public synchronized void changeEmail(
            UserAccount user,
            EmailAddress newEmail,
            String newToken,
            Instant expiry
        ) {
            String newKey = newEmail.value();

            UserAccount existing = usersByEmail.get(newKey);

            if (existing != null && existing != user) {
                throw new IllegalStateException(
                    "Email already registered.");
            }

            usersByEmail.remove(user.email.value());

            user.replaceEmail(
                newEmail,
                new PendingVerification(newToken, expiry)
            );

            usersByEmail.put(newKey, user);
        }

        // SEARCH USER.
        public synchronized Optional<UserAccount> findByEmail(
            EmailAddress email
        ) {
            return Optional.ofNullable(
                usersByEmail.get(email.value()));
        }
    }

    // SECURE TOKEN GENERATION.
    private static final SecureRandom RANDOM =
        new SecureRandom();

    private static String generateToken() {
        byte[] bytes = new byte[24];

        RANDOM.nextBytes(bytes);

        return Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(bytes);
    }

    // CONSTANT-TIME TOKEN COMPARISON.
    private static boolean constantTimeEquals(
        String expected,
        String actual
    ) {
        if (actual == null) {
            return false;
        }

        return MessageDigest.isEqual(
            expected.getBytes(
                java.nio.charset.StandardCharsets.UTF_8),
            actual.getBytes(
                java.nio.charset.StandardCharsets.UTF_8)
        );
    }

    // MAIN METHOD.
    public static void main(String[] args) {

        UserRepository repository = new UserRepository();

        String token = generateToken();
        Instant now = Instant.now();

        // 1. REGISTER USER.
        UserAccount user = repository.register(
            new EmailAddress("Student@Example.com"),

            Optional.of(
                new PhoneNumber("+91 98765-43210")),

            Optional.of(
                new PostalAddress(
                    "12 Main Road",
                    "Hyderabad",
                    "500001",
                    "India")),

            token,
            now.plus(Duration.ofHours(24))
        );

        System.out.println("USER ACCOUNT SYSTEM");

        System.out.println(
            "User registered: " + user.getEmail());

        System.out.println(
            "User ID: " + user.getId().value());

        // DEMO ONLY: Real applications send this token by email.
        System.out.println(
            "Verification token: " + token);

        // 2. VERIFY EMAIL.
        repository.verifyEmail(
            user.getEmail(), token, now);

        System.out.println(
            "Email verified: " + user.isEmailVerified());

        // 3. LOGIN.
        user.login();

        // 4. SUSPEND ACCOUNT.
        user.suspend();

        System.out.println(
            "Account status: " + user.getStatus());

        try {
            user.login();
        } catch (IllegalStateException e) {
            System.out.println(
                "Login blocked: " + e.getMessage());
        }

        // 5. ACTIVATE ACCOUNT.
        user.activate();

        System.out.println(
            "After activation: " + user.getStatus());

        // 6. CHANGE EMAIL.
        String newToken = generateToken();

        repository.changeEmail(
            user,
            new EmailAddress("student.new@example.com"),
            newToken,
            Instant.now().plus(Duration.ofHours(24))
        );

        System.out.println(
            "Changed email: " + user.getEmail());

        System.out.println(
            "Verified after email change: "
            + user.isEmailVerified());

        // 7. VERIFY NEW EMAIL.
        repository.verifyEmail(
            user.getEmail(),
            newToken,
            Instant.now()
        );

        System.out.println(
            "New email verified: "
            + user.isEmailVerified());

        // 8. DELETE ACCOUNT.
        user.delete();

        System.out.println(
            "Final status: " + user.getStatus());

        try {
            user.login();
        } catch (IllegalStateException e) {
            System.out.println(
                "Login blocked: " + e.getMessage());
        }
    }
}
