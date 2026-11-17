
package Session1;

class ManagedParkingSpot {

    private final int spotId;
    private final String location;

    private boolean available;
    private String vehicleNumber;

    // Constructor
    public ManagedParkingSpot(int spotId, String location) {

        if (spotId <= 0) {
            throw new IllegalArgumentException(
                "Invalid Parking Spot ID"
            );
        }

        if (location == null || location.isBlank()) {
            throw new IllegalArgumentException(
                "Location is required"
            );
        }

        this.spotId = spotId;
        this.location = location;
        this.available = true;
        this.vehicleNumber = null;
    }

    // Park vehicle
    public void parkVehicle(String vehicleNumber) {

        if (!available) {
            throw new IllegalStateException(
                "Parking spot is already occupied"
            );
        }

        if (vehicleNumber == null ||
            vehicleNumber.isBlank()) {

            throw new IllegalArgumentException(
                "Vehicle number is required"
            );
        }

        this.vehicleNumber = vehicleNumber;
        this.available = false;
    }

    // Release parking spot
    public void releaseSpot() {

        if (available) {
            throw new IllegalStateException(
                "Parking spot is already free"
            );
        }

        this.vehicleNumber = null;
        this.available = true;
    }

    // Display details
    public void displayDetails() {

        System.out.println("Spot ID: " + spotId);
        System.out.println("Location: " + location);
        System.out.println("Available: " + available);
        System.out.println("Vehicle: " + vehicleNumber);
        System.out.println("------------------------");
    }
}

// Main class
public class ParkingManagementDemo {

    public static void main(String[] args) {

        // Create three parking spot objects

        ManagedParkingSpot p1 =
            new ManagedParkingSpot(101, "Hyderabad");

        ManagedParkingSpot p2 =
            new ManagedParkingSpot(102, "Bachupally");

        ManagedParkingSpot p3 =
            new ManagedParkingSpot(103, "Nizampet");

        // Park vehicles

        p1.parkVehicle("TS09AB1234");
        p2.parkVehicle("TS09CD5678");

        // Display all parking spots

        System.out.println("PARKING MANAGEMENT SYSTEM");

        p1.displayDetails();
        p2.displayDetails();
        p3.displayDetails();

        // Release parking spot

        System.out.println("Releasing Spot 101");

        p1.releaseSpot();

        p1.displayDetails();

        // Test duplicate parking

        try {
            p2.parkVehicle("TS10XY1234");
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
