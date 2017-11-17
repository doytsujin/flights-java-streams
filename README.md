Processing Flight Data using Java 8 Streams
===========================================

I started this project to share some examples of working with the **Java 8** 
Stream API. I don't claim to be an expert on stream programming. This is just
a way for me to share my understanding of how you can use Java apps that 
leverage the Stream API. In particular, I hope to highlight the different style
encouraged by this API. As you will see, there is heavy use of Lambda expressions
throughout this code. That is intentional and strongly encouraged.

The [source data](http://stat-computing.org/dataexpo/2009/the-data.html "Flight Data") 
for this project is published in CSV format by the *American Statistical Association* 
based on data they received from the Bureau of Transportation Statistics (a division 
of the U.S. Department of Transportation). 

You may notice that the above data does have missing data. For example, for 
some flight records, there is no tailNum field.

The structure of the project can be seen in the following picture.

![Eclipse Project File Structure](https://i.imgur.com/vKDH8uT.gif)

I supplemented the plane data above with 
[additional data](http://registry.faa.gov/aircraftrenewal_reports/CanceledReg_Inquiry.aspx "Canceled Plane Data") 
for planes whose registrations have been cancelled. If you want to include this data, download it yourself to the 
*data* folder with the name *cancelled-planes.csv*.

The project makes extensive using of the following domain classes:

* Airport: The physical location of the origin and destination for a flight.
* Carrier: The airline that provided the flight service.
* Flight: The scheduled event to fly a plane between an origin and a destination.
* Plane: The plane scheduled for use on a flight.
* PlaneModel: The combination of a specific manufacturer and model for a plane.
* Route: The combination of a specific origin and destination in either direction.

Before running the code in this project, you should execute the install phase
to download the required data files.

~~~
mvn install
~~~
*This may take some time depending on your Internet download speed.*

Once the data files have been downloaded and extracted, you can execute either 
of the following classes:

* FlightReportsApp
* PlaneReportsApp
