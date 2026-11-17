
package DefensiveCopying;

record CustomerId(int value) {
    public CustomerId {
        if (value <= 0) {
            throw new IllegalArgumentException("Invalid Customer ID");
        }
    }
}

record BookingId(int value) {
    public BookingId {
        if (value <= 0) {
            throw new IllegalArgumentException("Invalid Booking ID");
        }
    }
}

record ParkingSpotId(int value) {
    public ParkingSpotId {
        if (value <= 0) {
            throw new IllegalArgumentException("Invalid Parking Spot ID");
        }
    }
}

public class TypedIdsDemo {

    static void cancelBooking(BookingId bookingId) {
        System.out.println(
            "Booking cancelled: " + bookingId.value()
        );
    }

    public static void main(String[] args) {

        CustomerId customerId = new CustomerId(101);
        BookingId bookingId = new BookingId(201);
        ParkingSpotId spotId = new ParkingSpotId(301);

        System.out.println("Customer ID: " + customerId.value());
        System.out.println("Booking ID: " + bookingId.value());
        System.out.println("Parking Spot ID: " + spotId.value());

        cancelBooking(bookingId);

        // cancelBooking(customerId);
        // Compilation error: Wrong ID type
    }
}
