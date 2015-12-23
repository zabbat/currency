package net.wandroid.badooconvert.json;

import java.io.Serializable;

/**
 * Describes a transaction
 */
public class Transaction implements Serializable {

    /**
     * Sku of the product
     */
    private String sku;
    /**
     * the amount of the product transacted
     */
    private double amount;
    /**
     * Currency used for this transaction (Ex USD)
     */
    private String currency;

    public Transaction(String sku, double amount, String currency) {
        this.sku = sku;
        this.amount = amount;
        this.currency = currency;
    }

    public String getSku() {
        return sku;
    }

    public double getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }
}
