
package DefensiveCopying;

import java.util.*;

public class CollectionsDemo {

    public static void main(String[] args) {

        // 1. LIST
        List<String> spots = new ArrayList<>();

        spots.add("P101");
        spots.add("P102");
        spots.add("P103");

        System.out.println("LIST: " + spots);


        // 2. SET
        Set<String> vehicles = new HashSet<>();

        vehicles.add("TS09AB1234");
        vehicles.add("TS09CD5678");
        vehicles.add("TS09AB1234");

        System.out.println("SET: " + vehicles);


        // 3. MAP
        Map<Integer, String> bookings = new HashMap<>();

        bookings.put(101, "Confirmed");
        bookings.put(102, "Pending");
        bookings.put(103, "Cancelled");

        System.out.println("MAP: " + bookings);


        // 4. QUEUE
        Queue<String> waiting = new LinkedList<>();

        waiting.add("Customer1");
        waiting.add("Customer2");
        waiting.add("Customer3");

        System.out.println("QUEUE: " + waiting);


        // 5. MUTABLE COLLECTION
        List<String> mutableSpots = new ArrayList<>();

        mutableSpots.add("P101");
        mutableSpots.add("P102");
        mutableSpots.remove("P101");

        System.out.println("MUTABLE LIST: " + mutableSpots);


        // 6. IMMUTABLE COLLECTION
        List<String> immutableSpots =
                List.of("P101", "P102");

        System.out.println("IMMUTABLE LIST: " + immutableSpots);

        // immutableSpots.add("P103");
        // UnsupportedOperationException
    }
}
