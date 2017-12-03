package com.ph.grib2tools.grib2file.datarepresentation;

import java.nio.ByteBuffer;

import com.ph.grib2tools.grib2file.GribSection;

public class DataSection72 extends DataSection7x {
	
	private static final long serialVersionUID = 100L;
	
	private DataRepresentationTemplate52 representation;
   // 6-xx              NG group reference values (XI in the decoding formula), each of which is encoded using the number of bits specified in octet 20 of Data Representation Template 5.0. Bits set to zero shall be appended as necessary to ensure this sequence of numbers ends on an octet boundary.
	public int[] groupRefValues;
   // [xx+1]-yy         NG group widths, each of which is encoded using the number of bits specified in octet 37 of Data Representation Template 5.2. Bits set to zero shall be appended as necessary to ensure this sequence of numbers ends on an octet boundary.
	public int[] groupWidths;
   // [yy+1]-zz         NG scaled group lengths, each of which is encoded using the number of bits specified in octet 47 of Data Representation Template 5.2. Bits set to zero shall be appended as necessary to ensure this sequence of numbers ends on an octet boundary. (see Note 14 of Data Representation Template 5.2)
	public int[] scaledGroupLength;
   
   
   // It's sucks but data are inverted.
   protected DataSection72() {}
	
	public DataSection72(int numberDataPoints, DataRepresentationTemplate52 representation, ByteBuffer byteBuffer)
	{
	   readData72(numberDataPoints, representation, byteBuffer);
   }
	
	protected final void readData72(int numberDataPoints, DataRepresentationTemplate52 representation, ByteBuffer byteBuffer)
   {
      groupRefValues    = new int[(int) representation.numberOfGroupsOfDataValues];
      groupWidths       = new int[(int) representation.numberOfGroupsOfDataValues];
      scaledGroupLength = new int[(int) representation.numberOfGroupsOfDataValues];
      variablePart      = new int[numberDataPoints];
      BitReader bR = new BitReader(byteBuffer);
      for (int i=0; i<groupRefValues.length; ++i)
      {
         groupRefValues[i] = bR.readBits( representation.numberBits );
      }
      bR.flushBit();
      for (int i=0; i<groupWidths.length; ++i)
      {
         groupWidths[i] = bR.readBits( representation.numberOfBitsUsedForTheGroupWidths );
      }
      bR.flushBit();
      for (int i=0; i<scaledGroupLength.length; ++i)
      {
         scaledGroupLength[i] = bR.readBits(representation.numberOfBitsForScaledGroupLengths);
      }
      bR.flushBit();
      
      int groupWidth, idxGrp=0, idxX=0;
      // For each group except last
      for ( ; idxGrp<groupRefValues.length-1; ++idxGrp)
      {
         int groupLength = scaledGroupLength[idxGrp] * representation.lengthIncrementForTheGroupLengths + representation.referenceForGroupLengths;
         groupWidth = groupWidths[idxGrp];
         for (int idxInGrp=0; idxInGrp < groupLength; ++idxInGrp, ++idxX)
         {
            variablePart[idxX] = bR.readBits(groupWidth) + groupRefValues[idxGrp];
         }
      }
      // Read last group
      groupWidth = groupWidths[idxGrp];
      for (int idxInGrp=0; idxInGrp < representation.trueLengthOfLastGroup; ++idxInGrp, ++idxX)
      {
         variablePart[idxX] = bR.readBits(groupWidth) + groupRefValues[idxGrp];
      }
   }
      
   public static class BitReader
   {
      ByteBuffer bb;
      int byteInProgress = 0;
      int nbBitRead = 8;
      public BitReader(ByteBuffer byteBuffer)
      {
         this.bb = byteBuffer;
      }
      public int readBits(int nbBitRequested)
      {
         int nbBitRequestedRemaining = nbBitRequested;
         int result = 0;
         // If remaining bit in buffer
         if (8-nbBitRead>0)
         {
            // Extract all available but not more than needed
            int nbAvailable = Math.min(8-nbBitRead, nbBitRequestedRemaining);
            // First a shift to the right to eliminate the "not yet to read" bits (if any)
            // Then a mask to eliminate the "already read" bits (if any)
            result = (byteInProgress >> (8-nbBitRead-nbAvailable)) & ((1<<nbAvailable)-1);
            // We have read data in the buffer, they are no more available
            nbBitRead += nbAvailable;
            nbBitRequestedRemaining -= nbAvailable;
         }
         // There is a need to read full byte (that's easy)
         while (nbBitRequestedRemaining>=8)
         {
            byteInProgress = GribSection.adjustUnsignedByte(bb.get());
            result = (result << 8) + byteInProgress;
            nbBitRequestedRemaining -= 8;
         }
         // Read the last bit needed (less than 8)
         if (nbBitRequestedRemaining>0)
         {
            // Get the next byte
            byteInProgress = GribSection.adjustUnsignedByte(bb.get());
            // First a shift to the left for the "already recovered" bits
            // Second a shift to the right to eliminate the "not yet to read" bits (they will always be some)
            result = (result << nbBitRequestedRemaining) + (byteInProgress >> (8-nbBitRequestedRemaining));
            // The buffered byte is not empty
            nbBitRead = nbBitRequestedRemaining;
         }
         return result;
      }
      public void flushBit()
      {
         nbBitRead = 8;
      }
   }
}
