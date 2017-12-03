package com.ph.grib2tools.grib2file.datarepresentation;

import java.nio.ByteBuffer;

import com.ph.grib2tools.grib2file.GribSection;

public class DataSection73 extends DataSection72 {
	
	private static final long serialVersionUID = 100L;
	
	public int[] fstValuesOfOriginal;
   public DataSection73(int numberDataPoints, DataRepresentationTemplate53 representation, ByteBuffer byteBuffer) {
      // We cant read "parent" data here
      super();
	   fstValuesOfOriginal = new int[representation.orderOfSpatialDifferencing+1];
	   for (int i=0; i<fstValuesOfOriginal.length; ++i)
	   {
	      switch (representation.numberOfOctetsExtraDescriptors)
	      {
	         case 1:
	            fstValuesOfOriginal[i] = GribSection.correctNegativeByte(byteBuffer.get());
	            break;
	         case 2:
	            fstValuesOfOriginal[i] = GribSection.correctNegativeShort(byteBuffer.getShort());
	            break;
	         case 4:
	            fstValuesOfOriginal[i] = GribSection.correctNegativeInt(byteBuffer.getInt());
	            break;
            default:
	            throw new IllegalArgumentException(String.format("Unsupported nb of octets : %d", representation.numberOfOctetsExtraDescriptors));
	      }
	   }
	   readData72(numberDataPoints, representation, byteBuffer);
	   assert !byteBuffer.hasRemaining();
	   
	   // Compute real values
	   // ****************************************
	   // WARNING : That's the part i am not sure : we lose the first 2 variablePart why ?
      // ****************************************
	   for (int i=0; i<fstValuesOfOriginal.length-1; ++i) { variablePart[i] = fstValuesOfOriginal[i]; }
	   int on = fstValuesOfOriginal[fstValuesOfOriginal.length-1];
	   for (int i=fstValuesOfOriginal.length-1; i<variablePart.length; ++i) {
	      variablePart[i] = variablePart[i]+on+2*variablePart[i-1]-variablePart[i-2];
	   }
   }
}
