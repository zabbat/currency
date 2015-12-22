package net.wandroid.badooconvert.json;

import java.io.Serializable;

/**
 * Created by zabbat on 2015-12-22.
 */
public class Transaction implements Serializable{

    private String sku;
    private String amount;
    private String currency;

    public Transaction(String sku, String amount, String currency) {
        this.sku = sku;
        this.amount = amount;
        this.currency = currency;
    }

    public String getSku() {
        return sku;
    }

    public String getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }
}
