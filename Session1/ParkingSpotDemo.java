
package Session1;
class ParkingSpotSession1 {

    int spotId;
    String location;
    boolean available;

    void displayDetails() {

        System.out.println("Spot ID: " + spotId);
        System.out.println("Location: " + location);
        System.out.println("Available: " + available);
    }
}

public class ParkingSpotDemo {

    public static void main(String[] args) {

        ParkingSpotSession1 p1 = new ParkingSpotSession1();

        p1.spotId = 101;
        p1.location = "Hyderabad";
        p1.available = true;

        p1.displayDetails();
    }
}
