package com.ph.grib2tools.grib2file.productdefinition;
import java.nio.ByteBuffer;

public class ProductDefinitionTemplate48 extends ProductDefinitionTemplate4x {	
	
	private static final long serialVersionUID = 100L;

	public ProductDefinitionTemplate40 productDefinitionTemplate40;

	public short yearEnd;
	public byte monthEnd;
	public byte dayEnd;
	public byte hourEnd;
	public byte minuteEnd;
	public byte secondEnd;
	public byte numberTimeRangeSpecifications;
	public int totalNumberDataValuesMissing;
	public TimeRangeSpecification[] timeRangeSpecification; 


	public ProductDefinitionTemplate48(ByteBuffer byteBuffer) {

		productDefinitionTemplate40 = new ProductDefinitionTemplate40(byteBuffer);
		
		yearEnd = byteBuffer.getShort();
		monthEnd = byteBuffer.get();
		dayEnd = byteBuffer.get();
		hourEnd = byteBuffer.get();
		minuteEnd = byteBuffer.get();
		secondEnd = byteBuffer.get();
		numberTimeRangeSpecifications = byteBuffer.get();
		totalNumberDataValuesMissing = byteBuffer.getInt();
		
		timeRangeSpecification = new TimeRangeSpecification[numberTimeRangeSpecifications];
		for (int i=0; i<numberTimeRangeSpecifications; i++) timeRangeSpecification[i] = new TimeRangeSpecification(byteBuffer);
	}
}
