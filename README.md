# Project9 - Implementation of unit and integration tests for a Billing and Accounting System, executed in an implemented continuous integration pipeline - 

This is a continuous integration environment setup for a Billing and accouting System. The Jenkins testings and code quality analyses are triggered after each commit to the following github repository : https://github.com/pplaxine/Project9

The integration tests being run for each layer (Business and Consumer) through different maven profiles, you can also choose to run tests separatly via the following jenkins jobs :  

    - Unit tests : run Project9UnitTest  
    - Unit tests + Integration tests on consumer layer : Project9IntegrationTestConsumerLayer  
    - Unit tests + Integration tests on business layer : Project9IntegrationTestBusinessLayer  
 

## Getting Started 

### Configuration    

  #### DataBase docker deployement
  1. Go to "yourProjectDirectory/docker/dev/" and edit "docker-compose.yml" file 
  2. Within the file, configure your database "ports:" with "yourDockerMachineIp:9032:5432" 

  Your database is set. 


  #### Application database url   
  1. Go to "yourProjectDirectory/myerp/myerp-consumer/src/main/resources/com/dummy/myerp/consumer and edit "applicationContext.xml" file
  2. Within the bean dataSourceMYERP, configure your database url with "jdbc:postgresql://yourDockerMachineIp:9032/db_myerp"

  Your app can now access the testing database


  #### Jenkins Jobs 
  1. Go to "yourProjectDirectory/jenkins job/Project9FullTest/ and edit "config.xml" file
  2. Replace -Dsonar.host.url=http://192.168.99.102:9000 by "http://yourDockerMachineIp:9000"
  3. Replace <command>sleep 13s&#xd;explorer &quot;http://192.168.99.101:9000/projects?sort=-analysis_date&quot;&#xd;docker ps -a</command> with       "http://yourDockerMachineIp:9000/..."

  Your Sonarqube server is now set. 



### Run jenkins job 
  
  1. Add the jenkins jobs folders in yourJenkinsHomeDirectory/jobs
  2. Go to "Manage Jenkins" and select "Reload Configuration from Disk" 
  3. All jobs should be now visible on your jenkins homepage

  *Warning*

  4. Make sure to run SonarqubeDeployement job first as the Sonarqube server takes several minutes before being operational. You can check the progress at the following address : http://yourDockerMachineIp:9000/
  5. Run the job of your choice (*Important*: Project9IntegrationTestBusinessLayer and Project9IntegrationTestConsumerLayer can't be ran simultaneously as they both use the same database docker container). 

  
## Prerequisites

Install Java JRE version 8 or higher.

Install the latest version of Maven (for more information : https://maven.apache.org/). 

Install the latest version of Docker (for more information : https://www.docker.com/). 

Install the latest version of Jenkins (for more information : https://jenkins.io/). 

## Built With

* [Eclipse](https://www.eclipse.org/documentation/)

## Authors

* **Philippe Plaxine** - *Initial work* - [PPlaxine](https://github.com/pplaxine)