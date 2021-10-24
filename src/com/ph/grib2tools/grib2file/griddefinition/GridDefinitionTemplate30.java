package com.ph.grib2tools.grib2file.griddefinition;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

import com.ph.grib2tools.grib2file.GribSection;
import com.ph.grib2tools.grib2file.RandomAccessGribFile;

public class GridDefinitionTemplate30 extends GridDefinitionTemplate3x {
	
	private static final long serialVersionUID = 100L;

	private static final Logger log = Logger.getLogger(GridDefinitionTemplate30.class.getName());

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
	
	// The following are protected, should bed accessed using getStepI() and getStepJ()
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
		
		// Supporte Scanning Modes: 0x01, 0x02, 0x40
		if ((scanningMode & 0xBC) != 0x00) log.warning("Scanning mode " + scanningMode + " not supported");
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
	
	public int getStepI() {
		
		int sign = getDirectionI();
		return iDirectionIncrement * sign;
	}
	
	public int getStepJ() {

		int sign = getDirectionJ();
		return jDirectionIncrement * sign;
	}

	public int getDirectionI() {
		
		int sign;
/*
		if ((scanningMode & 1) != 0) sign = -1;
		else sign = 1;
*/
		if (lastPointLon > firstPointLon) sign = 1;
		else sign = -1;

		return sign;
	}
	
	public int getDirectionJ() {

		int sign;
/*
		if ((scanningMode & 2) != 0) sign = 1;
		else sign = -1;
*/
		if (lastPointLat > firstPointLat) sign = 1;
		else sign = -1;

		return sign;
	}

	public int getDeltaJ() {
		
		int delta = 0;
		
//		if ((scanningMode & 0x01) == 0x01) log.warning("Scanning mode " + 0x01 + " not supported");
//		if ((scanningMode & 0x02) == 0x02) log.warning("Scanning mode " + 0x02 + " not supported");
//		if ((scanningMode & 0x04) == 0x04) log.warning("Scanning mode " + 0x04 + " not supported");
//		if ((scanningMode & 0x08) == 0x08) log.warning("Scanning mode " + 0x08 + " not supported");
//		if ((scanningMode & 0x10) == 0x10) log.warning("Scanning mode " + 0x10 + " not supported");
//		if ((scanningMode & 0x20) == 0x20) log.warning("Scanning mode " + 0x20 + " not supported");
		if ((scanningMode & 0x40) == 0x40) delta = jDirectionIncrement / 2;
//		if ((scanningMode & 0x80) == 0x80) log.warning("Scanning mode " + 0x80 + " not supported");					

		return delta;
	}
}
