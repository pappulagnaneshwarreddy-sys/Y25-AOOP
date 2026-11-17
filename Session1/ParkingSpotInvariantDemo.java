
package Session1;
class SafeParkingSpot {

    private int spotId;
    private String vehicleNumber;
    private boolean occupied;

    // Constructor with validation
    SafeParkingSpot(int spotId) {

        if (spotId <= 0) {
            throw new IllegalArgumentException(
                "Invalid Parking Spot ID"
            );
        }

        this.spotId = spotId;
        this.occupied = false;
    }

    // Park vehicle
    public void parkVehicle(String vehicleNumber) {

        if (occupied) {
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
        occupied = true;
    }

    // Display details
    public void display() {

        System.out.println("Spot ID: " + spotId);
        System.out.println("Vehicle: " + vehicleNumber);
        System.out.println("Occupied: " + occupied);
    }
}

public class ParkingSpotInvariantDemo {

    public static void main(String[] args) {

        SafeParkingSpot p1 = new SafeParkingSpot(101);

        p1.parkVehicle("TS08AB1234");

        p1.display();

        // Test invalid parking operation
        try {
            p1.parkVehicle("TS09CD5678");
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }

        // Test invalid vehicle number
        SafeParkingSpot p2 = new SafeParkingSpot(102);

        try {
            p2.parkVehicle("");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }

        // Test invalid parking spot ID
        try {
            SafeParkingSpot p3 = new SafeParkingSpot(-101);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
