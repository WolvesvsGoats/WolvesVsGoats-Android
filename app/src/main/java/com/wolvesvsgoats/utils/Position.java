/**
 * Wolves Vs Goats by Andre Rosa and Fernando Alves is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 * Based on a work at http://wvg.i3portal.net.
 * <p>
 * Learn how to share your work with existing communities that have enabled Creative Commons licensing.
 * <p>
 * Creative Commons is a non-profit organization.
 *
 * @author Andre Rosa
 * @author Fernando Alves
 * @version 0.1
 */
package com.wolvesvsgoats.utils;

import java.io.Serializable;

public class Position implements Serializable {

    private static final long serialVersionUID = 5452028014501937512L;

    public double lat, lng;
    public String qrString;

    public Position(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public Position(double lat, double lng, String qr) {
        this.lat = lat;
        this.lng = lng;
        qrString = qr;
    }

    public boolean hasQR() {
        return qrString != null;
    }
}
