package com.ph.grib2tools.grib2file;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import com.ph.grib2tools.grib2file.datarepresentation.DataRepresentationTemplate50;
import com.ph.grib2tools.grib2file.datarepresentation.DataRepresentationTemplate52;
import com.ph.grib2tools.grib2file.datarepresentation.DataRepresentationTemplate53;
import com.ph.grib2tools.grib2file.datarepresentation.DataRepresentationTemplate5x;
import com.ph.grib2tools.grib2file.datarepresentation.DataSection72;
import com.ph.grib2tools.grib2file.datarepresentation.DataSection73;
import com.ph.grib2tools.grib2file.datarepresentation.DataSection7x;

public class GribSection7 extends GribSection {	
	
	private static final long serialVersionUID = 200L;
	
	private int numberDataPoints;
	private DataRepresentationTemplate5x dataRepresentation;
	public DataSection7x data;
	
	// Contains the full data grid. If a bit map is used, this field contains the
	// full data grid, including the fields without a value, which are set to 0.
	// If no bitmap is used, this field contains the section 7 data
	public byte bitmapDecodedData[];

	public GribSection7(int len, byte num, byte[] data) {
		super(len, num, data);
		bitmapDecodedData = data;
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
		ByteBuffer byteBuffer = ByteBuffer.wrap(sectiondata);
		if (dataRepresentation.getClass().equals(DataRepresentationTemplate50.class)) { 
			// No action required
		}
		else if (dataRepresentation.getClass().equals(DataRepresentationTemplate52.class)) {
			data = new DataSection72(numberDataPoints, (DataRepresentationTemplate52) dataRepresentation, byteBuffer);
		} else if (dataRepresentation.getClass().equals(DataRepresentationTemplate53.class)) {
			data = new DataSection73(numberDataPoints, (DataRepresentationTemplate53) dataRepresentation, byteBuffer);
		} else
			System.out.println("Data Representation Template Number 7." + dataRepresentation + " not implemented.");
	}	
	
	public void setDataRepresentation(int numberDataPoints,
                                      DataRepresentationTemplate5x dataRepresentation)
	{
		this.numberDataPoints = numberDataPoints;
		this.dataRepresentation = dataRepresentation;
	}
	
	public void setBitmapDecodedData(byte decodedData[]) {
		bitmapDecodedData = decodedData;
	}
}
