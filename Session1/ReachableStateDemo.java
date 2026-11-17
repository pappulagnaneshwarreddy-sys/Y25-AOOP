package Session1;

class ReachableParkingSpot {
    private final int spotId;
    private boolean available;
    private String vehicleNumber;

    ReachableParkingSpot(int spotId) {
        if (spotId <= 0) {
            throw new IllegalArgumentException("Invalid spot ID");
        }

        this.spotId = spotId;
        this.available = true;
        this.vehicleNumber = null;
    }

    void parkVehicle(String number) {
        if (!available) {
            throw new IllegalStateException(
                "Parking spot is already occupied"
            );
        }

        if (number == null || number.isBlank()) {
            throw new IllegalArgumentException(
                "Vehicle number is required"
            );
        }

        vehicleNumber = number;
        available = false;
    }

    void releaseSpot() {
        if (available) {
            throw new IllegalStateException(
                "Parking spot is already available"
            );
        }

        vehicleNumber = null;
        available = true;
    }

    void displayDetails() {
        System.out.println("Spot ID: " + spotId);
        System.out.println("Available: " + available);
        System.out.println("Vehicle: " + vehicleNumber);
        System.out.println("--------------------");
    }
}

public class ReachableStateDemo {
    public static void main(String[] args) {
        ReachableParkingSpot spot =
            new ReachableParkingSpot(101);

        // Initial valid state
        spot.displayDetails();

        // Move to occupied state
        spot.parkVehicle("TS09AB1234");
        spot.displayDetails();

        // Return to available state
        spot.releaseSpot();
        spot.displayDetails();

        // Try an invalid operation
        try {
            spot.releaseSpot();
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}