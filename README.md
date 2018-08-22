# GRIB2Tools
Tools for processing GRIB2 files

GRIB2Tools is a library for processing GRIB2 files. The library reads and decodes the meta data of the GRIB file and puts it into Java objects that are easily accessible. Furthermore, the library offers functionality for random access as well as streamed (i.e sequential) access to the data of the file.

In both cases, random as well as streamed access, an object of type `GribFile` represents the GRIB2 file. This object contains all meta data of the GRIB2 file and provides access to the data of the GRIB2 file. An object of type `InputStream` is required, which delivers the data to the `GribFile`. The `InputStream` can be obtained from any source, for example from a file on the local PC, from a resource on a FTP server or from any URL. 

In its current state the library can is used for constantly processing weather forecast data from the ICON model of German authority Deutscher Wetterdienst (DWD) and from NOAA's GFS publications. See https://theweatherserver.com for a working demo.

Random Data Access
---------------------

A `GribFile` object for random access to the data of the GRIB2 file is created and filled with data by using the following lines: 

```
    RandomAccessGribFile gribFile = new RandomAccessGribFile(datasetid, url);
    gribFile.importFromStream(inputstream, 0);
```

Note that the `RandomAccessGribFile` loads the complete GRIB2 file into memory, which may be a limitation, depending on the number and size of GRIB2 files you want to process and the available memory. On the other hand, the `RandomAccessGribFile` offers arbitrary and location specific random access to the data contained in the GRIB2 file using the following function

```
    double longitude = ...   // in degrees
    double latitude = ...    // in degrees
    float val = gribFile.getValueAtLocation(grididx, latitude, longitude);
```

where longitude and latitude are the coordinates of the position whose data you want to obtain. Longitude and latitude can give an arbitrary position, if the position does not match a grid point, the value of the closest grid point is returned. If you need smoother data, the function

```
    double longitude = ...   // in degrees
    double latitude = ...    // in degrees
    float val = gribFile.interpolateValueAtLocation(grididx, latitude, longitude);
```

returns a two dimensional linear interpolated value at the position identified by longitude and latitude.

Parameters `datasetid` and `url` are for identification of the GRIB2 file that is represented by the `GribFile` object and do not have any further effect on the behaviour of the class.

Streamed Data Access
----------------------

Loading the complete GRIB2 file may not always be possible or desirable. In this case, a `StreamedGribFile` can be used instead of a `RandomAccessGribFile`. A `StreamedGribFile` is instantiated and prepared for data access with the following lines:

```
    StreamedGribFile gribFile = new StreamedGribFile(datasetid, url);
    gribFile.prepareImportFromStream(openFile(inputstream, 0);
```

These lines will load the full meta data of the GRIB2 file into memory and put it into Java classes, but it will not load the data section of the GRIB file. Instead, the data section will be read as a stream, which allows accessing the data of the GRIB2 file sequentially one by one using the function

```
    float val = gribFile.nextValue();
```

Note, that since the data is streamed, no random access of the data for a specific location is possible. Instead, the first position has to be read from the meta data.

The second parameter of the function `prepareImportFromStream` allows to define how many GRIB2 file structures should be skipped before data is accessed. This may be useful if there are several GRIB2 file structures contained in a single file or data stream.

Again, parameters `datasetid` and `url` are for identification of the GRIB2 file that is represented by the `GribFile` object and do not have any further effect on the behaviour of the class.

Accessing the meta data of a GRIB2 file
---------------------------------------

The meta data of a GRIB2 file is contained in so-called templates:
<ul>
	<li>the Grid Definition Template defines the grid of the data,</li>
	<li>the Product Definition Template defines the product,</li>
	<li>the Data Representation Template defines the representation of the data.</li>
</ul>
Each of these templates exist in different variants, which are identified by the Template Numbers. In order to obtain the correct template, the Template Number has to be considered. For each of the templates, uses the scheme illustrated below to obtain the correct template:

```
    GridDefinitionTemplate3x gridDefinition = null;
    GribSection3 section3 = gribFile.getSection3();
    if (section3.gridDefinitionTemplateNumber == 0)
        gridDefinition = (GridDefinitionTemplate30)section3.gridDefinitionTemplate;
    else {
        log.severe("Grid Definition Template Number 3." + section3.gridDefinitionTemplateNumber + " not implemented.");
        // ...
    }

    ProductDefinitionTemplate4x productDefinition = null;
    GribSection4 section4 = gribFile.getSection4();
    if (section4.productDefinitionTemplateNumber == 0)
        productDefinition = (ProductDefinitionTemplate40)section4.productDefinitionTemplate;
    else if (section4.productDefinitionTemplateNumber == 8)
        productDefinition = (ProductDefinitionTemplate48)section4.productDefinitionTemplate;
    else {
        log.severe("Product Definition Template Number 4." + section4.productDefinitionTemplateNumber + " not implemented.");
        // ...
    }

    DataRepresentationTemplate5x dataRepresentation = null;
    GribSection5 section5 = gribFile.getSection5(gridcnt);
    if (section5.dataRepresentationTemplateNumber == 0)
        dataRepresentation = (DataRepresentationTemplate50)section5.dataRepresentationTemplate;
    else {
        log.severe("Data Representation Template Number 5." + section5.dataRepresentationTemplateNumber + " not implemented.");
        // ...
    }
```

From the templates, the meta data of the GRIB2 file can be accessed directly as the individual data fields of the templates. For example, category and number of the product can be accessed from the Product Definition as follows:

```
    byte paramCategory = productDefinition.parameterCategory;
    byte paramNumber = productDefinition.parameterNumber;
```

Note that the library does not fully cover the GRIB2 specification. If you feel that there are certain templates missing that should be supported by the library, please contact me or extend the lib and send me a pull request.

Demo Application
----------------
File `GRIB2FileTest.java`contains a small application. Together with the GRIB2 file in the `res` folder, a working demo is provided, which shows how to use the library.
