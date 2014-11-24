SDNHub Opendaylight Tutorial
============================
This is the OpenDaylight project source code used by the [our tutorial](http://sdnhub.org/tutorials/opendaylight/).

# Directory Organization
* pom.xml: The POM in the main directory specifies all the sub-POMs to build
* commons/parent: contains the parent pom.xml with all properties defined for the subprojects.
* adsal_L2_forwarding: contains the ad-sal version of the L2 forwarding tutorial
* mdsal_L2_forwarding: contains the MD-SAL version of the L2 forwarding tutorial
* plugin_exercise: contain an application that loads both ADSAL OVSDB and MDSAL OpenFlow plugins to program switches
* features-tutorial: defines the three features "sdnhub-tutorial-adsal", "sdnhub-tutorial-mdsal", "sdnhub-plugin-exercise" that can be loaded in Karaf
* distribution/opendaylight-osgi-adsal: Will contain a working controller distribution with the AD-SAL tutorial app that can be run with OSGi
* distribution/opendaylight-osgi-adsal: will contain a working controller distribution with the MD-SAL tutorial app that can be run with OSGi
* distribution/opendaylight-osgi-adsal: will contain a working controller distribution with the all 3 tutorial apps that can be run with Karaf

# HOW TO BUILD
In order to build it's required to have JDK 1.7+ and Maven 3+. 
The following commands are used to build and run.
```
$ mvn clean install
$ cd distribution/opendaylight-karaf/target
$ tar xvfz distribution-karaf-0.5.0-SNAPSHOT.tar.gz
$ cd distribution-karaf-0.5.0-SNAPSHOT && ./bin/karaf
karaf>feature:install sdnhub-XYZ
```
For OsGi mode
```
$ cd distribution/opendaylight-osgi-mdsal/target/distribution-osgi-mdsal-1.1.0-SNAPSHOT-osgipackage/opendaylight
$ ./run.sh
```
