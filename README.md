# EN16931 Registry Services

## Overview

CEN/TC 434 and EN 16931 registry services.

The initial prototype of this project was presented at CEN TC 434 face2face plenary 2019 in Reykjavik by [Philip Helger](https://www.linkedin.com/in/void0/).<br/>
After 2019 there were no CEN TC 434 meeting nor calls for CEN TC WG 3, 5, 6 or 7 - only WG 1 was active.</br>
Likely most working groups and this registry project will be revived by EU funding.</br>
The project was therefore smoke-tested for the CEN 434 plenary in Bruxelles (2023) by [Svante Schubert](https://www.linkedin.com/in/svante-schubert-2913232/) by adding this prototype to an [Oracle free Cloud tier](https://www.oracle.com/cloud/free/) using a Mini Ubuntu 22.04 image with a state-of-the-art [Apache Tomcat 10.1.9 supporting Jakarta EE 10](https://tomcat.apache.org/whichversion.html).

## External references

* [Demo of the prototype of registry server (v2019) running in the Cloud (user view)](http://138.3.243.85:8080/en16931-registry-services-1.0.0-SNAPSHOT/public)
* [Prototype of registry server (v2019) running in the Cloud (admin view)](http://138.3.243.85:8080/en16931-registry-services-1.0.0-SNAPSHOT/secure/).<br/>
   Use ***admin@helger.com*** as email with ***password*** as the password!
* [Official EU Wiki on EN16931 CIUS & Extensions](https://ec.europa.eu/digital-building-blocks/wikis/display/EINVCOMMUNITY/Registry+of+CIUS+%28Core+Invoice+Usage+Specifications%29+and+Extensions) (***not yet added to the registry prototype (see [issue #3](https://github.com/phax/en16931-registry/issues/3)***))

## Possible missing Features (v2019)

* User Management
* Additional Meta data for CIUS/EXTENSION (see [issue #3](https://github.com/phax/en16931-registry/issues/3)
* High-Level CIUS/Extension Comparison (e.g. [visual comparison between e-Invoice and e-Receipt](https://stpe.semantic-treehouse.nl/#/TreeView/Message_a7df2626-c410-44fe-bf84-e18d64ae9f3b))
  ![image](https://github.com/phax/en16931-registry/assets/825051/d101aebd-8daa-4011-9f55-d8fed5d74c95)
* Visual Sorting/Selection (e.g. by Country providing EU Map or by Sector)
  ![image](https://github.com/phax/en16931-registry/assets/825051/c3b3e046-d66d-49d9-8c72-e18d6140a60c)
* Continuous Integration (CI/CD)
* Security Features
* Dependency updates: Consistent Jakarta EE 10 package usages
* Scalability
* JSON REST API for CIUS/Extension data sets
* ...

## Development

This project requires **Java 11 or later**.<br/>

* Call `mvn process-sources` to initially generate all sources.
* Call `mvn install` to build the WAR to be deployed to the Jakarta Application Server (e.g. [Apache Tomcat Server](https://tomcat.apache.org/download-10.cgi)).

***NOTE***: You might need to add the following folders into your IDE build path:

* target/generated-sources/jdmc
* target/generated-resources/jdmc
* target/generated-test-sources/jdmc
* target/generated-test-resources/jdmc

***NOTE***: Code generation is based on <https://github.com/phax/ph-jdmc>

---

[Project's Coding Style Guide](https://github.com/phax/meta/blob/master/CodingStyleguide.md) |
Kindly supported by [YourKit Java Profiler](https://www.yourkit.com)
