package com.sifast.appsocle.models;

/**
 * Created by Asus on 14/06/2016.
 */
public class PointOfSale {
    private Location location;

    public PointOfSale(Location location) {

        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
