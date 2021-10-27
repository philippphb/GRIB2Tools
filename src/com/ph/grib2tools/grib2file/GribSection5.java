package com.ph.grib2tools.grib2file;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

import com.ph.grib2tools.grib2file.datarepresentation.DataRepresentationTemplate50;
import com.ph.grib2tools.grib2file.datarepresentation.DataRepresentationTemplate52;
import com.ph.grib2tools.grib2file.datarepresentation.DataRepresentationTemplate53;
import com.ph.grib2tools.grib2file.datarepresentation.DataRepresentationTemplate5x;

public class GribSection5 extends GribSection {	
	
	private static final long serialVersionUID = 100L;

	private static final Logger log = Logger.getLogger(GribSection5.class.getName());

	// Content and structure of a Section 5
	@Deprecated
	protected int numberDataPoints;
	protected int numberValues;		// replaces numberDataPoints
	public short dataRepresentationTemplateNumber;
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
		numberValues = byteBuffer.getInt();
		numberDataPoints = numberValues;	// For downward compatibility
		dataRepresentationTemplateNumber = byteBuffer.getShort();
		
		if (dataRepresentationTemplateNumber == 0) dataRepresentationTemplate = new DataRepresentationTemplate50(byteBuffer);
		else if (dataRepresentationTemplateNumber == 2) dataRepresentationTemplate = new DataRepresentationTemplate52(byteBuffer);
		else if (dataRepresentationTemplateNumber == 3) dataRepresentationTemplate = new DataRepresentationTemplate53(byteBuffer);
		else System.out.println("Data Representation Template Number 5." + dataRepresentationTemplateNumber + " not implemented.");
	}
	
	@Deprecated
	public int getNumDataPoints(){ return numberDataPoints; }

	public int getNumValues(){ return numberValues; }

	@Deprecated
	public float calcValue(short unsignedraw) {
	
		// Calculate value according to the GRIB specification 
		int raw = adjustUnsignedShort(unsignedraw);
		float val = (dataRepresentationTemplate.referenceValueR + (float)(0+raw) * (float)Math.pow(2, dataRepresentationTemplate.binaryScaleFactorE) / (float)Math.pow(10, dataRepresentationTemplate.decimalScaleFactorD));
		
		return val;
	}
	
	public float calcValue(byte valuesArray[], int sourcevaluecnt) {

		float val = 0;
		
		if (dataRepresentationTemplateNumber == 0) {

			int numbits = ((DataRepresentationTemplate50)dataRepresentationTemplate).numberBits;
	
			// Position of the value in the array of values of Section 7
			int sourcebitpos = sourcevaluecnt * numbits;
	
			// Position of byte and bit of the value array 
			int valstartbyte = (int)Math.floor(sourcebitpos/8);
			int valstartbit = sourcebitpos % 8;
	
			// Retrieve value from value array
			int raw;
			if (numbits <= 8) {
				
				// Get data from values array
				ByteBuffer byteBuffer = ByteBuffer.wrap(valuesArray, valstartbyte, 1);
				short rawValue = byteBuffer.get();
	
				// Adjust for correct position in bit stream
				rawValue = (byte) (rawValue >> (8-numbits-valstartbit));
				rawValue = (byte) (rawValue & (0xff >> (8-numbits)));
				
				raw = adjustUnsignedByte(rawValue);
			}
			else {
				
				// Get data from values array
				ByteBuffer byteBuffer = ByteBuffer.wrap(valuesArray, valstartbyte, 2);
				short rawValue = byteBuffer.getShort();
	
				// Adjust for correct position in bit stream
				rawValue = (short) (rawValue >> (16-numbits-valstartbit));
				rawValue = (short) (rawValue & (0xffff >> (16-numbits)));
				
				raw = adjustUnsignedShort(rawValue);
			}
			
			// Calculate value according to the GRIB specification 
			val = (dataRepresentationTemplate.referenceValueR + (float)(0+raw) * (float)Math.pow(2, dataRepresentationTemplate.binaryScaleFactorE) / (float)Math.pow(10, dataRepresentationTemplate.decimalScaleFactorD));
		}
		
		else {
			log.warning("Data Representation Template Number 5." + dataRepresentationTemplateNumber + " not implemented.");
		}

		return val;
	}
}
