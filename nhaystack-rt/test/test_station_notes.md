#Test Harness Setup and Usage


##nhaystack_simple
This station simulates a scenario where the is only a single Jace talking to a system.
In this case we imagine that a "Haystack Client" is querying the Jace for points and 
history data.  So this station has a setup that mimics many different use cases for a 
Jace using nhaystack.

The test harness runs through a number of tests to check a number of the operations 
accessible through the REST API.
- cannot login using the admin username
- for workbench logins you must use the following credentials
  - username = user
  - password = Vk3ldb237847
- the admin user is used in the test script for this station
  - username = admin
  - password = abcd1234


##nhaystack_sup
This is an example of a larger setup of a Niagara system.  In this case there is 2
Jaces setup and a Web Supervisor.  THe Web Supervisor station is already configured
with the 

###Station Login Details
nhaystack_sup
- username = admin
- password = Abcde12345

nhaystack_j1
- username = admin
- password = Abcde12345

nhaystack_j2
- username = admin
- password = Abcde12345
  