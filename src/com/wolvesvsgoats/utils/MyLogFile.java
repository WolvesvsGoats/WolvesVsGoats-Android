/**
 * Wolves Vs Goats by André Rosa and Fernando Alves is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 * Based on a work at http://wvg.i3portal.net.
 * 
 * Learn how to share your work with existing communities that have enabled Creative Commons licensing.
 * 
 * Creative Commons is a non-profit organization.
 * 
 * @author André Rosa
 * @author Fernando Alves
 * @version 0.1
 */
package com.wolvesvsgoats.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import android.os.Environment;

public class MyLogFile {
	static FileOutputStream f;
	static PrintStream p;
	static File dir, sdCard, log;


	public static void startLog(String filename){
		try{
			sdCard = Environment.getExternalStorageDirectory();
			dir = new File (sdCard.getAbsolutePath() + "/w_vs_g/log");
			dir.mkdirs();
			String name =  "wifi_logs.csv";
			log = new File(dir, name);

			f = new FileOutputStream(log);
			p = new PrintStream(f);	
			
		}catch(Exception e){
			//do what?
		}
	}
	
	public static void writeLog(String toWrite){
		try{
			p.println(toWrite);
		}catch(Exception e){
			
		}
	}
		
	public static void closeFiles(){
		try{
			if(p != null){
				p.close();
			}
			if(f != null){
				f.close();
			}
		}catch(IOException e){
			
		}
	}
	
	public static String getDir(){
		if(dir != null){
			return dir.getAbsolutePath();
		}
		return "ta a null";
	}
	
	public static String getSdCard(){
		if(sdCard != null){
			return sdCard.toString();
		}
		return "SDCARD = ta a null";
	}
}
