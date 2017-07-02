/**
 * Wolves Vs Goats by Andr� Rosa and Fernando Alves is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 * Based on a work at http://wvg.i3portal.net.
 * <p>
 * Learn how to share your work with existing communities that have enabled Creative Commons licensing.
 * <p>
 * Creative Commons is a non-profit organization.
 *
 * @author Andr� Rosa
 * @author Fernando Alves
 * @version 0.1
 */
package com.wolvesvsgoats.data;

import java.io.Serializable;

public class Player implements Serializable {

    public static final int WOLF = 0;
    public static final int GOAT = 1;
    private static final long serialVersionUID = -5784477732660877676L;
    private String name;
    private String ip;
    private boolean detector;
    private int faction;
    private int weapon;
    private boolean trapAvailable;
    private String bluetoothMac;

    public Player(String name, String ip, boolean detec, int fac, String macB) {
        this.name = name;
        this.ip = ip;
        this.detector = detec;
        this.faction = fac;
        this.bluetoothMac = macB;
        trapAvailable = false;
        weapon = 0;
    }

    public Player(String name, String ip, String macB) {
        this.name = name;
        this.ip = ip;
        this.bluetoothMac = macB;
        trapAvailable = false;
    }

    public String getBluetoothMac() {
        return bluetoothMac;
    }

    public boolean isTrapAvailable() {
        return trapAvailable;
    }

    public void spendTrap() {
        trapAvailable = false;
    }

    public void obtainTrap() {
        trapAvailable = true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public boolean isDetector() {
        return detector;
    }

    public void setDetector(boolean detector) {
        this.detector = detector;
    }

    public int getFaction() {
        return faction;
    }

    public void setFaction(int faction) {
        this.faction = faction;
    }

    public int getWeapon() {
        return weapon;
    }

    public void upgradeWeapon() {
        if (weapon < 6)
            this.weapon++;
    }

    public String toString() {
        return name + " " + ip;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}