
package Invariant;

import java.time.LocalDateTime;
import java.util.Date;

// Broken class: Escaping References

class BadBooking {

    private final Date start;
    private final Date end;

    public BadBooking(Date start, Date end) {

        if (!end.after(start))
            throw new IllegalArgumentException(
                "End must be after start"
            );

        this.start = start;
        this.end = end;
    }

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }
}

// Fix 1: Defensive Copies

class CopyingBooking {

    private final Date start;
    private final Date end;

    public CopyingBooking(Date start, Date end) {

        Date s = new Date(start.getTime());
        Date e = new Date(end.getTime());

        if (!e.after(s))
            throw new IllegalArgumentException(
                "End must be after start"
            );

        this.start = s;
        this.end = e;
    }

    public Date getStart() {
        return new Date(start.getTime());
    }

    public Date getEnd() {
        return new Date(end.getTime());
    }
}

// Fix 2: Immutable Types

class TimeBooking {

    private final LocalDateTime start;
    private final LocalDateTime end;

    public TimeBooking(
        LocalDateTime start,
        LocalDateTime end
    ) {

        if (!end.isAfter(start))
            throw new IllegalArgumentException(
                "End must be after start"
            );

        this.start = start;
        this.end = end;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }
}

// Main class

public class Doorway2Demo {

    public static void main(String[] args) {

        System.out.println(
            "--- Broken: escape on the way out ---"
        );

        BadBooking bad = new BadBooking(
            new Date(1000),
            new Date(5000)
        );

        bad.getEnd().setTime(0);

        System.out.println(
            "start=" + bad.getStart().getTime() +
            " end=" + bad.getEnd().getTime() +
            " valid=" + bad.getEnd().after(bad.getStart())
        );

        System.out.println(
            "--- Broken: escape on the way in ---"
        );

        Date callerEnd = new Date(5000);

        BadBooking bad2 = new BadBooking(
            new Date(1000),
            callerEnd
        );

        callerEnd.setTime(0);

        System.out.println(
            "end=" + bad2.getEnd().getTime()
        );

        System.out.println(
            "--- Fix 1: defensive copies ---"
        );

        Date callerEnd2 = new Date(5000);

        CopyingBooking safe = new CopyingBooking(
            new Date(1000),
            callerEnd2
        );

        callerEnd2.setTime(0);

        safe.getEnd().setTime(0);

        System.out.println(
            "end=" + safe.getEnd().getTime()
        );

        System.out.println(
            "--- Fix 2: immutable LocalDateTime ---"
        );

        TimeBooking t = new TimeBooking(
            LocalDateTime.of(2026, 11, 9, 10, 0),
            LocalDateTime.of(2026, 11, 9, 12, 0)
        );

        System.out.println(
            "end=" + t.getEnd() +
            " (no setter exists, nothing to attack)"
        );
    }
}
