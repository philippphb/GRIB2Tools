package com.ph.grib2tools.grib2file;

import java.io.IOException;
import java.io.InputStream;

public class GribSection7 extends GribSection {	
	
	private static final long serialVersionUID = 100L;

	public GribSection7(int len, byte num, byte[] data) {
		super(len, num, data);
	}
	
	public GribSection7(InputStream gribfile) throws IOException {
		super(gribfile);		
	}

	public GribSection7(GribSection gribSection) {
		super(gribSection.sectionlength, gribSection.sectionnumber, gribSection.sectiondata);
	}

	@Override
	public void readData(InputStream gribfile) throws IOException {
		super.readData(gribfile);
		readSection();
	}

	public void readSection() {
	}	
}
