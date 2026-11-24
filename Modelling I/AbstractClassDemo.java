public class AbstractClassDemo {

    static abstract class ParkingSpot {
        protected int spotId;
        protected String location;

        public ParkingSpot(int spotId, String location) {
            this.spotId = spotId;
            this.location = location;
        }

        public void displayDetails() {
            System.out.println("Spot ID: " + spotId);
            System.out.println("Location: " + location);
        }

        abstract double calculateFare(int hours);
    }

    static class CarParkingSpot extends ParkingSpot {
        public CarParkingSpot(int spotId, String location) {
            super(spotId, location);
        }

        @Override
        double calculateFare(int hours) {
            return hours * 50.0;
        }
    }

    static class BikeParkingSpot extends ParkingSpot {
        public BikeParkingSpot(int spotId, String location) {
            super(spotId, location);
        }

        @Override
        double calculateFare(int hours) {
            return hours * 20.0;
        }
    }

    public static void main(String[] args) {
        ParkingSpot car = new CarParkingSpot(101, "Bachupally");
        ParkingSpot bike = new BikeParkingSpot(102, "Nizampet");

        System.out.println("=== Car Parking Details ===");
        car.displayDetails();
        System.out.println("Car Fare for 2 hours: Rs. " + car.calculateFare(2));

        System.out.println("\n=== Bike Parking Details ===");
        bike.displayDetails();
        System.out.println("Bike Fare for 2 hours: Rs. " + bike.calculateFare(2));
    }
}