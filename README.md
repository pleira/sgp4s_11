SGP4_11
------------

[![Join the chat at https://gitter.im/pleira/sgp4s_11](https://badges.gitter.im/pleira/sgp4s_11.svg)](https://gitter.im/pleira/sgp4s_11?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

Interim scala project to try different SGP4 algorithm implementations. SGP4 is used to predict orbits from TLE data. The Deep Space algorithm, which contains lunar-solar perturbations, is not implemented. My [sgp4s](http://github.com/pleira/sgp4s) project should become the improvements tried here.

Most of the code is based in the work done by Vallado, documented in [SpaceTrackReport #3](https://celestrak.com/NORAD/documentation/). To verify the results, the cpp [public code](http://celestrak.com/publications/AIAA/2006-6753/) has been taken. Also, I have looked into the TLE processing done in the [Orekit](http://www.orekit.org) project.
My initial aim is to introduce an alternative implementation for the calculation of the perturbations suggested by [Martin Lara](http://arxiv.org/pdf/1407.8076.pdf). 

This implementation is also an exercise of having cleaner code written in Scala. That means, the physical models and transformations should appear clearer in the code. 

The numerical library Spire is used. In particular, the user can work with other numerical types that can offer more precision than Double. That can be of interest near conflictive points in the theory (low inclinations, and 63 deg) at the expense of longer running time. 


Copyright and License
====================

This software is Copyright by Pablo Pita Leira, 2015. Licensed under Apache v. 2.0.

The software contains parts adapted from an SGP 4 s/w implementation in the public domain from Vallado. Some parts of the software were inspired by the SGP4 Orekit implementation, specially at the beginning of this project. Software taken from Orekit has copyright by CS Systèmes d'Information, licensed under Apache v. 2.0. 


Requires
---------------
* Please download latest version of SBT.
* [sbt 0.13.9](http://www.scala-sbt.org)

Use
---------------
Clone and test the project 

    git clone https://github.com/pleira/sgp4_11.git
    $ cd sgp4_11
    $ sbt
    > test

IDE
---------------
* Intellij IDEA (13 | 14). Intellij has great support for Scala, and works fast on this setup.
	> gen-idea

	* [Intellij IDEA 14](http://www.jetbrains.com/idea/download/). Community edition with Scala plugin is enough.

* Scala IDE: Uses Eclipse. Lagging behind Intellij in features, but offers the familiar feel of Eclipse development. 	
	> eclipse
	
	* Read [sbt eclipse](https://github.com/typesafehub/sbteclipse/wiki/Using-sbteclipse) for more details.
	* [Scala IDE 3](http://scala-ide.org). Install at least the ScalaTest plugin, Play plugin if you are building a web app using it.

Test
------------------
The best feature of SBT is incremental compilation. This is most evident when testing.

	> ~ test

Be warned, it's addictive. You'll never want to code in any other statically compiled language without this feature.

Test a single TestSuite

	> test-only package.subpackage.Class

Test and Jenkins
-------------------

ScalaTest is been configured to produce test reports in the JUnit XML format that Jenkins can understand in target/junit-xml-reports.
Configure Jenkins to use this folder. For many more options to configure, use the [ScalaTest runner documentation](http://www.scalatest.org/user_guide/using_the_runner)

Integration Test
-------------------
Abbreviated as it. A bit fiddly, since it will require every setting defined for test to also be defined for it.

	> it:test

Useful commands
-----------------
The former will run a scala REPL with all the project dependencies available. The later will autoscan and run a Main class.
	> console
	> run

Code Coverage
------------------
Uses Scoverage as a code coverage tool. The main advantage over other coverage tools is that it understands expressions, as opposed to lines.

	> test
	> coverageReport

See [Scoverage Plugin](https://github.com/scoverage/sbt-scoverage)

Read documentation details, specially if using integration tests.

Style Checker
-------------------
Uses [ScalaStyle](http://www.scalastyle.org)

	> scalastyle

Author
--------------------
Pablo Pita

