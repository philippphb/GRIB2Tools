package com.ph.grib2tools.grib2file;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import com.ph.grib2tools.grib2file.datarepresentation.DataRepresentationTemplate50;
import com.ph.grib2tools.grib2file.datarepresentation.DataRepresentationTemplate52;
import com.ph.grib2tools.grib2file.datarepresentation.DataRepresentationTemplate53;
import com.ph.grib2tools.grib2file.datarepresentation.DataRepresentationTemplate5x;

public class GribSection5 extends GribSection {	
	
	private static final long serialVersionUID = 100L;

	// Content and structure of a Section 5
	protected int numberDataPoints;
	protected short dataRepresentationTemplateNumber;
	public DataRepresentationTemplate5x dataRepresentationTemplate;
	
	
	public GribSection5(InputStream gribfile) throws IOException {
		super(gribfile);		
	}

	public GribSection5(GribSection gribSection) {
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
		numberDataPoints = byteBuffer.getInt();
		dataRepresentationTemplateNumber = byteBuffer.getShort();
		
		if (dataRepresentationTemplateNumber == 0) dataRepresentationTemplate = new DataRepresentationTemplate50(byteBuffer);
		else if (dataRepresentationTemplateNumber == 2) dataRepresentationTemplate = new DataRepresentationTemplate52(byteBuffer);
		else if (dataRepresentationTemplateNumber == 3) dataRepresentationTemplate = new DataRepresentationTemplate53(byteBuffer);
		else System.out.println("Data Representation Template Number 5." + dataRepresentationTemplateNumber + " not implemented.");
	}
	
	public int getNumDataPoints(){ return numberDataPoints; }
	
	public float calcValue(short unsignedraw) {
	
		// Calculate value according to the GRIB specification 
		int raw = adjustUnsignedShort(unsignedraw);
		float val = (dataRepresentationTemplate.referenceValueR + (float)(0+raw) * (float)Math.pow(2, dataRepresentationTemplate.binaryScaleFactorE) / (float)Math.pow(10, dataRepresentationTemplate.decimalScaleFactorD));
		
		return val;
	}
}
