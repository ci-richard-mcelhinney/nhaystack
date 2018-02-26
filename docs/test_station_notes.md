<link href="markdown.css" rel="stylesheet"/>

# ![NHaystack](tag.png)Testing nhaystack for Niagara 4  

##Introduction
The 'nhaystack' module for Niagara 4 comes ready with an extensive test suite.  
The test suite configuration allows a developer to run automated tests against more 
than 1 running Niagara station and exercise the Haystack REST API in different 
scenarios.  The test suite is constantly evolving and more tests and scenarios
are always being added.

By configuring and running a number of Niagara stations a developer is able to 
simulate a sufficient range of test scenarios.

The following notes describe how to use the test harness for testing against a 
live Niagara Station.

This document also describes how to run a small number of other unit tests using 
the Niagara TestNG test harness.

##Distribution
The test stations for the nhaystack module are distributed as Niagara Station
templates.  This means that a developer who wishes to run the 'nhaystack' test
suite must first create new stations on their local system using the distributed
station templates.

##Setup and execution
Step 1 in running the test suite is to clone/download the 'nhaystack' repository
and build the relevant Niagara modules ensuring that both the runtime and workbench
profile modules have been copied into the <niagara_home>\modules folder.

Step 2 is to copy the station template files included with the 'nhaystack' 
repository to the <niagara_user_home>\stationTemplates folder so they can be found
by the Niagara Workbench.

Step 3 is to create a new instance of each of the test stations using the templates.

Documentation for creating new Niagara stations from a template is available with 
your Niagara 4 distribution.  The most efficient way to run the 'nhaystack' test
harness would be to create each of the stations mentioned below.  Once a copy of
each station has been created and the administrator credentials have been configured
it will be possible to run the test suite.

Due to the fact that every time the stations are created, when creating via a 
template, new admnistrator credentials must be configured it is necessary to change
the credentials in the test suite.  To do this edit the SimpleClientTest.java and the
SupervisorClientTest.java, searching for any references to the username and password
for the respective stations and replacing the credentials in the source code with the
configured when creating the test stations from the templates.

Once this is done it will be possible to run the test harness using the 'gradlew' 
command.

<p><code>gradlew test</code></p>
  
This will build and run the test suite against the running test stations.  If you
receive errors check that the test stations have started properly and there are no 
port conflicts.  It is recommended that the test stations are started using the 
Niagara Workbench, in this way they will be started under the Platform Daemon and
have full crypto and security functionality operational, thus enabling a more 
realistic test scenario.

##nhaystack_simple
This station simulates a scenario where there is only a single Jace talking to a system.
In this case we imagine that a "Haystack Client" is querying the Jace for points and 
history data.  So this station has a setup that mimics many different use cases for a 
Jace using 'nhaystack'.

###Port Allocation
Fox  => 1912<br>
HTTP => 82<br>

##nhaystack_sup
This is an example of a larger setup of a Niagara system.  In this case there is 2
Jaces setup and a Web Supervisor.  THe Web Supervisor station is already configured
with the subordinate Jaces in the Niagara Network.  The subordinate JACEs do not have
any 'Haystack' tagging, they are just normal Niagara stations simulating the function
of a JACE.  

###Port Allocation
nhaystack_sup<br>
Fox  => 1915<br>
HTTP => 85<br>

nhaystack_j1<br>
Fox  => 1916<br>
HTTP => 86<br>

nhaystack_j2<br>
Fox  => 1917<br>
HTTP => 87<br>

##Niagara Test Harness
There is currently a small number of unit tests that can be run using the Niagara 
Test Harness.  The details of the test harness can be found in the developer 
documentation in the Niagara installation, however it is quite simple to run the 
test harness.

First you need to build the Niagara Test Module: <br>

<p><code>gradlew moduleTestJar</code></p>

then assuming you have already built the main nhaystack modules you can run 
the following from the command line in a Niagara shell.

<p><code>test nhaystack</code></p>

This will run the automated tests in the Niagara environment.  Test results are
viewable in the form of a HTML report which can be found under the Niagara User
Home directory.