package com.ph.grib2tools.grib2file;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;

// Template of a GRIB Section, valid for Section types from 1 to 7. Not valid for Sections of type 0 and 8
public class GribSection implements Serializable {

	private static final long serialVersionUID = 100L;

	// Length of the section
	public int sectionlength;

	// Section number
	public byte sectionnumber;

	// Data of the section
	public byte[] sectiondata;
	
	
	public GribSection(int len, byte num, byte[] data) {
		sectionlength = len;
		sectionnumber = num;
		sectiondata = data;
	}
	
	public GribSection(InputStream gribfile) throws IOException {


		// All Sections of type 1 to 7 begin with a five byte long header. This header consists of a four byte
		// long length of the section, followed by a one byte section number (type)
		byte[] sectionheader = new byte[5];
		gribfile.read(sectionheader);		
		ByteBuffer byteBuffer = ByteBuffer.wrap(sectionheader);

		// Extract section length and section number (type) from the header
		sectionlength = byteBuffer.getInt();
		sectionnumber = byteBuffer.get();
	}
	
	public GribSection initSection() {
		
		if (sectionnumber == 1) return new GribSection1(this);
		else if (sectionnumber == 2) return new GribSection2(this);
		else if (sectionnumber == 3) return new GribSection3(this);
		else if (sectionnumber == 4) return new GribSection4(this);
		else if (sectionnumber == 5) return new GribSection5(this);
		else if (sectionnumber == 6) return new GribSection6(this);
		else if (sectionnumber == 7) return new GribSection7(this);
		else System.out.println("Section Number " + sectionnumber + " not implemented");
		
		return null;
	}
		
	public void readData(InputStream gribfile) throws IOException {
		
		// Read complete section
		sectiondata = new byte[sectionlength-5];
		gribfile.read(sectiondata);
	}
	
	public void writeToStream(OutputStream gribFile) throws IOException {

		DataOutputStream dataout = new DataOutputStream(gribFile);

		dataout.writeInt(sectionlength);
		gribFile.write(sectionnumber);
		gribFile.write(sectiondata);
	}
	
	// Adjust a two byte value extracted from a GRIB file according to the GRIB specification to obtain
	// a correct unsigned short
	public static int adjustUnsignedShort(int unsignedshort) {
		int unsignedint = (unsignedshort & 0x7FFF) + (unsignedshort & 0x8000);
		return unsignedint;
	}
	
	// Convert a two byte value extracted from a GRIB file according to the GRIB specification to recover
	// the sign of a signed short
	public static short correctNegativeShort(short uncorrectedvalue) {
		short correctedvalue = (short)(uncorrectedvalue & 0x7fff);
		if ((uncorrectedvalue & 0x8000) == 0x8000) correctedvalue = (short) -correctedvalue;
		return correctedvalue;
	}

	// Convert a four byte value extracted from a GRIB file according to the GRIB specification to recover
	// the sign of a signed int
	public static int correctNegativeInt(int uncorrectedvalue) {
		int correctedvalue = uncorrectedvalue & 0x7fffffff;
		if ((uncorrectedvalue & 0x80000000) == 0x80000000) correctedvalue = -correctedvalue;
		return correctedvalue;
	}
}
