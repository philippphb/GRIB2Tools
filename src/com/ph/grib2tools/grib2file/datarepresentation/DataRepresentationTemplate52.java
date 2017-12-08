package com.ph.grib2tools.grib2file.datarepresentation;
import java.nio.ByteBuffer;

import com.ph.grib2tools.grib2file.GribSection;

public class DataRepresentationTemplate52 extends DataRepresentationTemplate50 {
	
	private static final long serialVersionUID = 100L;
	byte groupSplittingMethodUsed;
	byte missingValueManagementUsed;
	long primaryMissingValueSubstitute;
	long secondaryMissingValueSubstitute;
	int numberOfGroupsOfDataValues;
	short referenceForGroupWidths;
	short numberOfBitsUsedForTheGroupWidths;
	int referenceForGroupLengths;
	short lengthIncrementForTheGroupLengths;
	int trueLengthOfLastGroup;
	short numberOfBitsForScaledGroupLengths;

	public DataRepresentationTemplate52(ByteBuffer byteBuffer) {
	   super(byteBuffer);
	   assert byteBuffer.position() == 21 - 5;
	   groupSplittingMethodUsed = byteBuffer.get();
	   missingValueManagementUsed = byteBuffer.get();
	   primaryMissingValueSubstitute = GribSection.adjustUnsignedInt( byteBuffer.getInt() );
	   secondaryMissingValueSubstitute = GribSection.adjustUnsignedInt( byteBuffer.getInt() );
	   numberOfGroupsOfDataValues = byteBuffer.getInt() ;      // If negative this will go wrong during data extraction anyway
	   referenceForGroupWidths = GribSection.adjustUnsignedByte(byteBuffer.get());
	   numberOfBitsUsedForTheGroupWidths = GribSection.adjustUnsignedByte(byteBuffer.get());
	   referenceForGroupLengths = byteBuffer.getInt() ;        // If negative this will go wrong during data extraction anyway
	   lengthIncrementForTheGroupLengths = GribSection.adjustUnsignedByte(byteBuffer.get());
	   trueLengthOfLastGroup = byteBuffer.getInt();            // If negative this will go wrong during data extraction anyway
	   numberOfBitsForScaledGroupLengths = GribSection.adjustUnsignedByte(byteBuffer.get());
	}	
}
