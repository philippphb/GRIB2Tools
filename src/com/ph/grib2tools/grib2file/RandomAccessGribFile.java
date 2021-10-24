package com.ph.grib2tools.grib2file;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

import com.ph.grib2tools.grib2file.datarepresentation.DataRepresentationTemplate50;
import com.ph.grib2tools.grib2file.griddefinition.GridDefinitionTemplate30;

// A representation of a GRIB file containing all meta data and allowing a random
// access to the data of the GRIB file. While the random access allows great flexibility
// it requires that the complete data is held in memory 
public class RandomAccessGribFile extends GribFile {

	private static final long serialVersionUID = 100L;

	private static final Logger log = Logger.getLogger(RandomAccessGribFile.class.getName());

	
	public RandomAccessGribFile(String typeid, String source) {
		super(typeid, source);
	}

	public void importFromStream(InputStream gribfile, int numskip) throws IOException {
					
		if (gribfile == null) return;
		
		// By overwriting the section variables, the first numskip GRIB file data structures
		// within a stream or a file can be skipped
		for (int t=0; t<numskip+1; t++) {		

			gridcnt = 0;
	
			//while (true) {
			while (gridcnt < 1) {

				// Read all meta data but not the data itself in Section 7
				importMetadatFromStream(gribfile);
				
				// Read the data of Section 7 into memory
				GribSection nextsection = new GribSection(gribfile).initSection();
				if (nextsection.sectionnumber == 7) {
					section7[gridcnt] = (GribSection7)nextsection;
					section7[gridcnt].setDataRepresentation(section5[gridcnt].numberDataPoints, section5[gridcnt].dataRepresentationTemplate);
					section7[gridcnt].readData(gribfile);
					
					// Decode bit map to expand sparse data grid to full data grid
					section7[gridcnt].setBitmapDecodedData(decodeDataBitmap(gridcnt));
					
					gridcnt++;
				}
				else {
					log.warning("Section " + nextsection.sectionnumber + " found while Section 7 expected. aborting.");
					return;
				}
			}
	
			finalizeImport(gribfile);
		}		
	}
	
	private byte[] decodeDataBitmap(int gridcnt) {
		
		//GribSection6 section6 = gribFile.getSection6(0);
		if (section6[gridcnt].bitMapIndicator == 0) {
			
			GridDefinitionTemplate30 gridDefinition = (GridDefinitionTemplate30) this.getGridDefinitionTemplate();
			DataRepresentationTemplate50 dataRepresentation = (DataRepresentationTemplate50) this.getDataRepresentationTemplate(gridcnt);		
			int numbits = dataRepresentation.numberBits;

			byte valuesArray[] = section7[gridcnt].sectiondata;
			byte decodedGridData[] = new byte[gridDefinition.numberPointsLat*gridDefinition.numberPointsLon*2];
			
			if ((gridDefinition.scanningMode & 0x04) == 0x00) {
				
				// Holds the index of the next value in the value array of Section 7
				int sourcevaluecnt = 0;
				
				for (int j=0; j<gridDefinition.numberPointsLat; j++) {
					for (int i=0; i<gridDefinition.numberPointsLon; i++) {

						// Index of the bit map
						int bitmapIdx;
						bitmapIdx = j*gridDefinition.numberPointsLon + i;

						// Position of byte and bit of the bit map 
						int bitmapBytePos = (int)Math.floor(bitmapIdx/8);
						int bitmapBitPos = bitmapIdx % 8;
						byte bitmapByte = section6[gridcnt].bitMap[bitmapBytePos];

						short rawValue;
						
						// Grid point without value, here we set this to zero. Depending on the data, this is
						// not the best value. Maybe value for empty grid pos should be made configurable
						if ((bitmapByte & (1 << bitmapBitPos)) == 0) rawValue = 0;
						//if ((bitmapByte & (1 << (8-bitmapBitPos))) == 0) rawValue = 0;
						
						// Grid point with value
						else {
							
							// Position of the value in the array of values of Section 7
							int sourcebitpos = sourcevaluecnt * numbits;

							// Position of byte and bit of the value array 
							int valstartbyte = (int)Math.floor(sourcebitpos/8);
							int valstartbit = sourcebitpos % 8;

							// Retrieve value from value array
							ByteBuffer byteBuffer = ByteBuffer.wrap(valuesArray, valstartbyte, 2);
							rawValue = byteBuffer.getShort();
							rawValue = (short) (rawValue << valstartbit);
							rawValue = (short) (rawValue >> (16-numbits));

							// Increase the index of the next value in the value array of Section 7
							sourcevaluecnt++;
						}

						// Write value in the ful grid data array
						decodedGridData[bitmapIdx*2] = (byte) (rawValue >> 8);
						decodedGridData[bitmapIdx*2+1] = (byte) (rawValue & 0xFF);
					}
				}
			}	
				
			return decodedGridData;
		}
		
		else {
			return section7[gridcnt].sectiondata;
		}
	}
	
	// Extracts the data belonging to the passed coordinate (lat, lon) in degrees and returns the
	// value represented by this data. If the passed coordinate is not a grid point of the
	// data grid, the data belonging to the grid point closest to the passed position
	// is considered. 
	public float getValueAtLocation(int grididx, double lat, double lon) {
		return getValueAt(grididx, GribFile.degToUnits(lat), GribFile.degToUnits(lon));
	}
	
	// Extracts the data belonging to the passed coordinate (lat, lon) in units and returns the
	// value represented by this data. If the passed coordinate is not a grid point of the
	// data grid, the data belonging to the grid point closest to the passed position
	// is considered. 
	public float getValueAt(int grididx, int lat, int lon) {
	
		GribSection5 sec5 = getSection5(grididx);
		GribSection7 sec7 = getSection7(grididx);
				
		float val = 0;
		
		if (sec5.dataRepresentationTemplateNumber == 0) {

			DataRepresentationTemplate50 dataRepresentation = (DataRepresentationTemplate50)sec5.dataRepresentationTemplate;	
			int bytesperval = dataRepresentation.numberBits / 8;

			if (section3.gridDefinitionTemplateNumber == 0) {
	
				GridDefinitionTemplate30 gridDefinition = (GridDefinitionTemplate30)section3.gridDefinitionTemplate;
	
				int deltaj = 0;
/*
				// Implementation of Scanning Modes
//				if ((gridDefinition.scanningMode & 0x01) == 0x01) log.warning("Scanning mode " + 0x01 + " not supported");
//				if ((gridDefinition.scanningMode & 0x02) == 0x02) log.warning("Scanning mode " + 0x02 + " not supported");
				if ((gridDefinition.scanningMode & 0x04) == 0x04) log.warning("Scanning mode " + 0x04 + " not supported");
				if ((gridDefinition.scanningMode & 0x08) == 0x08) log.warning("Scanning mode " + 0x08 + " not supported");
				if ((gridDefinition.scanningMode & 0x10) == 0x10) log.warning("Scanning mode " + 0x10 + " not supported");
				if ((gridDefinition.scanningMode & 0x20) == 0x20) log.warning("Scanning mode " + 0x20 + " not supported");
				if ((gridDefinition.scanningMode & 0x40) == 0x40) deltaj = gridDefinition.jDirectionIncrement / 2;
				if ((gridDefinition.scanningMode & 0x80) == 0x80) log.warning("Scanning mode " + 0x80 + " not supported");
*/
				deltaj = gridDefinition.getDeltaJ();
	
				// Calculate j index of the matrix containing the data the contains the data of the 
				// passed latitude lat
				int deltalat = lat - (gridDefinition.firstPointLat + deltaj);
				//int jidx = Math.round((float)deltalat / (float)gridDefinition.jDirectionIncrement);
//				int jidx = Math.round((float)deltalat / (float)gridDefinition.getStepJ());
				int jidx = (int)Math.floor((float)deltalat / (float)gridDefinition.getStepJ());

				// Calculate i index of the matrix containing the data the contains the data of the 
				// passed longitude lon
				int firstPointLon = gridDefinition.firstPointLon + 0;
				if (firstPointLon >= GribFile.degToUnits(180)) firstPointLon -= GribFile.degToUnits(360);
				int deltalon = lon - firstPointLon;
				//int iidx = Math.round((float)deltalon / (float)gridDefinition.iDirectionIncrement);
				int iidx = Math.round((float)deltalon / (float)gridDefinition.getStepI());

				// Extract data belonging to the referred location and calculate the value represented by the data
				//byte data[] = sec7.sectiondata;
				byte data[] = sec7.bitmapDecodedData;
				short unsignedraw = ByteBuffer.wrap(data).getShort((jidx*gridDefinition.numberPointsLon+iidx)*bytesperval);
				val = sec5.calcValue(unsignedraw); 
			}
			
			else {
				log.warning("Grid Definition Template Number 3." + section3.gridDefinitionTemplateNumber + " not implemented.");
			}
		}
		
		else {
			log.warning("Data Representation Template Number 5." + sec5.dataRepresentationTemplateNumber + " not implemented.");
		}
		
		return val;
	}

	// Extracts the data belonging to the passed coordinate (lat, lon) in degrees and returns the
	// value represented by this data. If the passed coordinate is not a grid point of the
	// data grid, the data belonging to the neighbored grid points is interpolated and used
	// to calculate the value belonging to the passed position.
	public float interpolateValueAtLocation(int grididx, double lat, double lon) {
		return interpolateValueAt(grididx, GribFile.degToUnits(lat), GribFile.degToUnits(lon));
	}

	// Extracts the data belonging to the passed coordinate (lat, lon) in units and returns the
	// value represented by this data. If the passed coordinate is not a grid point of the
	// data grid, the data belonging to the neighbored grid points is interpolated and used
	// to calculate the value belonging to the passed position.
	public float interpolateValueAt(int grididx, int lat, int lon) {
		
		GribSection5 sec5 = getSection5(grididx);
		GribSection7 sec7 = getSection7(grididx);
		
		float val = 0;
		
		if (sec5.dataRepresentationTemplateNumber == 0) {

			DataRepresentationTemplate50 dataRepresentation = (DataRepresentationTemplate50)sec5.dataRepresentationTemplate;	
			int bytesperval = dataRepresentation.numberBits / 8;

			if (section3.gridDefinitionTemplateNumber == 0) {
	
				GridDefinitionTemplate30 gridDefinition = (GridDefinitionTemplate30)section3.gridDefinitionTemplate;

				int deltaj = 0;
/*
				// Implementation of Scanning Modes
//				if ((gridDefinition.scanningMode & 0x01) == 0x01) log.warning("Scanning mode " + 0x01 + " not supported");
//				if ((gridDefinition.scanningMode & 0x02) == 0x02) log.warning("Scanning mode " + 0x02 + " not supported");
				if ((gridDefinition.scanningMode & 0x04) == 0x04) log.warning("Scanning mode " + 0x04 + " not supported");
				if ((gridDefinition.scanningMode & 0x08) == 0x08) log.warning("Scanning mode " + 0x08 + " not supported");
				if ((gridDefinition.scanningMode & 0x10) == 0x10) log.warning("Scanning mode " + 0x10 + " not supported");
				if ((gridDefinition.scanningMode & 0x20) == 0x20) log.warning("Scanning mode " + 0x20 + " not supported");
				if ((gridDefinition.scanningMode & 0x40) == 0x40) deltaj = gridDefinition.jDirectionIncrement / 2;
				if ((gridDefinition.scanningMode & 0x80) == 0x80) log.warning("Scanning mode " + 0x80 + " not supported");					
*/
				deltaj = gridDefinition.getDeltaJ();
deltaj = 0;				
				// Find j indices of the grid points of the matrix containing the data that surround the
				// passed latitude lat
				int deltalat = lat - (gridDefinition.firstPointLat + deltaj);
				//int jidx1 = Math.round((float)deltalat / (float)gridDefinition.jDirectionIncrement);
				//int jidx1 = (int)Math.floor((float)deltalat / (float)gridDefinition.jDirectionIncrement);
				int jidx1 = (int)Math.floor((float)deltalat / (float)gridDefinition.getStepJ());
				
				// Correct indices when end of the array dimension is reached
				if (jidx1 >= gridDefinition.numberPointsLat-1) jidx1--; 
				int jidx2 = jidx1 + 1;

				// Find i indices of the grid points of the matrix containing the data that surround the
				// passed longitude lon
				int firstPointLon = gridDefinition.firstPointLon + 0;
// 17.10.21
//				if (firstPointLon >= GribFile.degToUnits(180)) firstPointLon -= GribFile.degToUnits(360);
				int deltalon = lon - firstPointLon;
				//int iidx1 = Math.round((float)deltalon / (float)gridDefinition.iDirectionIncrement);
				//int iidx1 = (int)Math.floor((float)deltalon / (float)gridDefinition.iDirectionIncrement);
				int iidx1 = (int)Math.floor((float)deltalon / (float)gridDefinition.getStepI());

				// Correct indices when end of the array dimension is reached
				if (iidx1 >= gridDefinition.numberPointsLon-1) iidx1--; 
				int iidx2 = iidx1 + 1;

				
				//byte data[] = sec7.sectiondata;				
				byte data[] = sec7.bitmapDecodedData;				

				// Extract data of the four grid points surrounding the passed coordinate and calculate the
				// values represented by the data
				float val11 = sec5.calcValue(ByteBuffer.wrap(data).getShort((jidx1*gridDefinition.numberPointsLon+iidx1)*bytesperval));
				float val12 = sec5.calcValue(ByteBuffer.wrap(data).getShort((jidx1*gridDefinition.numberPointsLon+iidx2)*bytesperval)); 
				
				float val21 = sec5.calcValue(ByteBuffer.wrap(data).getShort((jidx2*gridDefinition.numberPointsLon+iidx1)*bytesperval));
				float val22 = sec5.calcValue(ByteBuffer.wrap(data).getShort((jidx2*gridDefinition.numberPointsLon+iidx2)*bytesperval)); 

				
				// Find latitudes of the grid points of the matrix containing the data that surround the
				// passed latitude lat
				//int lat1 = (gridDefinition.firstPointLat + deltaj) + jidx1 * gridDefinition.jDirectionIncrement;
				//int lat2 = lat1 + gridDefinition.jDirectionIncrement;
				int lat1 = (gridDefinition.firstPointLat + deltaj) + jidx1 * gridDefinition.getStepJ();
				int lat2 = lat1 + gridDefinition.getStepJ();

				// Find longitudes of the grid points of the matrix containing the data that surround the
				// passed longitude lon
				int lon1 = firstPointLon + iidx1 * gridDefinition.getStepI();
				int lon2 = lon1 + gridDefinition.getStepI();

				// Interpolate value belonging to the passed location
				float val1x = val11 + (val12-val11) * (lon-lon1) / (lon2-lon1);				
				float val2x = val21 + (val22-val21) * (lon-lon1) / (lon2-lon1);
				val = val1x + (val2x-val1x) * (lat-lat1) / (lat2-lat1);
			}
			
			else {
				log.warning("Grid Definition Template Number 3." + section3.gridDefinitionTemplateNumber + " not implemented.");
			}
		}
		
		else {
			log.warning("Data Representation Template Number 5." + sec5.dataRepresentationTemplateNumber + " not implemented.");
		}

		return val;
	}
}
