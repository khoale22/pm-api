## PM DAO Library
A library containing JPA entities for our databases and utilities to interact with them. This includes:

1. JPA entities for the EMD database.
2. Base classes related to processing information from audit tables.
3. Base classes for collecting and publishing internal events.
4. Oracle-specific converters.

#### Using this Library
To use a released version of this library, add the PM Maven repository to your dependencies by adding this to the 
repository section of your build.gradle:

<pre>
maven {
    name "HEB PM"
    url "http://nexus-lib.heb.com/nexus/content/repositories/PM"
}
</pre>

Include the following dependencies:

<pre>
compile("com.heb.pm:pm-lib-util:$pmUtilVersion")
compile("com.heb.pm:pm-lib-dao:$pmDaoVersion")
</pre>

To use a snapshot version of this library, add the PM Maven repository to your dependencies by adding this to the 
repository section of your build.gradle:

<pre>
maven {
    name "HEB PM"
    url "http://nexus-lib.heb.com/nexus/content/repositories/PM-Snapshots"
}
</pre>

Include the following dependencies:

<pre>
compile("com.heb.pm:pm-lib-model:$pmModelVersion-SNAPSHOT")
</pre>

See [build.gradle](build.gradle) for the latest version of this library and the minimum util library. The util library
may not be needed depending on what functions you are using.

Include the following in the list of packages to scan with `@EnableJpaRepositories`:

<pre>
com.heb.pm.dao.oracle
com.heb.pm.util.jpa
com.heb.pm.dao.core.converters
</pre>

Com.heb.pm.util.jpa may not be necessary depending on the features you are using. 

Include the following in the list of packages to scan  with `@EntityScan`:

<pre>
com.heb.pm.dao.core.entity
</pre>


#### Updating the Project Version
Update version in [build.gradle](build.gradle).

If you are making a bug-fix that does not change the interface of any of the classes, raise the revision version only.

If you are adding new entities or other new classes, raise the minor version number.

If you are adding a significant chunk of code (think entities for a new database) or are making a breaking-change, raise
the major version.

#### To Run in IntelliJ
This project makes use of Lombok. To get this to work nicely in Intellij:

1. Install the plugin: In *Preference > Plugins > Marketplace*, search for "Lombok Plugin", install it, and restart IntelliJ.
2. Enable the plugin for development: In *Preferences > Build, Execution, Deployment > Compiler > Annotation Processors* click on "Enable annotation processing".

#### Testing
The first line of testing should be through JUnit tests. Add the table DDL to the appropriate core-*group*-schema.sql
and the data load DML to core-*group*-data.sql. If you create new files, reference them in 
[schema.sql](src/test/resources/schema.sql) and [data.sql](src/test/resources/data.sql) so they are
executed.

If you need to create a repository for testing, it will need to be defined in the [repository](src/main/java/com/heb/pm/dao/core/repository) 
package. I could not figure out how to put them in the src/test/java directory. If you figure it out, change it.

To test with an application using the library, you can install a new version in your local maven distribution by running `./gradlew install`.
The consuming project needs to have *mavenLocal()* defined as a repository.

#### Deploying
Once the code is complete, make sure the version number is updated based on the rules above 
and push the code to Gitlab. Assign the merge request to Darren Danvers.

This project is built and deployed through [Jenkins](https://jenkins.heb.com/jenkins/job/product-management/job/pm-lib-dao/).

Snapshot versions of this library are deployed to the PM-Snapshots repository. To do a snapshot build, log on to the Jenkins
project and ensure the Shapshot and DeployArchive parameters are checked.

Release versions are deployed to the PM repository. To do a release build, log on to the Jenkins project and ensure the
Snapshot parameter is *not* checked and the DeployArchive parameter *is* checked.  


#### To Create a New Entity
Most tables requrie an entity tied to them. The exception is some code tables (described below). When creating
an entity, use the below to help you construct something that matches all the others.

##### Variable Types
In general, all variables should be of the types String, Long, Boolean, Instant, BigDecimal, and enumerated types.

Use object types, not primitives.

For Strings, if the field in the database is of type CHAR, annotate the field as `@Type(type = "fixedLengthChar")`.
This will allow for some specific character handling functionality in Oracle.

If you have a switch that *only* contains Y and N, you can make the variable of type Boolean and use a SwitchToBooleanConverter
to convert between Y and N and true and false. This can only be done if the field only contains Y and N (no nulls or spaces). Otherwise,
use a String.

For whole numbers, use Longs.

For dates and timestamps, use Instants.

For floating point numbers, use BigDecimals. There may be a use case for Double, but the database fields have
a precise precision and scale, and BigDecimal more accurately reflect that. there are four predefined BigDecimal converters
that you can use based on the precision you need: [`BigDecimalSingleScaledConverter`](src/main/java/com/heb/pm/dao/oracle/BigDecimalSingleScaledConverter.java), 
[`BigDecimalDoubleScaledConverter`](src/main/java/com/heb/pm/dao/oracle/BigDecimalDoubleScaledConverter.java),
[`BigDecimalTripleScaledConverter`](src/main/java/com/heb/pm/dao/oracle/BigDecimalTripleScaledConverter.java), and 
[`BigDecimalQuadrupleScaledConverter`](src/main/java/com/heb/pm/dao/oracle/BigDecimalQuadrupleScaledConverter.java).

For code fields, construct a class that extends [`Code`](src/main/java/com/heb/pm/dao/core/entity/Code.java) and use that as the type. If there is no application code that needs to reference
specific values in the table, then construct a regular entity class for the table. If there *is* application code that needs to
refer to specific values in the table, then construct an `Enum` as described below.

##### Embedded IDs
Annotate embedded IDs with the following annotations:

<pre>
@Embeddable
@Data
@Accessors(chain = true)
@EqualsAndHashCode
</pre>

All embedded IDs should implement Serializable.

You do not need to code a toString, equals, or hashCode method. Lombok will generate them.

##### Entities
Annotate entities with the following annotations:

<pre>
@Entity
@Table(name = "<i>table name</i>")
@Data
@Accessors(chain = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
</pre>

All entities should implement Serializable.

The `@Id` field should also be annotated with `@EqualsAndHashCode.Include`. Where the ID field is an embedded class, 
name it `key` and instantiate the variable in the class definition.

For joins, create a variable for the column itself and a separate variable that represents the join. If the
join is a list, instantiate a `LinkedList` in the class itself. This will allow the client to use the list
without having to instantiate it. Annotate joined lists as `@ToString.Exclude`. Typically, the cascade type on the join
should be set to `cascade = CascadeType.ALL`. This will ensure the joined entities are saved when the parent
entity is.

If your need functionality to run before the object is persisted, create a function called `prePersist` 
and annotate it as `@PrePersist`. Examples of things to put here are publication of internal events or copying values
from tied objects.

Make static `of()` methods to create instances of the class. Make at least one method that takes no parameters. Typically,
you should make at least one more that takes enough information to create the key. This may be passing in joined 
objects. [`CandidateSellingUnitExtendedAttribute`](src/main/java/com/heb/pm/dao/core/entity/CandidateSellingUnitExtendedAttribute.java) has an example of this.

For columns with useful defined values, the preferred choice is an externally defined Enum. The second choice is an Enum
defined in the class. The last choice is public constants defined in the class. This route should only be taken when
not all the fields can be defined, but certain values for that field have special meaning.

Type in column names in the `@Column` annotation as lower-case.

You do not need to code a `toString()`, `equals()`, or `hashCode()` method. Lombok will generate them. As `toString()` is generated
off of all attributes by default, complex relationships can cause infinite loops. Where needed, attributes can be excluded
from the `toString()` by annotating it with `@ToString.Exclude`.

##### Enumerated Codes
For code tables that have business logic built around certain values, users cannot add values to the table and have
anything reasonable happen. A value needs to be inserted and code written around it. For that reason, Enums are
the most appropriate structure for these values. If you have a field like this, create an Enum in the codes package
and have it implement the [`Code`](src/main/java/com/heb/pm/dao/core/entity/Code.java) interface. Define the values you need it to have, and make the type of the field
the new type. Create a converter in the converters package to handle marshalling and unmarshalling the field. 
See [`ScaleMaintenanceFunction`](src/main/java/com/heb/pm/dao/core/entity/codes/ScaleMaintenanceFunction.java) 
and [`StringToScaleMaintenanceFunctionConverter`](src/main/java/com/heb/pm/dao/core/converters/StringToScaleMaintenanceFunctionConverter.java) as examples.

##### Using Repositories
If you write a class that needs a repository for one of these entities, refer to the JpaRepository, a defined one. This is
necessary because consuming applications will need to define their own repositories and not use ones defined in this project.
See [`TransactionTasklet`](src/main/java/com/heb/pm/dao/core/batch/TransactionTasklet.java) for an example.
