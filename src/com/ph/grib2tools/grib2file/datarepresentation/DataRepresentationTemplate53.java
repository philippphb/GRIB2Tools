package com.ph.grib2tools.grib2file.datarepresentation;
import java.nio.ByteBuffer;

import com.ph.grib2tools.grib2file.GribSection;

public class DataRepresentationTemplate53 extends DataRepresentationTemplate52 {
	
	private static final long serialVersionUID = 100L;
	byte orderOfSpatialDifferencing;
	short numberOfOctetsExtraDescriptors;
		

	public DataRepresentationTemplate53(ByteBuffer byteBuffer) {
	   super(byteBuffer);
	   assert byteBuffer.position()==47 - 5;
		orderOfSpatialDifferencing = byteBuffer.get();
		numberOfOctetsExtraDescriptors = GribSection.adjustUnsignedByte(byteBuffer.get());
	}	
}
