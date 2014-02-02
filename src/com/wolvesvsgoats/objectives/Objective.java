/**
 * Wolves Vs Goats by Andr� Rosa and Fernando Alves is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 * Based on a work at http://wvg.i3portal.net.
 * 
 * Learn how to share your work with existing communities that have enabled Creative Commons licensing.
 * 
 * Creative Commons is a non-profit organization.
 * 
 * @author Andr� Rosa
 * @author Fernando Alves
 * @version 0.1
 */
package com.wolvesvsgoats.objectives;

import java.io.Serializable;

import com.wolvesvsgoats.utils.Position;

public class Objective implements Serializable {

	private static final long serialVersionUID = -7888833720729073269L;

	public static final int SIMPLE = 0;
	public static final int GET_WEAPON = 1;
	public static final int TRAP = 2;
	public static final int CHINAR = 3;

	private Position position;
	private int type;
	private boolean faction;

	public Objective(Position pos, String qr, int t, boolean f) {
		position = pos;
		type = t;
		faction = f;
	}

	public Objective(Position pos, int t, boolean f) {
		position = pos;
		type = t;
		faction = f;
	}

	public boolean isFaction() {
		return faction;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String toString() {
		// switch stronk here
		switch (type) {
			case SIMPLE:
				return "Control point";
			case GET_WEAPON:
				return "Improve arsenal";
			case TRAP:
				return "Set a trap";
			case CHINAR:
				return "Eliminate an adversary";
			default:return "";
		}
	}

}
