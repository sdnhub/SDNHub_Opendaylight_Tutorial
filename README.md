SDNHub Opendaylight Tutorial
============================
This is the OpenDaylight project source code used by the [our tutorial](http://sdnhub.org/tutorials/opendaylight/).

# Directory Organization
* pom.xml: The POM in the main directory specifies all the sub-POMs to build
* commons/parent: contains the parent pom.xml with all properties defined for the subprojects.
* commons/utils: contains custom utilities built for OpenFlow programming 
* learning-switch: contains the tutorial L2 hub / switch
* tapapp: contains the traffic monitoring tap application
* features: defines the two features "sdnhub-tutorial-learning-switch", * "sdnhub-tutorial-tapapp" that can be loaded in Karaf
* distribution/karaf-branding: contains karaf branner for SDN Hub
* distribution/opendaylight-karaf: contains packaging relevant pom to * generate a running directory 

# HOW TO BUILD
In order to build it's required to have JDK 1.8+ and Maven 3.2+. 
The following commands are used to build and run.
```
$ mvn clean install
$ cd distribution/opendaylight-karaf/target/assembly
$ ./bin/karaf
karaf>feature:install sdnhub-XYZ
```
