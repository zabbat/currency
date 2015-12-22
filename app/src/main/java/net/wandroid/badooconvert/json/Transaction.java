package net.wandroid.badooconvert.json;

/**
 * Created by zabbat on 2015-12-22.
 */
public class Transaction {

    private String sku;
    private String amount;

    public Transaction(String sku, String amount) {
        this.sku = sku;
        this.amount = amount;
    }

    public String getSku() {
        return sku;
    }

    public String getAmount() {
        return amount;
    }
}
