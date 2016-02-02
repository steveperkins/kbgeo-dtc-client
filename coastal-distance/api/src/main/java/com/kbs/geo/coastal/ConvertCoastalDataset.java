package com.kbs.geo.coastal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ConvertCoastalDataset {

	private File file;

	public static void main(String[] args) {
		new ConvertCoastalDataset(new File("C:/Users/Steve/Downloads/netcdf-3.6.1-beta1-win32dll/dist2coast.signed.txt")).convert();
	}
	
	public ConvertCoastalDataset(File file) {
		this.file = file;
	}

	public void convert() {
		int x = 0;
		String line = null;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			while( (line = reader.readLine()) != null && x < 10) {
				/*
				 * File format is
				 * lat\tlon\tdistance_inkm
				 * where \t is a tab character
				 */
				int previousTabPosition = line.indexOf("\t");
				String lat = line.substring(0, previousTabPosition);
				
				int nextTabPosition = line.indexOf("\t", previousTabPosition + 1);
				String lon = line.substring(previousTabPosition + 1, nextTabPosition);
				
				String distance = line.substring(nextTabPosition + 1);
				
				System.out.println("Lat: " + lat + " Lon: " + lon + " Km: " + distance);
				x++;
			}
		} catch (IOException e) {
			e.printStackTrace();
			try {
				reader.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
}
