package de.uniba.wiai.ktr.sound;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;






public class Mp3Player {
	
	

	public static void main(String args[])  {
		String filename = "mp3/JSA.mp3";	
		File file = new File(filename);
		InputStream is;
		try {
			is = new FileInputStream(file);
			AdvancedPlayer pl = new AdvancedPlayer(is);
			pl.play();
		} catch (FileNotFoundException | JavaLayerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	
}