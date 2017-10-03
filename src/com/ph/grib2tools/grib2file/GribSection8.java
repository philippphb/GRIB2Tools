package com.ph.grib2tools.grib2file;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;

public class GribSection8 implements Serializable {	
	
	private static final long serialVersionUID = 100L;

	// End identifier of a GRIB file 
	protected final static byte[] GRIBENDIDENTIFIER = {55, 55, 55, 55};

	// Content and structure of a Section 8
	byte[] endidentifierbytes = new byte[4];

	
	public GribSection8(InputStream gribfile) {

		try {
			
			// Read complete section
			byte[] section8 = new byte[4];
			gribfile.read(section8);		
			ByteBuffer byteBuffer = ByteBuffer.wrap(section8);

			// Parse section and extract data
			byteBuffer.get(endidentifierbytes);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void writeToStream(OutputStream gribFile) {
				
		try {
			
			gribFile.write(endidentifierbytes);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean verifyEndIdentifier() {
		return (ByteBuffer.wrap(endidentifierbytes).compareTo(ByteBuffer.wrap(GRIBENDIDENTIFIER)) == 0);			
	}
}
