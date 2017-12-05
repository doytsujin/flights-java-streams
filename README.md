Processing Flight Data using Java 8 Streams
===========================================

I started this project to share some examples of working with the **Java 8** 
Stream API. I don't claim to be an expert on stream programming. This is just
a way for me to share my understanding of how to build Java apps that leverage 
the Stream API. In particular, I hope to highlight the different programming style
encouraged by this API. As you will see, there is heavy use of Lambda expressions
throughout this code. That is intentional and strongly encouraged.

The [source data](http://stat-computing.org/dataexpo/2009/the-data.html "Flight Data") 
for this project is published in CSV format by the *American Statistical Association* 
based on data they received from the Bureau of Transportation Statistics (a division 
of the U.S. Department of Transportation). You may notice that the data does have 
missing fields. For example, for some flight records, there is no tailNum field. This 
obviously limits what you can do with the data in those cases.

The structure of the project can be seen in the following picture.

![Eclipse Project File Structure](https://i.imgur.com/yGJhebX.gif)

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

Once the data files have been downloaded and extracted, you can execute the
ReportsApp class to select and launch available reports.

The classes in the airtraffic.iterator and airtraffic.stream packages implement
the same functionality using iterators and streams respectively. This provides
a convenient way to compare an iterator-based solution and a stream-based one.

I have also included classes in the airtraffic.benchmark class that perform
micro benchmarks using [JMH](http://openjdk.java.net/projects/code-tools/jmh/).
You will see that the stream-based solutions either as fast or faster than 
the iterator-based ones.

If you visit the [source data link](http://stat-computing.org/dataexpo/2009/the-data.html "Flight Data"), 
you will notice there are data files for each year from 1987 to 2008. 
The install phase will only download the 2008 flight data file. If you want to 
execute the above classes using flight data from earlier years, you can 
download and extract any of the other available files. Then you can add them to 
the configuration file (*config.yaml*) like this:

![YAML File](https://i.imgur.com/YJjsU6w.gif) 

The project includes a logging.properties file that can be used to configure
JDK logging. To enable it, add the following VM arguments when you launch the 
above application classes:

~~~
-Djava.util.logging.config.file=logging.properties
~~~

All of the code in this project is licensed under the MIT License. See the 
LICENSE file for details.

Pull requests are welcome.
