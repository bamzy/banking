package ir.samatco.eft.tms.entity;

/**
 * Created by Bamdad Aghili on 4/24/2014.
 */
public class Duration {
    private long from;
    private long to;

    public long getFrom() {
        return from;
    }

    public void setFrom(long from) {
        this.from = from;
    }

    public long getTo() {
        return to;
    }

    public void setTo(long to) {
        this.to = to;
    }

    public Duration(long from, long to) {
        this.from = from;
        this.to = to;
    }
}
