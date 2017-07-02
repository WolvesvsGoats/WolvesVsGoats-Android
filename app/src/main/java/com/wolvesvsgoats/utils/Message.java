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
package com.wolvesvsgoats.utils;

import java.io.Serializable;
import java.util.ArrayList;

public class Message implements Serializable {

    public static final int NEW_GAME = 0;
    public static final int START_GAME = 1;
    public static final int END_GAME = 2;
    public static final int ADD_PLAYER = 3;
    public static final int ELIMINATE_PLAYER = 4;
    public static final int SET_OBJECTIVE = 5;
    public static final int COMPLETE_GOAT_OBJECTIVE = 6;
    public static final int COMPLETE_WOLF_OBJECTIVE = 7;
    public static final int COMPLETE_GLOBAL_OBJECTIVE = 8;
    public static final int SET_OBJECTIVE_PLACE = 9;
    public static final int LIST_GAMES = 10;
    public static final int JOIN_GAME = 11;
    public static final int OBJECTIVE_PLACES = 12;
    public static final int GET_NAME_BY_BMAC = 13;
    public static final int LIST_OBJECTIVE = 14;
    public static final int PROCESS_QRCODE = 15;
    public static final int UPDATE = 16;
    public static final int CHINAR = 17;
    public static final int REGISTER_PLAYER = 18;
    public static final int UPGRADE_WEAPON = 19;
    public static final int UPGRADE_STATUS = 20;
    // audio
    public static final int NEARBY_GOAT = 20;
    public static final int NEARBY_WOLF = 21;
    private static final long serialVersionUID = -4001063516563993337L;
    private int type;
    private ArrayList<Object> elements;

    public Message(int t) {
        type = t;
        elements = new ArrayList<Object>();
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public ArrayList<Object> getElements() {
        return elements;
    }

    public void setElements(ArrayList<Object> elements) {
        this.elements = elements;
    }

    public void addElement(Object e) {
        this.elements.add(e);
    }

    public Object getElement() {
        return elements.get(0);
    }

    public Object getElement(int index) {
        return elements.get(index);
    }

}
