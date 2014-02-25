SDNHub_Opendaylight_Tutorial
============================

DIRECTORY ORGANIZATION
======================

- commons/parent: contains the parent pom.xml for all ovsdb projects.
- tutorial_L2_forwarding: contains the ovsdb SB plugin
- distribution/opendaylight: will build a working controller distribution
  based on the controller + tutorial_L2_forwarding modules

HOW TO BUILD
============

In order to build it's required to have JDK 1.7+ and Maven 3+, to get
a build going it's needed to:

cd commons/parent
mvn clean install

or if you want to avoid SNAPSHOT checking

cd commons/parent
mvn clean install -nsu

HOW TO RUN
============

Upon successful completion of a build

cd distribution/opendaylight/target/distribution.tutorial_L2_forwarding-1.0.0-SNAPSHOT-osgipackage/opendaylight/
./run.sh

Wait for the osgi console to startup and then point a browser at 

http:localhost:8080/




