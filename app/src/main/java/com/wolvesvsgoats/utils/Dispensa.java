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

import android.content.Context;

import com.wolvesvsgoats.data.Player;
import com.wolvesvsgoats.objectives.Objective;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class Dispensa {
    public static final ReentrantLock commLock = new ReentrantLock();
    public static Socket socket;
    public static ObjectInputStream inStream;
    public static ObjectOutputStream outStream;
    public static Player player;
    public static List<Objective> objectives = new LinkedList<Objective>();
    public static Context ctx;
}
