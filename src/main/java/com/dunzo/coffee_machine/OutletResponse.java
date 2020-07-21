package com.dunzo.coffee_machine;

/**
 * Class to collect the Response of whether the outlet was able to prepare the beverage or not
 */
public class OutletResponse {
    private final String beverageName;
    private final boolean wasPrepared;

    public OutletResponse(String beverageName, boolean wasPrepared) {
        this.beverageName = beverageName;
        this.wasPrepared = wasPrepared;
    }

    public String getBeverageName() {
        return beverageName;
    }

    public boolean wasBeveragePrepared() {
        return wasPrepared;
    }
}
