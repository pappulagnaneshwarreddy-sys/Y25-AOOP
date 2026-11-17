
package DefensiveCopying;

import java.util.*;

class ParkingArea {

    private final List<String> spots;

    ParkingArea(List<String> spots) {
        this.spots = new ArrayList<>(spots);
    }

    public List<String> getSpots() {
        return List.copyOf(spots);
    }
}

public class DefensiveCopyingDemo {

    public static void main(String[] args) {

        // Original list
        List<String> original = new ArrayList<>();

        original.add("P101");
        original.add("P102");

        // Create ParkingArea object
        ParkingArea area = new ParkingArea(original);

        // Modify original list
        original.add("P103");

        System.out.println("Original List: " + original);

        System.out.println("Parking Area List: " + area.getSpots());

        // Try to modify returned list
        try {
            area.getSpots().add("P104");
        } catch (UnsupportedOperationException e) {
            System.out.println("Cannot modify the parking area list.");
        }

        System.out.println("Final Parking Area List: " + area.getSpots());
    }
}
