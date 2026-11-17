
package Session1;

class SingleFieldParkingSpot {

    private int spotId;

    // Constructor with validation
    SingleFieldParkingSpot(int spotId) {

        if (spotId <= 0) {
            throw new IllegalArgumentException(
                "Spot ID must be positive"
            );
        }

        this.spotId = spotId;
    }

    // Display details
    void displayDetails() {
        System.out.println("Spot ID: " + spotId);
    }
}

public class SingleFieldInvariantDemo {

    public static void main(String[] args) {

        // Valid parking spot
        SingleFieldParkingSpot p1 =
            new SingleFieldParkingSpot(101);

        p1.displayDetails();

        // Invalid parking spot
        try {

            SingleFieldParkingSpot p2 =
                new SingleFieldParkingSpot(-101);

            p2.displayDetails();

        } catch (IllegalArgumentException e) {

            System.out.println(
                "Error: " + e.getMessage()
            );
        }

        // Test zero ID
        try {

            SingleFieldParkingSpot p3 =
                new SingleFieldParkingSpot(0);

        } catch (IllegalArgumentException e) {

            System.out.println(
                "Error: " + e.getMessage()
            );
        }
    }
}
