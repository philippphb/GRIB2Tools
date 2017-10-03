package com.ph.grib2tools.grib2file;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

import com.ph.grib2tools.grib2file.griddefinition.GridDefinitionTemplate30;

public abstract class GribFile implements Serializable {

	private static final long serialVersionUID = 100L;

	// Maximum number of Sections 5, 6, 7 supported
	protected final int MAXNUMSECTIONS567 = 10;

	// Conversation factor for converting GRIB file units to degree (for coordinates)
	public static int GRIB2DEGUNIT = 1000000;

	//
	protected String typeid;
	
	//
	protected String source;
	
	// Sections of the GRIB file
	protected GribSection0 section0;
	protected GribSection1 section1;
	protected GribSection2 section2;
	protected GribSection3 section3;
	protected GribSection4 section4;
	protected GribSection5 section5[];
	protected GribSection6 section6[];
	protected GribSection7 section7[];
	protected GribSection8 section8;

	// Counter for multiple Sections 5, 6, 7
	int gridcnt;

//	private static final Logger log = Logger.getLogger(GribFile.class.getName());

	
	public GribFile(String typeid, String source) {

		this.typeid = typeid;
		this.source = source;
		
		this.gridcnt = 0;
		
		section5 = new GribSection5[MAXNUMSECTIONS567];
		section6 = new GribSection6[MAXNUMSECTIONS567];
		section7 = new GribSection7[MAXNUMSECTIONS567];
	}

	// Reads all data describing structure and type of the product, the data etc. The
	// data itself is not read, also the end section (Section 8) is not considered.
	// This offers the opportunity to process the data separately and independently.
	//public void importMetadatFromStream(InputStream gribfile) {
	public void importMetadatFromStream(InputStream gribfile) throws IOException {
					
		if (gribfile == null) return;

		// Read Section 0 and verify if the data is valid GRIB/GRIB2 data
		section0 = new GribSection0(gribfile);
		if (!section0.verifyMagicNumber()) {
			System.out.println("This is not a GRIB file");
		}
		if (!section0.verifyGribVersion()) {
			System.out.println("This is not a GRIB2 file");
		}

		// Process the GRIB file
		while (true) {

			// Read the next Section of the GRIB file
			GribSection nextsection = new GribSection(gribfile).initSection();			
//if (nextsection == null) continue;
if (nextsection == null) break;
			if (nextsection.sectionnumber == 1) {
				section1 = (GribSection1)nextsection;
				section1.readData(gribfile);
			}
			else if (nextsection.sectionnumber == 2) {
				section2 = (GribSection2)nextsection;
				section2.readData(gribfile);
			}
			else if (nextsection.sectionnumber == 3) {
				section3 = (GribSection3)nextsection;
				section3.readData(gribfile);
			}
			else if (nextsection.sectionnumber == 4) {
				section4 = (GribSection4)nextsection;
				section4.readData(gribfile);
			}
			else if (nextsection.sectionnumber == 5) {
				section5[gridcnt] = (GribSection5)nextsection;
				section5[gridcnt].readData(gribfile);
			}
			else if (nextsection.sectionnumber == 6) {
				section6[gridcnt] = (GribSection6)nextsection;
				section6[gridcnt].readData(gribfile);
				break;
			}
			
			// Stop processing the GRIB file if no known section is found 
			else break;
		}
	}

	// Handle Section 8 of a GRIB file
	public void finalizeImport(InputStream gribfile) {

		// Read Section 8 and verify if the GRIB data is terminated correctly
		section8 = new GribSection8(gribfile);
		if (!section8.verifyEndIdentifier()) {
			System.out.println("End of GRIB file not reached.");
		}		
	}

	// Write the complete GRIB file data to an OutputStream
	//public void writeToStream(OutputStream gribFile) {
	public void writeToStream(OutputStream gribFile) throws IOException {
		
		section0.writeToStream(gribFile);
		section1.writeToStream(gribFile);
		section2.writeToStream(gribFile);
		section3.writeToStream(gribFile);
		section4.writeToStream(gribFile);
		
		for (int cnt=0; cnt<gridcnt; cnt++) {
			section5[cnt].writeToStream(gribFile);
			section6[cnt].writeToStream(gribFile);
			section7[cnt].writeToStream(gribFile);
		}
		
		section8.writeToStream(gribFile);
	}

	public String getType() { return typeid; }
	public String getSource() {return source; }
	
	public GribSection0 getSection0() { return section0; }
	public GribSection1 getSection1() { return section1; }
	public GribSection2 getSection2() { return section2; }
	public GribSection3 getSection3() { return section3; }
	public GribSection4 getSection4() { return section4; }
	public GribSection5 getSection5(int gridid) { return section5[gridid]; }
	public GribSection6 getSection6(int gridid) { return section6[gridid]; }
	public GribSection7 getSection7(int gridid) { return section7[gridid]; }
	public GribSection8 getSection8() { return section8; }

	public void setSection0(GribSection0 section0) { this.section0 = section0; }
	public void setSection1(GribSection1 section1) { this.section1 = section1; }
	public void setSection2(GribSection2 section2) { this.section2 = section2; }
	public void setSection3(GribSection3 section3) { this.section3 = section3; }
	public void setSection4(GribSection4 section4) { this.section4 = section4; }
	public void setSection5(int idx, GribSection5 section5) { this.section5[idx] = section5; }
	public void setSection6(int idx, GribSection6 section6) { this.section6[idx] = section6; }
	public void setSection7(int idx, GribSection7 section7) { this.section7[idx] = section7; }
	public void setSection8(GribSection8 section8) { this.section8 = section8; }

	public static int getLatIndex(GridDefinitionTemplate30 gridDefinition, double latitude) {
		return (GribFile.degToUnits(latitude) - gridDefinition.firstPointLat) / gridDefinition.jDirectionIncrement;
	}

	public static int getLonIndex(GridDefinitionTemplate30 gridDefinition, double longitude) {

		// Make sure that longitude is in a range from -180 to 180 degrees  
		int firstPointLon = gridDefinition.firstPointLon;
		if (firstPointLon >= GribFile.degToUnits(180)) firstPointLon -= GribFile.degToUnits(360);
		
		return (GribFile.degToUnits(longitude) - firstPointLon) / gridDefinition.iDirectionIncrement;
	}

	public static long getVersion() { return serialVersionUID; }
	public static String getVersionString() { return Long.toString(serialVersionUID); }

	public static double unitsToDeg(int units) { return (double)units/GRIB2DEGUNIT; }
	public static int degToUnits(double deg) { return (int)(deg*GRIB2DEGUNIT); }
}
