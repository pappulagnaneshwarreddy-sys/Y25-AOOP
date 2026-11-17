
package Invariant;

import java.util.ArrayList;
import java.util.List;

// ==========================================
// 1. BAD WALLET - EXPOSED MUTABLE LIST
// ==========================================

class BadWallet {

    private long balance;

    private final List<Long> credits = new ArrayList<>();

    public void credit(long amount) {

        if (amount <= 0)
            throw new IllegalArgumentException(
                "Amount must be positive"
            );

        balance += amount;
        credits.add(amount);
    }

    // Returns internal list directly
    public List<Long> getCredits() {
        return credits;
    }

    public boolean isConsistent() {

        return balance ==
            credits.stream()
                   .mapToLong(Long::longValue)
                   .sum();
    }
}

// ==========================================
// 2. SAFE WALLET - IMMUTABLE SNAPSHOT
// ==========================================

class SafeWallet {

    private long balance;

    private final List<Long> credits = new ArrayList<>();

    public void credit(long amount) {

        if (amount <= 0)
            throw new IllegalArgumentException(
                "Amount must be positive"
            );

        balance += amount;
        credits.add(amount);
    }

    // Returns immutable copy
    public List<Long> getCredits() {
        return List.copyOf(credits);
    }

    public boolean isConsistent() {

        return balance ==
            credits.stream()
                   .mapToLong(Long::longValue)
                   .sum();
    }
}

// ==========================================
// 3. PARKING LOT - COPY ON INPUT
// ==========================================

class ParkingLot {

    private final List<String> spotIds;

    public ParkingLot(List<String> spotIds) {

        // Never keep the caller's original list
        this.spotIds = new ArrayList<>(spotIds);
    }

    public int size() {
        return spotIds.size();
    }
}

// ==========================================
// 4. MAIN CLASS
// ==========================================

public class Doorway3Demo {

    public static void main(String[] args) {

        // ----------------------------------
        // BROKEN: EXPOSED LIST
        // ----------------------------------

        System.out.println("--- Broken: exposed list ---");

        BadWallet bad = new BadWallet();

        bad.credit(500);

        // Attack: Clear transaction history
        bad.getCredits().clear();

        System.out.println(
            "consistent=" + bad.isConsistent()
        );

        // ----------------------------------
        // FIXED: LIST.COPYOF
        // ----------------------------------

        System.out.println(
            "\n--- Fixed: List.copyOf ---"
        );

        SafeWallet safe = new SafeWallet();

        safe.credit(500);

        try {

            // Attempt to modify immutable list
            safe.getCredits().clear();

        } catch (UnsupportedOperationException e) {

            System.out.println(
                "Blocked: list is read-only"
            );
        }

        System.out.println(
            "consistent=" + safe.isConsistent()
            + " credits=" + safe.getCredits()
        );

        // ----------------------------------
        // COPY ON INPUT
        // ----------------------------------

        System.out.println(
            "\n--- Copy on input ---"
        );

        List<String> mine =
            new ArrayList<>(List.of("A1", "A2"));

        ParkingLot lot = new ParkingLot(mine);

        // Caller modifies original list
        mine.add("A3");

        System.out.println(
            "lot size=" + lot.size() + " (still 2)"
        );
    }
}
