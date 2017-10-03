# GRIB2Tools
Tools for processing GRIB2 files

...

<code>
    // Connect to FTP server for data download
    InputStream inputstream = openFtpConnection(ftpservername, url);
		
    // Import GRIB file
    RandomAccessGribFile gribFile = new RandomAccessGribFile(datasetid, url);
    gribFile.importFromStream(inputstream, 0);
</code>
    
...
    
<code>    
    double longitude = ...   // in degrees
    double latitude = ...    // in degrees
    float val = gribFile.getValueAt(grididx, GribFile.degToUnits(latitude), GribFile.degToUnits(longiude));
</code>

...

<code>    
    float val = gribFile.interpolateValueAt(grididx, GribFile.degToUnits(latitude), GribFile.degToUnits(longiude));
</code>

...

<code>
		// Connect to FTP server for data download
		InputStream ftpInputstream = openFtpConnection(ftpservername, url);

    // Prepare import GRIB file
		StreamedGribFile gribFile = new StreamedGribFile(datasetid, url);
		gribFile.prepareImportFromStream(openFile(inputstream, 0);
</code>

...

<code>
    gribFile.float val = nextValue();
</code>

...

<code>
    GridDefinitionTemplate3x gridDefinition = null;
		GribSection3 section3 = gribFile.getSection3();
		
		if (section3.gridDefinitionTemplateNumber == 0) gridDefinition = (GridDefinitionTemplate30)section3.gridDefinitionTemplate;
		else {
			log.warning("Grid Definition Template Number 3." + section3.gridDefinitionTemplateNumber + " not implemented.");
			// ...
		}
</code>

...

<code>
    ProductDefinitionTemplate4x productDefinition = null;
    GribSection4 section4 = gribFile.getSection4();

    if (section4.productDefinitionTemplateNumber == 0) productDefinition = ((ProductDefinitionTemplate40)section4.productDefinitionTemplate;
    else if (section4.productDefinitionTemplateNumber == 8) productDefinition = (ProductDefinitionTemplate48)section4.productDefinitionTemplate;
    else {
      log.severe("Product Definition Template Number 4." + section4.productDefinitionTemplateNumber + " not implemented.");
      // ...
    }
</code>

...

<code>
    DataRepresentationTemplate5x dataRepresentation = null;
    GribSection5 section5 = gribFile.getSection5(gridcnt);

    if (section5.dataRepresentationTemplateNumber == 0) dataRepresentation = ((DataRepresentationTemplate50)section5.dataRepresentationTemplate;
    else {
      log.severe("Data Representation Template Number 5." + section5.dataRepresentationTemplateNumber + " not implemented.");
      // ...
    }
</code>
