package Practical;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class BankAccountSystem {

    // VALUE OBJECT: Money must always be positive.
    static final class Money {
        private final BigDecimal amount;

        public Money(String amount) {
            Objects.requireNonNull(amount, "Amount is required");
            BigDecimal value = new BigDecimal(amount).setScale(2);

            if (value.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException(
                    "Amount must be greater than zero.");
            }
            this.amount = value;
        }

        public BigDecimal value() {
            return amount;
        }
    }

    // VALUE OBJECT: Account number.
    record AccountNumber(String value) {
        AccountNumber {
            if (value == null ||
                !value.matches("ACC-[0-9]{6}")) {
                throw new IllegalArgumentException(
                    "Invalid account number.");
            }
        }
    }

    enum AccountStatus {
        ACTIVE, FROZEN, CLOSED
    }

    enum TransactionType {
        DEPOSIT, WITHDRAWAL, TRANSFER_IN, TRANSFER_OUT
    }

    // IMMUTABLE LEDGER ENTRY.
    record LedgerEntry(
        UUID transactionId,
        TransactionType type,
        BigDecimal amount,
        String description,
        Instant dateTime
    ) {}

    // ENTITY: Bank Account.
    static final class BankAccount {

        private final AccountNumber accountNumber;
        private final String accountHolder;
        private final BigDecimal overdraftLimit;

        private BigDecimal balance;
        private AccountStatus status;

        private final List<LedgerEntry> ledger =
            new ArrayList<>();

        public BankAccount(
            AccountNumber accountNumber,
            String accountHolder,
            String overdraftLimit,
            String openingBalance) {

            this.accountNumber =
                Objects.requireNonNull(accountNumber);

            if (accountHolder == null ||
                accountHolder.isBlank()) {
                throw new IllegalArgumentException(
                    "Account holder name is required.");
            }

            this.accountHolder = accountHolder;

            this.overdraftLimit =
                new BigDecimal(overdraftLimit).setScale(2);

            this.balance =
                new BigDecimal(openingBalance).setScale(2);

            if (this.overdraftLimit.compareTo(
                BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException(
                    "Invalid overdraft limit.");
            }

            if (this.balance.compareTo(
                this.overdraftLimit.negate()) < 0) {
                throw new IllegalArgumentException(
                    "Opening balance exceeds overdraft limit.");
            }

            this.status = AccountStatus.ACTIVE;
        }

        public AccountNumber getAccountNumber() {
            return accountNumber;
        }

        public String getAccountHolder() {
            return accountHolder;
        }

        public synchronized BigDecimal getBalance() {
            return balance;
        }

        public synchronized AccountStatus getStatus() {
            return status;
        }

        public synchronized List<LedgerEntry> getLedger() {
            return List.copyOf(ledger);
        }

        private void checkActive() {
            if (status != AccountStatus.ACTIVE) {
                throw new IllegalStateException(
                    "Account is " + status);
            }
        }

        private void checkWithdrawalAllowed(Money money) {
            checkActive();

            BigDecimal newBalance =
                balance.subtract(money.value());

            if (newBalance.compareTo(
                overdraftLimit.negate()) < 0) {
                throw new IllegalStateException(
                    "Overdraft limit exceeded.");
            }
        }

        private void addLedgerEntry(
            TransactionType type,
            Money money,
            String description) {

            ledger.add(new LedgerEntry(
                UUID.randomUUID(),
                type,
                money.value(),
                description,
                Instant.now()
            ));
        }

        // DEPOSIT
        public synchronized void deposit(Money money) {
            Objects.requireNonNull(money);
            checkActive();

            balance = balance.add(money.value());

            addLedgerEntry(
                TransactionType.DEPOSIT,
                money,
                "Money deposited"
            );
        }

        // WITHDRAW
        public synchronized void withdraw(Money money) {
            Objects.requireNonNull(money);
            checkWithdrawalAllowed(money);

            balance = balance.subtract(money.value());

            addLedgerEntry(
                TransactionType.WITHDRAWAL,
                money,
                "Money withdrawn"
            );
        }

        // FREEZE ACCOUNT
        public synchronized void freezeAccount() {
            if (status != AccountStatus.ACTIVE) {
                throw new IllegalStateException(
                    "Only active accounts can be frozen.");
            }

            status = AccountStatus.FROZEN;
        }

        // UNFREEZE ACCOUNT
        public synchronized void unfreezeAccount() {
            if (status != AccountStatus.FROZEN) {
                throw new IllegalStateException(
                    "Only frozen accounts can be unfrozen.");
            }

            status = AccountStatus.ACTIVE;
        }

        // CLOSE ACCOUNT
        public synchronized void closeAccount() {
            if (status == AccountStatus.CLOSED) {
                throw new IllegalStateException(
                    "Account is already closed.");
            }

            if (balance.compareTo(BigDecimal.ZERO) != 0) {
                throw new IllegalStateException(
                    "Balance must be zero before closing.");
            }

            status = AccountStatus.CLOSED;
        }
    }

    // TRANSFER MONEY BETWEEN TWO ACCOUNTS.
    public static void transfer(
        BankAccount source,
        BankAccount destination,
        Money money) {

        Objects.requireNonNull(source);
        Objects.requireNonNull(destination);
        Objects.requireNonNull(money);

        if (source == destination) {
            throw new IllegalArgumentException(
                "Source and destination must differ.");
        }

        synchronized (source) {
            synchronized (destination) {

                // Validate both accounts first.
                source.checkWithdrawalAllowed(money);
                destination.checkActive();

                // Update both balances.
                source.balance =
                    source.balance.subtract(money.value());

                destination.balance =
                    destination.balance.add(money.value());

                // Update transaction histories.
                source.addLedgerEntry(
                    TransactionType.TRANSFER_OUT,
                    money,
                    "Transferred to " +
                    destination.accountNumber.value()
                );

                destination.addLedgerEntry(
                    TransactionType.TRANSFER_IN,
                    money,
                    "Received from " +
                    source.accountNumber.value()
                );
            }
        }
    }

    // MAIN METHOD.
    public static void main(String[] args) {

        BankAccount accountA = new BankAccount(
            new AccountNumber("ACC-100001"),
            "Rahul",
            "2000.00",
            "10000.00"
        );

        BankAccount accountB = new BankAccount(
            new AccountNumber("ACC-100002"),
            "Arun",
            "1000.00",
            "5000.00"
        );

        System.out.println("BANK ACCOUNT SYSTEM");

        // 1. DEPOSIT
        accountA.deposit(new Money("1500.00"));
        System.out.println(
            "After deposit: " + accountA.getBalance());

        // 2. WITHDRAW
        accountA.withdraw(new Money("500.00"));
        System.out.println(
            "After withdrawal: " + accountA.getBalance());

        // 3. TRANSFER
        transfer(accountA, accountB, new Money("5000.00"));

        System.out.println(
            "Account A: " + accountA.getBalance());

        System.out.println(
            "Account B: " + accountB.getBalance());

        // 4. FREEZE ACCOUNT
        accountB.freezeAccount();
        System.out.println(
            "Account B status: " + accountB.getStatus());

        // Test frozen account protection.
        try {
            accountB.withdraw(new Money("100.00"));
        } catch (IllegalStateException e) {
            System.out.println(
                "Withdrawal blocked: " + e.getMessage());
        }

        // 5. UNFREEZE ACCOUNT
        accountB.unfreezeAccount();
        System.out.println(
            "After unfreezing: " + accountB.getStatus());

        // 6. DISPLAY LEDGER
        System.out.println("\nACCOUNT A TRANSACTION HISTORY");

        for (LedgerEntry entry : accountA.getLedger()) {
            System.out.println(
                entry.dateTime() + " | " +
                entry.type() + " | " +
                entry.amount() + " | " +
                entry.description()
            );
        }

        System.out.println("\nACCOUNT B TRANSACTION HISTORY");

        for (LedgerEntry entry : accountB.getLedger()) {
            System.out.println(
                entry.dateTime() + " | " +
                entry.type() + " | " +
                entry.amount() + " | " +
                entry.description()
            );
        }

        System.out.println("\nBanking operations completed.");
    }
}
