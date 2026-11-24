public class CallerSubstitutabilityDemo {

    static class ParkingSpot {
        public void parkVehicle(String vehicle) {
            System.out.println("Vehicle parked: " + vehicle);
        }
    }

    static class CarParkingSpot extends ParkingSpot {}
    static class BikeParkingSpot extends ParkingSpot {}

    public static void main(String[] args) {
        ParkingSpot[] spots = { new CarParkingSpot(), new BikeParkingSpot() };
        for (ParkingSpot spot : spots) {
            spot.parkVehicle("TS09AB1234");
        }
    }
}