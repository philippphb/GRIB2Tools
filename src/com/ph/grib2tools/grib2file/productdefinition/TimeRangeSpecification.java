package com.ph.grib2tools.grib2file.productdefinition;
import java.io.Serializable;
import java.nio.ByteBuffer;

public class TimeRangeSpecification implements Serializable {

	private static final long serialVersionUID = 100L;

	public byte statisticalProcess;
	public byte typeOfTimeIncrement;
	public byte unitTimeRange;
	public int lengthTimeRange;
	public byte unitTimeIncrement;
	public int lengthTimeIncrement;
	
	
	public TimeRangeSpecification(ByteBuffer byteBuffer) {

		statisticalProcess = byteBuffer.get();
		typeOfTimeIncrement = byteBuffer.get();
		unitTimeRange = byteBuffer.get();
		lengthTimeRange = byteBuffer.getInt();
		unitTimeIncrement = byteBuffer.get();
		lengthTimeIncrement = byteBuffer.getInt();
	}
}
