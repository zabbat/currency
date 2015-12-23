package net.wandroid.badooconvert.json;

/**
 * Describes a rate object
 */
public class Rate {
    /**
     * From what currency (Ex USD)
     */
    private String from;
    /**
     * To what currency (Ex GBP)
     */
    private String to;

    /**
     * At what rate
     */
    private double rate;

    public Rate(String from, String to, double rate) {
        this.from = from;
        this.to = to;
        this.rate = rate;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public double getRate() {
        return rate;
    }

}
