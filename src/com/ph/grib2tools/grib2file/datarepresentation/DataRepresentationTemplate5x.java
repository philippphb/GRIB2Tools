package com.ph.grib2tools.grib2file.datarepresentation;

import java.io.Serializable;

public class DataRepresentationTemplate5x implements Serializable {

	private static final long serialVersionUID = 100L;

	public float referenceValueR;
	public short binaryScaleFactorE;
	public short decimalScaleFactorD;
}
