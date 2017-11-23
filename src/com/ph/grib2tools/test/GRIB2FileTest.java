package com.ph.grib2tools.test;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.ph.grib2tools.grib2file.GribFile;
import com.ph.grib2tools.grib2file.GribSection1;
import com.ph.grib2tools.grib2file.RandomAccessGribFile;
import com.ph.grib2tools.grib2file.griddefinition.GridDefinitionTemplate30;
import com.ph.grib2tools.grib2file.productdefinition.ProductDefinitionTemplate40;

public class GRIB2FileTest {

	public static void main(String[] args) {
		
		if (args.length < 1) {
			System.out.println("Syntax: java GRIB2FileTest <filename> <structureid (optional)>");
			return;
		}

		// Name of the GRIB2 file
		String filename = args[0];
		
		// Defines how many GRIB file structures shall be skipped when reading the GRIB2 file. This
		// is useful since some organizations put several GRIB2 file structures in one file.
		int numskip;
		try {
			numskip = Integer.parseInt(args[1]);
		} catch (Exception e) {
			numskip = 0;
		}
				
		// Id of the grid within the GRIBF2 file, since GRIB2 files can contain several grids (Sections 5-7)
		int gridid = 0;
		
		System.out.println("Reading file " + filename + ", file structure " + numskip + ":");
		try {

			// Open GRIB2 file and read it
			InputStream inputstream;
			inputstream = Files.newInputStream(Paths.get(filename));
			RandomAccessGribFile gribFile = new RandomAccessGribFile("testdata", filename);
			gribFile.importFromStream(inputstream, numskip);
			
			// Get identification information
			GribSection1 section1 = gribFile.getSection1();
			System.out.println("Date: " + String.format("%02d", section1.day) + "." + String.format("%02d", section1.month) + "." + section1.year);
			System.out.println("Time: " + String.format("%02d", section1.hour) + ":" + String.format("%02d", section1.minute) + "." + String.format("%02d", section1.second));
			System.out.println("Generating centre: " + section1.generatingCentre);
			// Get product information
			ProductDefinitionTemplate40 productDefinition = (ProductDefinitionTemplate40)gribFile.getProductDefinitionTemplate();
			System.out.println("Forecast time: " + productDefinition.forecastTime);
			System.out.println("Parameter category: " + productDefinition.parameterCategory);
			System.out.println("Parameter number: " + productDefinition.parameterNumber);		

			// Get grid information
			GridDefinitionTemplate30 gridDefinition = (GridDefinitionTemplate30)gribFile.getGridDefinitionTemplate();
			System.out.println("Covered area:");
			System.out.println("   from (latitude, longitude): " + GribFile.unitsToDeg(gridDefinition.firstPointLat) + ", " + GribFile.unitsToDeg(gridDefinition.firstPointLon));		
			System.out.println("   to: (latitude, longitude): " + GribFile.unitsToDeg(gridDefinition.lastPointLat) + ", " + GribFile.unitsToDeg(gridDefinition.lastPointLon));		

			// Get grid data
			double latitude = 52.52;
			double longitude = 13.38;
			System.out.println("Value at (" + latitude + ", " + longitude + "): " + gribFile.interpolateValueAtLocation(gridid, latitude, longitude));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
