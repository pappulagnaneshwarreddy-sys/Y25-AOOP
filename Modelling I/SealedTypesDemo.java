public class SealedTypesDemo {

    sealed static class ParkingSpot permits CarParkingSpot, BikeParkingSpot, ElectricParkingSpot {
        public void display() {
            System.out.println("Parking spot");
        }
    }

    static final class CarParkingSpot extends ParkingSpot {
        @Override
        public void display() {
            System.out.println("Car Parking Spot");
        }
    }

    static final class BikeParkingSpot extends ParkingSpot {
        @Override
        public void display() {
            System.out.println("Bike Parking Spot");
        }
    }

    static final class ElectricParkingSpot extends ParkingSpot {
        @Override
        public void display() {
            System.out.println("Electric Vehicle Parking Spot");
        }
    }

    public static void main(String[] args) {
        ParkingSpot car = new CarParkingSpot();
        ParkingSpot bike = new BikeParkingSpot();
        ParkingSpot electric = new ElectricParkingSpot();

        System.out.println("=== Parking Spot Details ===");
        car.display();
        bike.display();
        electric.display();
    }
}