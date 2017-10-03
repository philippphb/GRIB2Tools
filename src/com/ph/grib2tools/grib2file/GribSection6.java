package com.ph.grib2tools.grib2file;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class GribSection6 extends GribSection {	
	
	private static final long serialVersionUID = 100L;

	// Content and structure of a Section 6
	protected byte bitMapIndicator;
	protected byte[] bitMap;
	
	
	public GribSection6(InputStream gribfile) throws IOException {
		super(gribfile);		
	}

	public GribSection6(GribSection gribSection) {
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
		bitMapIndicator = byteBuffer.get();
		
		bitMap = new byte[sectionlength-6];
		byteBuffer.get(bitMap);
	}
}
