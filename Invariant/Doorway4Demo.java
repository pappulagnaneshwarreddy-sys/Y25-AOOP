
package Invariant;

import java.util.HashSet;
import java.util.Set;

// ==========================================
// 1. DEFAULT EQUALITY
// No equals() and hashCode()
// ==========================================

class DefaultSpot {

    private final int spotId;

    DefaultSpot(int spotId) {
        this.spotId = spotId;
    }
}

// ==========================================
// 2. HALF EQUALITY
// equals() without hashCode()
// ==========================================

class HalfSpot {

    private final int spotId;

    HalfSpot(int spotId) {
        this.spotId = spotId;
    }

    @Override
    public boolean equals(Object o) {

        return o instanceof HalfSpot other
                && other.spotId == spotId;
    }
}

// ==========================================
// 3. MUTABLE EQUALITY
// Equality based on changing state
// ==========================================

class MutableEqSpot {

    private final int spotId;

    private boolean available = true;

    MutableEqSpot(int spotId) {
        this.spotId = spotId;
    }

    void park() {
        available = false;
    }

    @Override
    public boolean equals(Object o) {

        return o instanceof MutableEqSpot other
                && other.available == available;
    }

    @Override
    public int hashCode() {
        return Boolean.hashCode(available);
    }
}

// ==========================================
// 4. GOOD EQUALITY
// Identity field and consistent hashCode()
// ==========================================

class GoodSpot {

    private final int spotId;

    GoodSpot(int spotId) {
        this.spotId = spotId;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;

        return o instanceof GoodSpot other
                && spotId == other.spotId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(spotId);
    }
}

// ==========================================
// 5. MAIN CLASS
// ==========================================

public class Doorway4Demo {

    public static void main(String[] args) {

        // Default equality

        Set<DefaultSpot> a = new HashSet<>();

        a.add(new DefaultSpot(101));
        a.add(new DefaultSpot(101));

        System.out.println(
            "Default equality : size=" + a.size()
            + " (same spot booked twice)"
        );

        // equals without hashCode

        Set<HalfSpot> b = new HashSet<>();

        b.add(new HalfSpot(101));

        System.out.println(
            "equals w/o hashCode: contains="
            + b.contains(new HalfSpot(101))
            + " (almost always false)"
        );

        // Mutable equality

        Set<MutableEqSpot> c = new HashSet<>();

        MutableEqSpot s = new MutableEqSpot(101);

        c.add(s);

        s.park();

        System.out.println(
            "Mutable equality : contains="
            + c.contains(s)
            + " (object lost)"
        );

        // Correct identity equality

        Set<GoodSpot> d = new HashSet<>();

        d.add(new GoodSpot(101));
        d.add(new GoodSpot(101));

        System.out.println(
            "Identity equality : size=" + d.size()
            + " contains=" + d.contains(new GoodSpot(101))
        );
    }
}
