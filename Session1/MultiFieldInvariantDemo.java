
package Session1;

class MultiFieldParkingSpot {

    private int spotId;
    private String location;
    private boolean available;
    private String vehicleNumber;

    // Constructor
    MultiFieldParkingSpot(int spotId, String location) {

        if (spotId <= 0) {
            throw new IllegalArgumentException(
                "Invalid spot ID"
            );
        }

        if (location == null || location.isBlank()) {
            throw new IllegalArgumentException(
                "Invalid location"
            );
        }

        this.spotId = spotId;
        this.location = location;
        this.available = true;
        this.vehicleNumber = null;
    }

    // Park vehicle
    void parkVehicle(String vehicleNumber) {

        if (!available) {
            throw new IllegalStateException(
                "Parking spot is already occupied"
            );
        }

        if (vehicleNumber == null ||
            vehicleNumber.isBlank()) {

            throw new IllegalArgumentException(
                "Vehicle number cannot be empty"
            );
        }

        this.vehicleNumber = vehicleNumber;
        this.available = false;
    }

    // Release parking spot
    void releaseSpot() {

        if (available) {
            throw new IllegalStateException(
                "Parking spot is already available"
            );
        }

        this.vehicleNumber = null;
        this.available = true;
    }

    // Display details
    void displayDetails() {

        System.out.println("Spot ID: " + spotId);
        System.out.println("Location: " + location);
        System.out.println("Available: " + available);
        System.out.println("Vehicle: " + vehicleNumber);
    }
}

// Main class
public class MultiFieldInvariantDemo {

    public static void main(String[] args) {

        MultiFieldParkingSpot p1 =
            new MultiFieldParkingSpot(101, "Hyderabad");

        // Park vehicle
        p1.parkVehicle("TS09AB1234");

        System.out.println("--- Parking Details ---");
        p1.displayDetails();

        // Release parking spot
        System.out.println("\n--- Releasing Parking Spot ---");

        p1.releaseSpot();
        p1.displayDetails();

        // Test empty vehicle number
        System.out.println("\n--- Testing Invalid Vehicle ---");

        try {
            p1.parkVehicle("");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }

        // Test duplicate parking
        System.out.println("\n--- Testing Duplicate Parking ---");

        p1.parkVehicle("TS10XY1234");

        try {
            p1.parkVehicle("TS11AB5678");
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
