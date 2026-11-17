
package DefensiveCopying;

import java.util.*;

// ==========================================
// 1. TYPED IDENTIFIERS
// ==========================================

record WCustomerId(int value) {
    public WCustomerId {
        if (value <= 0) {
            throw new IllegalArgumentException("Invalid Customer ID");
        }
    }
}

record WBookingId(int value) {
    public WBookingId {
        if (value <= 0) {
            throw new IllegalArgumentException("Invalid Booking ID");
        }
    }
}

record WParkingSpotId(int value) {
    public WParkingSpotId {
        if (value <= 0) {
            throw new IllegalArgumentException("Invalid Parking Spot ID");
        }
    }
}

record WPaymentId(int value) {
    public WPaymentId {
        if (value <= 0) {
            throw new IllegalArgumentException("Invalid Payment ID");
        }
    }
}

// ==========================================
// 2. MONEY - IMMUTABLE VALUE OBJECT
// ==========================================

record WMoney(long paise, String currency) {

    public WMoney {
        if (paise < 0) {
            throw new IllegalArgumentException(
                "Money cannot be negative"
            );
        }

        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException(
                "Currency cannot be empty"
            );
        }

        currency = currency.toUpperCase(Locale.ROOT);
    }

    public WMoney plus(WMoney other) {
        checkCurrency(other);

        return new WMoney(
            Math.addExact(this.paise, other.paise),
            currency
        );
    }

    public WMoney minus(WMoney other) {
        checkCurrency(other);

        if (this.paise < other.paise) {
            throw new IllegalArgumentException(
                "Insufficient amount"
            );
        }

        return new WMoney(
            this.paise - other.paise,
            currency
        );
    }

    public WMoney times(long quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException(
                "Quantity cannot be negative"
            );
        }

        return new WMoney(
            Math.multiplyExact(this.paise, quantity),
            currency
        );
    }

    public List<WMoney> allocate(int parts) {
        if (parts <= 0) {
            throw new IllegalArgumentException(
                "Parts must be greater than zero"
            );
        }

        List<WMoney> result = new ArrayList<>();

        long baseAmount = paise / parts;
        long remainder = paise % parts;

        for (int i = 0; i < parts; i++) {
            long amount = baseAmount;

            if (i < remainder) {
                amount++;
            }

            result.add(new WMoney(amount, currency));
        }

        return List.copyOf(result);
    }

    private void checkCurrency(WMoney other) {
        if (other == null ||
            !this.currency.equals(other.currency)) {

            throw new IllegalArgumentException(
                "Currency mismatch"
            );
        }
    }

    @Override
    public String toString() {
        return String.format(
            Locale.ROOT,
            "%s %d.%02d",
            currency,
            paise / 100,
            paise % 100
        );
    }
}

// ==========================================
// 3. CUSTOMER
// ==========================================

class WCustomer {

    private final WCustomerId customerId;
    private final String name;

    public WCustomer(WCustomerId customerId, String name) {

        if (customerId == null || name == null ||
            name.isBlank()) {

            throw new IllegalArgumentException(
                "Invalid customer details"
            );
        }

        this.customerId = customerId;
        this.name = name;
    }

    public WCustomerId getCustomerId() {
        return customerId;
    }

    public String getName() {
        return name;
    }
}

// ==========================================
// 4. PARKING SPOT
// ==========================================

enum WSpotStatus {
    AVAILABLE,
    OCCUPIED
}

class WParkingSpot {

    private final WParkingSpotId spotId;
    private final String location;

    private WSpotStatus status;
    private String vehicleNumber;

    public WParkingSpot(
        WParkingSpotId spotId,
        String location
    ) {

        if (spotId == null || location == null ||
            location.isBlank()) {

            throw new IllegalArgumentException(
                "Invalid parking spot details"
            );
        }

        this.spotId = spotId;
        this.location = location;
        this.status = WSpotStatus.AVAILABLE;
        this.vehicleNumber = null;
    }

    public boolean isAvailable() {
        return status == WSpotStatus.AVAILABLE;
    }

    // Tell, Don't Ask

    public void parkVehicle(String vehicleNumber) {

        if (!isAvailable()) {
            throw new IllegalStateException(
                "Parking spot is already occupied"
            );
        }

        if (vehicleNumber == null ||
            vehicleNumber.isBlank()) {

            throw new IllegalArgumentException(
                "Invalid vehicle number"
            );
        }

        this.vehicleNumber = vehicleNumber;
        this.status = WSpotStatus.OCCUPIED;
    }

    public void releaseSpot() {

        if (isAvailable()) {
            throw new IllegalStateException(
                "Parking spot is already available"
            );
        }

        this.vehicleNumber = null;
        this.status = WSpotStatus.AVAILABLE;
    }

    public WParkingSpotView getSnapshot() {
        return new WParkingSpotView(
            spotId,
            location,
            status,
            vehicleNumber
        );
    }
}

// ==========================================
// 5. IMMUTABLE PARKING SPOT VIEW
// ==========================================

record WParkingSpotView(
    WParkingSpotId spotId,
    String location,
    WSpotStatus status,
    String vehicleNumber
) {}

// ==========================================
// 6. PAYMENT
// ==========================================

record WPayment(
    WPaymentId paymentId,
    WBookingId bookingId,
    WMoney amount,
    boolean successful
) {

    public WPayment {
        if (paymentId == null ||
            bookingId == null ||
            amount == null) {

            throw new IllegalArgumentException(
                "Invalid payment details"
            );
        }
    }
}

// ==========================================
// 7. BOOKING STATE
// ==========================================

enum WBookingStatus {
    PENDING,
    CONFIRMED,
    CANCELLED
}

// ==========================================
// 8. BOOKING
// ==========================================

class WBooking {

    private final WBookingId bookingId;
    private final WCustomerId customerId;
    private final WParkingSpotId spotId;
    private final String vehicleNumber;
    private final WMoney fare;

    private WBookingStatus status;
    private WPayment payment;

    public WBooking(
        WBookingId bookingId,
        WCustomerId customerId,
        WParkingSpotId spotId,
        String vehicleNumber,
        WMoney fare
    ) {

        if (bookingId == null ||
            customerId == null ||
            spotId == null ||
            vehicleNumber == null ||
            vehicleNumber.isBlank() ||
            fare == null) {

            throw new IllegalArgumentException(
                "Invalid booking details"
            );
        }

        this.bookingId = bookingId;
        this.customerId = customerId;
        this.spotId = spotId;
        this.vehicleNumber = vehicleNumber;
        this.fare = fare;

        this.status = WBookingStatus.PENDING;
        this.payment = null;
    }

    // Payment must be successful before confirmation.

    public void attachPayment(WPayment payment) {

        if (status != WBookingStatus.PENDING) {
            throw new IllegalStateException(
                "Payment can only be attached to a pending booking"
            );
        }

        if (payment == null || !payment.successful()) {
            throw new IllegalArgumentException(
                "Payment was unsuccessful"
            );
        }

        if (!payment.bookingId().equals(bookingId)) {
            throw new IllegalArgumentException(
                "Payment belongs to another booking"
            );
        }

        if (!payment.amount().equals(fare)) {
            throw new IllegalArgumentException(
                "Payment amount does not match booking fare"
            );
        }

        this.payment = payment;

        this.status = WBookingStatus.CONFIRMED;
    }

    // Only confirmed bookings can be cancelled.

    public void cancel() {

        if (status != WBookingStatus.CONFIRMED) {
            throw new IllegalStateException(
                "Only confirmed bookings can be cancelled"
            );
        }

        this.status = WBookingStatus.CANCELLED;
    }

    public WBookingId getBookingId() {
        return bookingId;
    }

    public WCustomerId getCustomerId() {
        return customerId;
    }

    public WParkingSpotId getSpotId() {
        return spotId;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public WMoney getFare() {
        return fare;
    }

    public WBookingStatus getStatus() {
        return status;
    }

    public WPayment getPayment() {
        return payment;
    }
}

// ==========================================
// 9. WAYPOINT APPLICATION
// ==========================================

class Waypoint {

    private final Map<WCustomerId, WCustomer> customers =
        new HashMap<>();

    private final Map<WParkingSpotId, WParkingSpot> spots =
        new HashMap<>();

    private final Map<WBookingId, WBooking> bookings =
        new HashMap<>();

    private final Set<String> registeredVehicles =
        new HashSet<>();

    private int bookingCounter = 1;
    private int paymentCounter = 1;

    // Register customer

    public void registerCustomer(WCustomer customer) {

        if (customer == null) {
            throw new IllegalArgumentException(
                "Customer cannot be null"
            );
        }

        if (customers.containsKey(customer.getCustomerId())) {
            throw new IllegalArgumentException(
                "Customer already registered"
            );
        }

        customers.put(customer.getCustomerId(), customer);
    }

    // Add parking spot

    public void addParkingSpot(WParkingSpot spot) {

        if (spot == null) {
            throw new IllegalArgumentException(
                "Parking spot cannot be null"
            );
        }

        WParkingSpotId id = spot.getSnapshot().spotId();

        if (spots.containsKey(id)) {
            throw new IllegalArgumentException(
                "Parking spot already exists"
            );
        }

        spots.put(id, spot);
    }

    // Register vehicle

    public void registerVehicle(String vehicleNumber) {

        if (vehicleNumber == null ||
            vehicleNumber.isBlank()) {

            throw new IllegalArgumentException(
                "Invalid vehicle number"
            );
        }

        String normalized =
            vehicleNumber.trim().toUpperCase(Locale.ROOT);

        if (!registeredVehicles.add(normalized)) {
            throw new IllegalArgumentException(
                "Vehicle already registered"
            );
        }
    }

    // Display parking spots

    public List<WParkingSpotView> getParkingSpots() {

        List<WParkingSpotView> result = new ArrayList<>();

        for (WParkingSpot spot : spots.values()) {
            result.add(spot.getSnapshot());
        }

        return List.copyOf(result);
    }

    // Reserve parking spot

    public WBooking reserve(
        WCustomerId customerId,
        WParkingSpotId spotId,
        String vehicleNumber,
        int hours,
        WMoney hourlyRate
    ) {

        if (!customers.containsKey(customerId)) {
            throw new IllegalArgumentException(
                "Customer not found"
            );
        }

        if (hours <= 0) {
            throw new IllegalArgumentException(
                "Booking hours must be greater than zero"
            );
        }

        if (hourlyRate == null) {
            throw new IllegalArgumentException(
                "Hourly rate cannot be null"
            );
        }

        String normalizedVehicle =
            vehicleNumber == null
                ? ""
                : vehicleNumber.trim().toUpperCase(Locale.ROOT);

        if (!registeredVehicles.contains(normalizedVehicle)) {
            throw new IllegalArgumentException(
                "Vehicle is not registered"
            );
        }

        WParkingSpot spot = spots.get(spotId);

        if (spot == null) {
            throw new IllegalArgumentException(
                "Parking spot not found"
            );
        }

        if (!spot.isAvailable()) {
            throw new IllegalStateException(
                "Parking spot is not available"
            );
        }

        WMoney totalFare = hourlyRate.times(hours);

        WBookingId bookingId =
            new WBookingId(bookingCounter++);

        WPaymentId paymentId =
            new WPaymentId(paymentCounter++);

        boolean vehicleParked = false;

        try {

            // Step 1: Reserve parking spot.

            spot.parkVehicle(normalizedVehicle);
            vehicleParked = true;

            // Step 2: Create pending booking.

            WBooking booking = new WBooking(
                bookingId,
                customerId,
                spotId,
                normalizedVehicle,
                totalFare
            );

            // Step 3: Simulate successful payment.

            WPayment payment = new WPayment(
                paymentId,
                bookingId,
                totalFare,
                true
            );

            // Step 4: Attach payment.

            booking.attachPayment(payment);

            // Step 5: Store confirmed booking.

            bookings.put(bookingId, booking);

            return booking;

        } catch (RuntimeException exception) {

            // Roll back parking spot if booking fails.

            if (vehicleParked) {
                spot.releaseSpot();
            }

            throw exception;
        }
    }

    // Cancel booking

    public void cancelBooking(WBookingId bookingId) {

        WBooking booking = bookings.get(bookingId);

        if (booking == null) {
            throw new IllegalArgumentException(
                "Booking not found"
            );
        }

        booking.cancel();

        WParkingSpot spot = spots.get(booking.getSpotId());

        spot.releaseSpot();
    }

    // Display all bookings

    public void displayBookings() {

        System.out.println("\n----- BOOKING DETAILS -----");

        for (WBooking booking : bookings.values()) {

            System.out.println(
                "Booking ID: " + booking.getBookingId().value()
            );

            System.out.println(
                "Customer ID: " + booking.getCustomerId().value()
            );

            System.out.println(
                "Parking Spot ID: " + booking.getSpotId().value()
            );

            System.out.println(
                "Vehicle Number: " + booking.getVehicleNumber()
            );

            System.out.println(
                "Fare: " + booking.getFare()
            );

            System.out.println(
                "Booking Status: " + booking.getStatus()
            );

            System.out.println("---------------------------");
        }
    }
}

// ==========================================
// 10. MAIN CLASS
// ==========================================

public class WaypointDemo {

    public static void main(String[] args) {

        Waypoint waypoint = new Waypoint();

        // Step 1: Register customer

        WCustomer customer = new WCustomer(
            new WCustomerId(101),
            "Ramya"
        );

        waypoint.registerCustomer(customer);

        System.out.println("Customer registered successfully.");

        // Step 2: Add parking spots

        WParkingSpot spot1 = new WParkingSpot(
            new WParkingSpotId(201),
            "Bachupally"
        );

        WParkingSpot spot2 = new WParkingSpot(
            new WParkingSpotId(202),
            "Nizampet"
        );

        waypoint.addParkingSpot(spot1);
        waypoint.addParkingSpot(spot2);

        System.out.println("Parking spots added successfully.");

        // Step 3: Register vehicle

        waypoint.registerVehicle("TS09AB1234");

        System.out.println("Vehicle registered successfully.");

        // Step 4: Display parking availability

        System.out.println("\n----- PARKING AVAILABILITY -----");

        for (WParkingSpotView view :
             waypoint.getParkingSpots()) {

            System.out.println(
                "Spot ID: " + view.spotId().value()
            );

            System.out.println(
                "Location: " + view.location()
            );

            System.out.println(
                "Status: " + view.status()
            );

            System.out.println("---------------------------");
        }

        // Step 5: Book a parking spot

        System.out.println("\n----- BOOKING PARKING -----");

        WBooking booking = waypoint.reserve(
            new WCustomerId(101),
            new WParkingSpotId(201),
            "TS09AB1234",
            2,
            new WMoney(5000, "INR")
        );

        System.out.println(
            "Booking ID: " + booking.getBookingId().value()
        );

        System.out.println(
            "Total Fare: " + booking.getFare()
        );

        System.out.println(
            "Payment Status: " +
            booking.getPayment().successful()
        );

        System.out.println(
            "Booking Status: " + booking.getStatus()
        );

        // Step 6: Try booking the same spot

        System.out.println(
            "\n----- TRYING DUPLICATE BOOKING -----"
        );

        try {

            waypoint.reserve(
                new WCustomerId(101),
                new WParkingSpotId(201),
                "TS09AB1234",
                1,
                new WMoney(5000, "INR")
            );

        } catch (IllegalStateException exception) {

            System.out.println(
                "Booking failed: " + exception.getMessage()
            );
        }

        // Step 7: Cancel booking

        System.out.println("\n----- CANCELLING BOOKING -----");

        waypoint.cancelBooking(booking.getBookingId());

        System.out.println(
            "Booking Status After Cancellation: " +
            booking.getStatus()
        );

        // Step 8: Display parking availability

        System.out.println(
            "\n----- PARKING AVAILABILITY AFTER CANCELLATION -----"
        );

        for (WParkingSpotView view :
             waypoint.getParkingSpots()) {

            System.out.println(
                "Spot ID: " + view.spotId().value()
            );

            System.out.println(
                "Location: " + view.location()
            );

            System.out.println(
                "Status: " + view.status()
            );

            System.out.println("---------------------------");
        }

        // Step 9: Display all bookings

        waypoint.displayBookings();
    }
}
