package com.ph.grib2tools.grib2file;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import com.ph.grib2tools.grib2file.productdefinition.ProductDefinitionTemplate40;
import com.ph.grib2tools.grib2file.productdefinition.ProductDefinitionTemplate48;
import com.ph.grib2tools.grib2file.productdefinition.ProductDefinitionTemplate4x;

public class GribSection4 extends GribSection {	
	
	private static final long serialVersionUID = 100L;

	// Content and structure of a Section 4
	public short numberCoordinateValues;
	public short productDefinitionTemplateNumber;
	public ProductDefinitionTemplate4x productDefinitionTemplate;
	public byte[] optionalListOfCoordinates;
	
	
	public GribSection4(InputStream gribfile) throws IOException {
		super(gribfile);		
	}

	public GribSection4(GribSection gribSection) {
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
		numberCoordinateValues = byteBuffer.getShort();
		productDefinitionTemplateNumber = byteBuffer.getShort();
		
		if (productDefinitionTemplateNumber == 0) productDefinitionTemplate = new ProductDefinitionTemplate40(byteBuffer);
		else if (productDefinitionTemplateNumber == 8) productDefinitionTemplate = new ProductDefinitionTemplate48(byteBuffer);
		else System.out.println("Product Definition Template Number 4." + productDefinitionTemplateNumber + " not implemented.");
		
		optionalListOfCoordinates = new byte[numberCoordinateValues];
		byteBuffer.get(optionalListOfCoordinates);
	}
}
