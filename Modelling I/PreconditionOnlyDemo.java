public class PreconditionOnlyDemo {

    public void reserveParking(String vehicleNumber, int hours) {
        if (vehicleNumber == null || vehicleNumber.isBlank()) {
            throw new IllegalArgumentException("Invalid vehicle number");
        }
        if (hours <= 0) {
            throw new IllegalArgumentException("Hours must be positive");
        }
        System.out.println("Parking reserved");
    }

    public static void main(String[] args) {
        PreconditionOnlyDemo demo = new PreconditionOnlyDemo();
        demo.reserveParking("TS09AB1234", 2);
    }
}