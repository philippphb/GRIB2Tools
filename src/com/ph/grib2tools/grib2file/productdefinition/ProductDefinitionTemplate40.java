package com.ph.grib2tools.grib2file.productdefinition;
import java.nio.ByteBuffer;

public class ProductDefinitionTemplate40 extends ProductDefinitionTemplate4x {
	
	private static final long serialVersionUID = 100L;

	public byte parameterCategory;
	public byte parameterNumber;
	public byte typeOfGeneratingProcess;
	public byte backgroundGeneratingProcessId;
	public byte analysisOrForecastGeneratingProcessId;
	public short hoursObservationalDataCutOff;
	public byte minutesObservationalDataCutOff;
	public byte unitTimeRange;
	public int forecastTime;
	public byte typeFirstFixedSurface;
	public byte scaleFactorFirstFixedSurface;
	public int scaledValueFirstFixedSurface;
	public byte typeSecondFixedSurface;
	public byte scaleFactorSecondFixedSurface;
	public int scaledValueSecondFixedSurface;	
	

	public ProductDefinitionTemplate40(ByteBuffer byteBuffer) {

		parameterCategory = byteBuffer.get();
		parameterNumber = byteBuffer.get();
		typeOfGeneratingProcess = byteBuffer.get();
		backgroundGeneratingProcessId = byteBuffer.get();
		analysisOrForecastGeneratingProcessId = byteBuffer.get();
		hoursObservationalDataCutOff = byteBuffer.getShort();
		minutesObservationalDataCutOff = byteBuffer.get();
		unitTimeRange = byteBuffer.get();
		forecastTime = byteBuffer.getInt();
		typeFirstFixedSurface = byteBuffer.get();
		scaleFactorFirstFixedSurface = byteBuffer.get();
		scaledValueFirstFixedSurface = byteBuffer.getInt();
		typeSecondFixedSurface = byteBuffer.get();
		scaleFactorSecondFixedSurface = byteBuffer.get();
		scaledValueSecondFixedSurface = byteBuffer.getInt();	
	}
}
