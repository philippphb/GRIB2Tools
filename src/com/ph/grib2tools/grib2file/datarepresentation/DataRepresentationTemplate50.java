package com.ph.grib2tools.grib2file.datarepresentation;
import java.nio.ByteBuffer;

import com.ph.grib2tools.grib2file.GribSection;

public class DataRepresentationTemplate50 extends DataRepresentationTemplate5x {
	
	private static final long serialVersionUID = 100L;

	public byte numberBits;
	public byte typeOfField;
		

	public DataRepresentationTemplate50(ByteBuffer byteBuffer) {

		referenceValueR = byteBuffer.getFloat();
		binaryScaleFactorE = GribSection.correctNegativeShort(byteBuffer.getShort());
		decimalScaleFactorD = GribSection.correctNegativeShort(byteBuffer.getShort());
		numberBits = byteBuffer.get();
		typeOfField = byteBuffer.get();
	}	
}
