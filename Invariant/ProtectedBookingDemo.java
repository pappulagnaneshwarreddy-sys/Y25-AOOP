
package Invariant;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// ==========================================
// 1. BOOKING STATUS
// ==========================================

enum BookingStatus {
    CREATED,
    CONFIRMED,
    ACTIVE,
    COMPLETED,
    CANCELLED
}

// ==========================================
// 2. PROTECTED BOOKING CLASS
// ==========================================

final class Booking {

    // Doorway 5: Final class
    // Doorway 1: Private fields

    private final int bookingId;

    // Doorway 2: Immutable types
    private final LocalDateTime start;
    private final LocalDateTime end;

    // Doorway 3: Private mutable collection
    private final List<String> notes = new ArrayList<>();

    private BookingStatus status = BookingStatus.CREATED;

    // ======================================
    // CONSTRUCTOR
    // ======================================

    public Booking(
        int bookingId,
        LocalDateTime start,
        LocalDateTime end
    ) {

        if (bookingId <= 0) {
            throw new IllegalArgumentException(
                "Invalid booking ID"
            );
        }

        if (start == null || end == null) {
            throw new IllegalArgumentException(
                "Booking dates cannot be null"
            );
        }

        if (!end.isAfter(start)) {
            throw new IllegalArgumentException(
                "End must follow start"
            );
        }

        this.bookingId = bookingId;
        this.start = start;
        this.end = end;
    }

    // ======================================
    // DOORWAY 1: BEHAVIOUR METHODS
    // ======================================

    public void confirm() {

        if (status != BookingStatus.CREATED) {
            throw new IllegalStateException(
                "Only CREATED can be confirmed"
            );
        }

        status = BookingStatus.CONFIRMED;
    }

    public void activate() {

        if (status != BookingStatus.CONFIRMED) {
            throw new IllegalStateException(
                "Only CONFIRMED can be activated"
            );
        }

        status = BookingStatus.ACTIVE;
    }

    public void complete() {

        if (status != BookingStatus.ACTIVE) {
            throw new IllegalStateException(
                "Only ACTIVE can be completed"
            );
        }

        status = BookingStatus.COMPLETED;
    }

    // ======================================
    // DOORWAY 3: COLLECTION PROTECTION
    // ======================================

    public void addNote(String note) {

        if (note == null || note.isBlank()) {
            throw new IllegalArgumentException(
                "Note required"
            );
        }

        notes.add(note);
    }

    public List<String> getNotes() {

        return List.copyOf(notes);
    }

    // ======================================
    // GETTER
    // ======================================

    public BookingStatus getStatus() {
        return status;
    }

    // ======================================
    // DOORWAY 4: EQUALS AND HASHCODE
    // ======================================

    @Override
    public boolean equals(Object o) {

        return o instanceof Booking b
            && b.bookingId == bookingId;
    }

    @Override
    public int hashCode() {

        return Integer.hashCode(bookingId);
    }

    // ======================================
    // TOSTRING
    // ======================================

    @Override
    public String toString() {

        return "Booking[" + bookingId +
            ", " + status +
            ", " + start +
            " -> " + end + "]";
    }
}

// ==========================================
// 3. MAIN CLASS
// ==========================================

public class ProtectedBookingDemo {

    public static void main(String[] args) {

        // Create booking dates

        LocalDateTime s =
            LocalDateTime.of(2026, 11, 9, 10, 0);

        // Create booking

        Booking b = new Booking(
            501,
            s,
            s.plusHours(2)
        );

        // Change booking status

        b.confirm();

        b.activate();

        // Add booking note

        b.addNote("Level 1, near lift");

        b.complete();

        // Display booking details

        System.out.println(b);

        System.out.println(
            "notes=" + b.getNotes()
        );

        // ==================================
        // TEST INVALID STATUS TRANSITION
        // ==================================

        try {

            b.activate();

        } catch (IllegalStateException e) {

            System.out.println(
                "Rejected: " + e.getMessage()
            );
        }

        // ==================================
        // TEST IMMUTABLE COLLECTION
        // ==================================

        try {

            b.getNotes().add("hack");

        } catch (UnsupportedOperationException e) {

            System.out.println(
                "Rejected: notes are read-only"
            );
        }

        // ==================================
        // TEST INVALID BOOKING DATES
        // ==================================

        try {

            new Booking(
                502,
                s,
                s.minusHours(1)
            );

        } catch (IllegalArgumentException e) {

            System.out.println(
                "Rejected: " + e.getMessage()
            );
        }
    }
}
