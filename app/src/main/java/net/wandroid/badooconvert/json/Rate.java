package net.wandroid.badooconvert.json;

/**
 * Created by zabbat on 2015-12-22.
 */
public class Rate {
    private String from;
    private String to;
    private String rate;

    public Rate(String from, String to, String rate) {
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

    public String getRate() {
        return rate;
    }
}
