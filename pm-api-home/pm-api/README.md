## Product Management API

API to manage product information.

## To Run Locally:
You will need to download Tomcat.
1. To get the Tomcat version currently on the server, go to https://archive.apache.org/dist/tomcat/tomcat-9/v9.0.12/bin/.
2. Choose your environment you are running and download.

You will need to have a version of OpenJDK.
1. To get the version currently on the server, go to https://jdk.java.net/11/.
2. Add the jdk of OpenJDK (the package with a bin subFolder) to your PATH environment variables.

## Updating the Project Version:
1. Update version in [build.gradle](build.gradle).
2. Update version in service section of [deploy.yaml](deploy.yaml).

## To Run in IntelliJ:
This project makes use of Lombok. To get this to work nicely in Intellij, install the plugin.
1. In *Preferences > Plugins > Marketplace*, search for "Lombok Plugin", install it, and restart IntelliJ.
2. In *Preferences > Build, Execution, Deployment > Compiler > Annotation Processors*, select "Enable annotation processing".

Create a new Tomcat connection using the Tomcat version indicated above.
1. In *Edit Configurations* menu, click on the +.
2. Under *Tomcat* choose *Local*.
3. Under the *Deployment* tab of your Tomcat connection, 
    1. Choose to deploy the "pm_api.war(exploded)" artifact.
    2. Delete what is in the *Application Context* box.
5. Debug/run your new configuration.

There is a bug in Spring Boot, so you have to do a manual setup to run unit tests that take advantage of SpringRunner.
1. In *Edit Configurations* menu, click on the +.
2. Choose JUnit.
3. In the *Test kind* dropdown, choose "All in package".
4. In the *Package* box, put in "com.heb.pm".
5. In the *VM Options:* box, add "--add-opens=java.base/jdk.internal.loader=ALL-UNNAMED".
6. In *Search for tests:*, choose "In single module" and select module pm-api in the *Use classpath of module:* box.

## To View Swagger Documentation:
From context root you are using, add '/swagger-ui.html'. For example, if running pm-api locally on http://localhost:8080, put http://localhost:8080/swagger-ui.html in browser url.

## To Update the Model and Entity Objects:
The object model is maintained in the [pm-lib-model](https://git.heb.com/Product-Management/pm-lib-model) project, any changes need to be made there.

The entity objects are maintained in the [pm-lib-dao](https://git.heb.com/Product-Management/pm-lib-dao) project, any changes need to be made there.

Instructions are available in those projects for making modification.

## Operation IDs
Any operation that generates legacy events needs a unique ID. All IDs for operations in this API should follow the form
I18J0*XXX* where *XXX* is a sequential number. You should define a new ID in the [application.properties](src/main/resources/application.properties)
file in the Operation IDs section. Just add 1 to the previous max ID. 

## Build from command line
1. switch to java 11 in the environment (i.e. j11 alias)

2. ./gradlew build
