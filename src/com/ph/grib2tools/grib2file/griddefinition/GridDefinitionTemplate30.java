package com.ph.grib2tools.grib2file.griddefinition;
import java.nio.ByteBuffer;

import com.ph.grib2tools.grib2file.GribSection;

public class GridDefinitionTemplate30 extends GridDefinitionTemplate3x {
	
	private static final long serialVersionUID = 100L;

	public byte shapeOfEarth;
	public byte scaleFactorRadiusSphericalEarth;
	public int scaledValueRadiusSphericalEarth;
	public byte scaleFactorMajorAxisOblateSpheroidEarth;
	public int scaledValueMajorAxisOblateSpheroidEarth;
	public byte scaleFactorMinorAxisOblateSpheroidEarth;
	public int scaledValueMinorAxisOblateSpheroidEarth;
	public int numberPointsLon;
	public int numberPointsLat;
	public int basicAngle;
	public int subdivisionsBasicAngle;
	public int firstPointLat;
	public int firstPointLon;
	public byte resolutionAndComponentFlags;
	public int lastPointLat;
	public int lastPointLon;
	public int iDirectionIncrement;
	public int jDirectionIncrement;
	public byte scanningMode;
	
	
	public GridDefinitionTemplate30(ByteBuffer byteBuffer) {

		shapeOfEarth = byteBuffer.get();
		scaleFactorRadiusSphericalEarth = byteBuffer.get();
		scaledValueRadiusSphericalEarth = byteBuffer.getInt();
		scaleFactorMajorAxisOblateSpheroidEarth = byteBuffer.get();
		scaledValueMajorAxisOblateSpheroidEarth = byteBuffer.getInt();
		scaleFactorMinorAxisOblateSpheroidEarth = byteBuffer.get();
		scaledValueMinorAxisOblateSpheroidEarth = byteBuffer.getInt();
		numberPointsLon = byteBuffer.getInt();
		numberPointsLat = byteBuffer.getInt();
		basicAngle = byteBuffer.getInt();
		subdivisionsBasicAngle = byteBuffer.getInt();
		firstPointLat = GribSection.correctNegativeInt(byteBuffer.getInt());
		firstPointLon = GribSection.correctNegativeInt(byteBuffer.getInt());
		resolutionAndComponentFlags = byteBuffer.get();
		lastPointLat = GribSection.correctNegativeInt(byteBuffer.getInt());
		lastPointLon = GribSection.correctNegativeInt(byteBuffer.getInt());
		iDirectionIncrement = GribSection.correctNegativeInt(byteBuffer.getInt());
		jDirectionIncrement = GribSection.correctNegativeInt(byteBuffer.getInt());
		scanningMode = byteBuffer.get();
	}

	// Compares this GridDefinitionTemplate30 with the passed GridDefinitionTemplate30 other
	public boolean equals(GridDefinitionTemplate30 other) {

		if (other == null) return false;
		
		if ((this.firstPointLat == other.firstPointLat) &&
			(this.firstPointLon == other.firstPointLon) &&
			(this.lastPointLat == other.lastPointLat) &&
			(this.lastPointLon == other.lastPointLon) &&
			(this.iDirectionIncrement == other.iDirectionIncrement) &&
			(this.jDirectionIncrement == other.jDirectionIncrement) &&
			(this.numberPointsLat == other.numberPointsLat) &&
			(this.numberPointsLon == other.numberPointsLon)
			) {
			return true;
		}
		
		return false;
	}
}
