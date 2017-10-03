package com.ph.grib2tools.grib2file;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import com.ph.grib2tools.grib2file.griddefinition.GridDefinitionTemplate30;
import com.ph.grib2tools.grib2file.griddefinition.GridDefinitionTemplate3x;

public class GribSection3 extends GribSection {	
	
	private static final long serialVersionUID = 100L;

	// Content and structure of a Section 3
	public byte sourceOfGridDefinition;
	public int numDataPoints;
	public byte numOfOctetsForOptionalList;
	public byte interpretationOfList;
	public short gridDefinitionTemplateNumber;
	public GridDefinitionTemplate3x gridDefinitionTemplate;
	public byte[] optionalListOfPoints;
	
	public GribSection3(InputStream gribfile) throws IOException {
		super(gribfile);		
	}

	public GribSection3(GribSection gribSection) {
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
		sourceOfGridDefinition = byteBuffer.get();
		numDataPoints = byteBuffer.getInt();
		numOfOctetsForOptionalList = byteBuffer.get();
		interpretationOfList = byteBuffer.get();
		gridDefinitionTemplateNumber = byteBuffer.getShort();
		
		if (gridDefinitionTemplateNumber == 0) gridDefinitionTemplate = new GridDefinitionTemplate30(byteBuffer);
		else System.out.println("Grid Definition Template Number 3." + gridDefinitionTemplateNumber + " not implemented.");
		
		optionalListOfPoints = new byte[numOfOctetsForOptionalList];
		byteBuffer.get(optionalListOfPoints);
	}
}
