
package DefensiveCopying;

class ParkingSpot {

    private String vehicleNumber;

    public void parkVehicle(String number) {

        if (number == null || number.isBlank()) {
            throw new IllegalArgumentException(
                "Vehicle number is required"
            );
        }

        if (vehicleNumber != null) {
            throw new IllegalStateException(
                "Parking spot is already occupied"
            );
        }

        vehicleNumber = number;
        System.out.println("Vehicle parked successfully.");
    }

    public void releaseSpot() {

        if (vehicleNumber == null) {
            throw new IllegalStateException(
                "Parking spot is already available"
            );
        }

        vehicleNumber = null;
        System.out.println("Parking spot released successfully.");
    }

    public boolean isAvailable() {
        return vehicleNumber == null;
    }
}

public class IllegalStatesDemo {

    public static void main(String[] args) {

        ParkingSpot spot = new ParkingSpot();

        System.out.println("Is Available: " + spot.isAvailable());

        spot.parkVehicle("TS09AB1234");

        System.out.println("Is Available: " + spot.isAvailable());

        try {
            spot.parkVehicle("TS09CD5678");
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }

        spot.releaseSpot();

        System.out.println("Is Available: " + spot.isAvailable());
    }
}
