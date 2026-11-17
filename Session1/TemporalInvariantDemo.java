
package Session1;

class TemporalBooking {

    private int bookingId;
    private String status;
    private int startTime;
    private int endTime;

    // Constructor
    TemporalBooking(int bookingId, int startTime, int endTime) {

        if (bookingId <= 0) {
            throw new IllegalArgumentException(
                "Invalid booking ID"
            );
        }

        if (endTime <= startTime) {
            throw new IllegalArgumentException(
                "End time must be after start time"
            );
        }

        this.bookingId = bookingId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = "CREATED";
    }

    // Confirm booking
    void confirmBooking() {

        if (!status.equals("CREATED")) {
            throw new IllegalStateException(
                "Only created bookings can be confirmed"
            );
        }

        status = "CONFIRMED";
    }

    // Activate booking
    void activateBooking() {

        if (!status.equals("CONFIRMED")) {
            throw new IllegalStateException(
                "Only confirmed bookings can be activated"
            );
        }

        status = "ACTIVE";
    }

    // Complete booking
    void completeBooking() {

        if (!status.equals("ACTIVE")) {
            throw new IllegalStateException(
                "Only active bookings can be completed"
            );
        }

        status = "COMPLETED";
    }

    // Display booking status
    void displayStatus() {

        System.out.println("Booking ID: " + bookingId);
        System.out.println("Status: " + status);
    }
}

// Main class
public class TemporalInvariantDemo {

    public static void main(String[] args) {

        // Create booking
        TemporalBooking b1 =
            new TemporalBooking(501, 10, 12);

        // Valid booking lifecycle
        b1.confirmBooking();
        b1.activateBooking();
        b1.completeBooking();

        b1.displayStatus();

        // Test invalid activation
        try {

            b1.activateBooking();

        } catch (IllegalStateException e) {

            System.out.println(
                "Error: " + e.getMessage()
            );
        }

        // Test invalid booking time
        try {

            TemporalBooking b2 =
                new TemporalBooking(502, 12, 10);

        } catch (IllegalArgumentException e) {

            System.out.println(
                "Error: " + e.getMessage()
            );
        }

        // Test invalid booking ID
        try {

            TemporalBooking b3 =
                new TemporalBooking(-1, 10, 12);

        } catch (IllegalArgumentException e) {

            System.out.println(
                "Error: " + e.getMessage()
            );
        }
    }
}
