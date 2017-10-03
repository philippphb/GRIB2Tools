package com.ph.grib2tools.grib2file;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;

public class GribSection0 implements Serializable {

	private static final long serialVersionUID = 100L;

	// Start identifier of a GRIB file 
	protected final static byte[] GRIBMAGICNUMBER = {71, 82, 73, 66};

	// Content and structure of a Section 0
	protected byte[] magicnumberbytes = new byte[4];
	protected short reserved;
	protected byte discipline;
	protected byte number;
	protected long totalLength;
	
	
	public GribSection0(InputStream gribfile) {
		
		try {

			// Read complete section
			byte[] section0 = new byte[16];
			gribfile.read(section0);		
			ByteBuffer byteBuffer = ByteBuffer.wrap(section0);

			// Parse section and extract data
			byteBuffer.get(magicnumberbytes);
			reserved = byteBuffer.getShort();
			discipline = byteBuffer.get();
			number = byteBuffer.get();
			totalLength = byteBuffer.getLong();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
	}

	public void writeToStream(OutputStream gribFile) {
		
		DataOutputStream dataout = new DataOutputStream(gribFile);
		
		try {
			
			gribFile.write(magicnumberbytes);
			dataout.writeShort(reserved);
			gribFile.write(discipline);
			gribFile.write(number);
			dataout.writeLong(totalLength);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean verifyMagicNumber() {		
		return (ByteBuffer.wrap(magicnumberbytes).compareTo(ByteBuffer.wrap(GRIBMAGICNUMBER)) == 0);			
	}	

	public boolean verifyGribVersion() {		
		return (number == 2);			
	}	
}
