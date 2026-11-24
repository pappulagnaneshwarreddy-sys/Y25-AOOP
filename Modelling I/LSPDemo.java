public class LSPDemo {

    static class ParkingSpot {
        public void parkVehicle(String vehicle) {
            System.out.println("Vehicle parked: " + vehicle);
        }
    }

    static class CarParkingSpot extends ParkingSpot {
        @Override
        public void parkVehicle(String vehicle) {
            System.out.println("Car parked: " + vehicle);
        }
    }

    static class BikeParkingSpot extends ParkingSpot {
        @Override
        public void parkVehicle(String vehicle) {
            System.out.println("Bike parked: " + vehicle);
        }
    }

    public static void main(String[] args) {
        ParkingSpot spot1 = new CarParkingSpot();
        ParkingSpot spot2 = new BikeParkingSpot();

        spot1.parkVehicle("Car");
        spot2.parkVehicle("Bike");
    }
}