package com.ph.grib2tools.grib2file;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class GribSection1 extends GribSection {	
	
	private static final long serialVersionUID = 100L;

	// Content and structure of a Section 1
	protected short generatingCentre;
	protected short generatingSubcentre;
	protected byte masterTablesVersion;
	protected byte localTablesVersion;
	protected byte significanceOfReferenceTime;
	protected short year;
	protected byte month;
	protected byte day;
	protected byte hour;
	protected byte minute;
	protected byte second;
	protected byte productionStatus;
	protected byte type;
	

	public GribSection1(InputStream gribfile) throws IOException {
		super(gribfile);		
	}

	public GribSection1(GribSection gribSection) {
		super(gribSection.sectionlength, gribSection.sectionnumber, gribSection.sectiondata);
	}

	@Override
	public void readData(InputStream gribfile) throws IOException {
		super.readData(gribfile);
		readSection();
	}
	
	public void readSection() {
		
		ByteBuffer byteBuffer = ByteBuffer.wrap(sectiondata);
		
		// Parse section and extract data
		generatingCentre = byteBuffer.getShort();
		generatingSubcentre = byteBuffer.getShort();
		masterTablesVersion = byteBuffer.get();
		localTablesVersion = byteBuffer.get();
		significanceOfReferenceTime = byteBuffer.get();
		year = byteBuffer.getShort();
		month = byteBuffer.get();
		day = byteBuffer.get();
		hour = byteBuffer.get();
		minute = byteBuffer.get();
		second = byteBuffer.get();
		productionStatus = byteBuffer.get();
		type = byteBuffer.get();		
	}
}
