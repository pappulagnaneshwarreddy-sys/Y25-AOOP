
package Invariant;

import java.util.ArrayList;
import java.util.List;

// ==========================================
// 1. INTERFACE
// ==========================================

interface Describable {
    String describe();
}

// ==========================================
// 2. REGISTRY
// ==========================================

class Registry {

    void register(Describable d) {
        System.out.println("Registry sees: " + d.describe());
    }
}

// ==========================================
// 3. BROKEN: LEAKING THIS
// ==========================================

class LeakyBooking implements Describable {

    private final int bookingId;
    private String status;

    LeakyBooking(int bookingId, Registry registry) {

        // Object escapes before construction is complete
        registry.register(this);

        this.bookingId = bookingId;
        this.status = "CREATED";
    }

    @Override
    public String describe() {
        return "id=" + bookingId + ", status=" + status;
    }
}

// ==========================================
// 4. FIXED: SAFE BOOKING
// ==========================================

final class SafeBooking implements Describable {

    private final int bookingId;
    private String status;

    private SafeBooking(int bookingId) {

        this.bookingId = bookingId;
        this.status = "CREATED";
    }

    static SafeBooking create(int bookingId, Registry registry) {

        // Fully construct object first
        SafeBooking b = new SafeBooking(bookingId);

        // Publish object only after construction
        registry.register(b);

        return b;
    }

    @Override
    public String describe() {
        return "id=" + bookingId + ", status=" + status;
    }
}

// ==========================================
// 5. BROKEN: BASE BOOKING
// ==========================================

class BaseBooking {

    BaseBooking() {

        // Calls an overridable method
        init();
    }

    protected void init() {
    }
}

// ==========================================
// 6. PREMIUM BOOKING
// ==========================================

class PremiumBooking extends BaseBooking {

    private final List<String> perks = new ArrayList<>();

    @Override
    protected void init() {

        // Subclass field is not initialized yet
        perks.add("VALET");
    }
}

// ==========================================
// 7. MAIN CLASS
// ==========================================

public class Doorway5Demo {

    public static void main(String[] args) {

        Registry registry = new Registry();

        // ----------------------------------
        // BROKEN: LEAKING THIS
        // ----------------------------------

        System.out.println(
            "--- Broken: leaking this ---"
        );

        new LeakyBooking(501, registry);

        // ----------------------------------
        // FIXED: STATIC FACTORY
        // ----------------------------------

        System.out.println(
            "--- Fixed: static factory ---"
        );

        SafeBooking.create(502, registry);

        // ----------------------------------
        // BROKEN: OVERRIDABLE METHOD
        // ----------------------------------

        System.out.println(
            "--- Broken: overridable method in constructor ---"
        );

        try {

            new PremiumBooking();

        } catch (NullPointerException e) {

            System.out.println(
                "NullPointerException: subclass field was not initialised yet"
            );
        }

        // This will NOT compile because SafeBooking is final.
        // class HackedBooking extends SafeBooking { }
    }
}
