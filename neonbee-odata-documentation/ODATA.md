# NeonBee OData Capabilities
NeonBee uses internally [Apache Olingo™](https://olingo.apache.org) and [Vert.x Verticles](https://vertx.io/docs/vertx-core/java/#_verticles) to provide an asynchronous and non-blocking programming approach to enable
developers to easily provide OData version 4 services.

Compared to some other frameworks, you do **not** need any persistence layer like the Java
Persistence API (JPA) and **no** Java Servlets, to provide OData services usign NeonBee.

**Please note that NeonBee does not support each and every OData feature. But the list of supported OData features grows continuously.**

## How-To Provide NeonBee OData Services
To provide an OData service using NeonBee you have to follow the followign steps:

### 1. Define the Core Data Services (CDS) model
In the **models** folder of NeonBee **working directory** create a [Core Data Services (CDS)](https://cap.cloud.sap/docs/cds/) definition file e.g. ```working_dir/models/BookstoreService.cds``` with the following content:
   
  ```
  service BookstoreService {

      entity Books {
          key Id : Integer;
          Title : String;
          Author : String;
      }
      
  }
  ```

### 2. Create the EntityVerticle
Implement an [EntityVerticle](https://github.com/SAP/neonbee/blob/main/src/main/java/io/neonbee/entity/EntityVerticle.java) that fills the defined service with data and handles the Create/Read/Update/Delete (CRUD) actions.
Please note that the EntityVerticle's [FullQualifiedName](https://github.com/apache/olingo-odata4/blob/master/lib/commons-api/src/main/java/org/apache/olingo/commons/api/edm/FullQualifiedName.java) has to match to the definitions you did in the CDS file (Namespace: \<CDS Service Name\>, Name: \<CDS Entity Name\>).

  ```java
    import java.util.List;
    import java.util.Set;
    
    import org.apache.olingo.commons.api.data.Entity;
    import org.apache.olingo.commons.api.data.Property;
    import org.apache.olingo.commons.api.data.ValueType;
    import org.apache.olingo.commons.api.edm.FullQualifiedName;
    
    import io.neonbee.NeonBeeDeployable;
    import io.neonbee.data.DataContext;
    import io.neonbee.data.DataQuery;
    import io.neonbee.entity.EntityVerticle;
    import io.neonbee.entity.EntityWrapper;
    import io.neonbee.logging.LoggingFacade;
    import io.vertx.core.Future;
    
    @NeonBeeDeployable // Load and register this verticle automatically
    public class BooksEntityVerticle extends EntityVerticle {
        
        public static final FullQualifiedName FQN = new FullQualifiedName("BookstoreService.Books");
    
        @Override
        public Future<Set<FullQualifiedName>> entityTypeNames() {
            return Future.succeededFuture(Set.of(FQN));
        }
    
        @Override
        public Future<EntityWrapper> retrieveData(DataQuery query, DataContext context) {
            List<Entity> entities = List.of(
                createBookEntity(0, "Lord Citrange - Mold On The Horizon ", "God"),
                createBookEntity(1, "Crispbread - Crumbs In The Eye", "Bernd The Bread")
            );
            return Future.succeededFuture(new EntityWrapper(FQN, entities));
        }
    
        private static Entity createBookEntity(int id, String title, String author) {
            Entity entity = new Entity();
            entity.addProperty(new Property(null, "Id", ValueType.PRIMITIVE, id));
            entity.addProperty(new Property(null, "Title", ValueType.PRIMITIVE, title));
            entity.addProperty(new Property(null, "Author", ValueType.PRIMITIVE, author));
            return entity;
        }
    
      }
  ```

### 3. Compile the CDS to CSN and EDMX
1. Install Node.js
2. Use the Node.js based [CDS Development Kit @sap/cds-dk](https://www.npmjs.com/package/@sap/cds-dk) to compile the CDS file to XML (EDMX) format e.g. like:
 
  ```
    node node_modules/@sap/cds-dk/bin/cds.js compile working_dir/models/ --service all --to edmx-v4 --dest working_dir/models
  ```
3. Change the XML file's extension from **.xml** to **.edmx**
4. Use the Node.js based [CDS Development Kit @sap/cds-dk](https://www.npmjs.com/package/@sap/cds-dk) to compile the CDS file to CSN format e.g. like:
 
  ```
    node node_modules/@sap/cds-dk/bin/cds.js compile working_dir/models/BookstoreService.cds --service all --to json --dest ./
  ``` 
5. Change the JSON file's extension from **.json** to **.csn**
6. Ensure that the EDMX and CSN files are placed in the **models** directory of NeonBee's working-directory e.g. ```working_dir/models/BookstoreService.edmx``` and ```working_dir/models/BookstoreService.csn```

**Please note that the steps 3 to 6 will be replaced in the future via a NeonBee Gradle plugin to get rid of manual steps. The NeonBee Gradle plugin is not yet available, but is coming soon.**


## About NeonBee OData Services
### Loading Models
NeonBee automatically loads the EDMX/CSN files from the *working_dir/models* folder and/or
from *NeonBee Modules* (JAR files) in the classpath.

### OData Root Endpoint
The OData root endpoint is the central entry point for every incomming OData HTTP
request. This root endpoint handles the routing to the available OData services (Entity
Verticles) internally.

By default NeonBee exposes the **/odata** HTTP root endpoint but can be configured.
See [working_dir/config/io.neonbee.internal.verticle.ServerVerticle.example.yaml](./working_dir/config/io.neonbee.internal.verticle.ServerVerticle.example.yaml)
for details.

## About OData In General
OData (Open Data Protocol) is an ISO/IEC approved, OASIS standard that defines RESTful
APIs and it's features. The advantage of OData compared to custom RESTful APIs is
that the OData standard defines the supported features like ordering, navigation
between entities, filtering and so on in detail. This means a user od an OData API
can expect a dedicated set of features. Furthermore OData defines request and response
headers, status codes, HTTP methods, URL conventions, media types, payload formats,
query options, tracking changes, functions/actions, batch requests and everything
else to provide a technology independent definition of an OData service.

OData RESTful APIs are very easy to consume as they provide metadata for implemented
services. This OData metadata of an OData service is a machine-readable description
of the data model of the APIs and enables the creation of generic client proxies
and tools. For instance this is used by modern frontend frameworks like [OpenUI5](https://openui5.org).

Here you can find links to the most important OData version 4 specifications:

* [OData Protocol Specification](http://docs.oasis-open.org/odata/odata/v4.0/odata-v4.0-part1-protocol.html)
* [URL Conventions](http://docs.oasis-open.org/odata/odata/v4.0/odata-v4.0-part2-url-conventions.html)
* [Common Schema Definition Language](http://docs.oasis-open.org/odata/odata/v4.0/odata-v4.0-part3-csdl.html)
* [JSON Format Specification, ](http://docs.oasis-open.org/odata/odata-json-format/v4.0/odata-json-format-v4.0.html)
* [ABNF](http://docs.oasis-open.org/odata/odata/v4.0/errata02/os/complete/abnf/)
* [Standardized Vocabularies](http://docs.oasis-open.org/odata/odata/v4.0/errata02/os/complete/vocabularies/)
* [Aggregation Extension](http://docs.oasis-open.org/odata/odata-data-aggregation-ext/v4.0/odata-data-aggregation-ext-v4.0.html)

See [OData.org](https://www.odata.org/) for more information about OData libraries,
tools and so on.

### OData Service Definition
OData services are defined by describing the service's Entity Data Model (EDM). The
central concepts in the EDM are entities, entity sets, and relationships.

### OData Entity/Entities
An OData entity is an instance of an defined entity type e.g. Customer, Product
or Business Partner. Entity types are nominal structured types with a (compound)
key.

### OData Entity Set
An OData entity set is a collection of OData entities.

### Entity Data Model (EDM)
OData has a defined type system to fully describe all the entities in the metadata.
This is the Entity Data Model (EDM).

### Entity Data Model XML (EDMX)
To define a OData version 4 conform service the EDMX (Entity Data Model XML) format
is used to describe the service definition. The file extension has to be *edmx.

### OData Relationships
OData supports creating or removing relationships between two existing entities.
In OData version 4 terminology, the relationship is a **reference**. References from
one entity to another are represented as navigation properties.

**Please note that this is currently not supported by NeonBee.**

### OData URL Components
#### Literals

##### Primitive Literals
Primitive literals can appear in the resource path as key property values, and in
the query part, for example, as operands in filter expressions. They are represented
according to the primitiveLiteral rule in [OData ABNF](http://docs.oasis-open.org/odata/odata/v4.0/errata02/os/complete/abnf/odata-abnf-construction-rules.txt).

Here you can find some example expressions using primitive literals:

```
NullValue eq null
TrueValue eq true
FalseValue eq false
IntegerValue lt -128
DecimalValue eq 34.95
StringValue eq 'Say Hello,then go'
DateValue eq 2012-12-03
DateTimeOffsetValue eq 2012-12-03T07:16:23Z
TimeOfDayValue eq 07:59:59.999
GuidValue eq 01234567-89ab-cdef-0123-456789abcdef
Int64Value eq 0
```

##### Complex and Collection Literals
Complex literals and collection literals in URLs are represented as JSON objects
and arrays according to the arrayOrObject rule in [OData-ABNF]. Such literals MUST
NOT appear in the path portion of the URL but can be passed to bound functions and
function imports in path segments by using parameter aliases.

Object member values and array items can be expressions, including other objects
and arrays, arithmetic expressions, property names, and of course primitive values.

Note that the special characters *{*, *}*, *[*, *]*, and *"* MUST be percent-encoded
in URLs although some browsers will accept and pass them on unencoded.

Here you can find an example of a collection of string literals:
```
/odata/Example.Service/ProductsByColors(colors=@c)?@c=["red","green"]
```

Here you can find an example of a filter query which checks whether a pair of properties
has one of several possible pair values:
```
$filter=[FirstName,LastName] in [["John","Doe"],["Jane","Smith"]]
```

**Please note that this is currently not supported by NeonBee.**

##### Null Literal
The *null* literal can be used to compare a value to null, or to pass a null value
to a function.

### EDM Data Types and Respective Java Data Types
#### Date and Time in OData
To represent date and time information, the OData specification version 4.0 knows
the following types:

- Edm.Date
- Edm.TimeOfDay
- Edm.DateTimeOffset
- Edm.Duration

##### Edm.Date
Edm.DateTime represents a date and a time in UTC (formerly, Greenwich Mean Time).
It MUST NOT contain a time-zone offset:

Java Format: yyyy-MM-

Example: 2000-01-

##### Edm.TimeOfDay
Edm.TimeOfDay represents the clock time from 00:00 to 23:59:59.99999999999

Java Format: HH:MM:SS.fractionalSeconds, where fractionalSeconds =1\*12DIGIT e.g.
04:03:05.07900

Example: Time from 00:00 to 23:59:59.9999999999

##### Edm.DateTimeOffset
Edm.DateTimeOffset represents date and time with a time-zone offset but without leap
seconds. Edm.DateTimeOffset adds time zone information relative to UTC.

Example:

- 2000-01-01T16:00:00.000Z
- 2000-01-01T16:00:00.000-09:00
- 2002-10-10T17:00:00Z
- 2020-04-07T18:08:19+00:00
- 2020-04-07T18:08:19Z
- 20200407T180819Z

##### Edm.Duration
Edm.Duration represents a signed duration in days, hours, minutes, and (sub)second.

#### Supported EDM Types
##### Primitive Types
The following primitive types are supported by NeonBee.

- Edm.Boolean: Binary-valued logic
- Edm.Date: Date without a time-zone offset
- Edm.DateTimeOffset: Date and time with a time-zone offset, no leap seconds
- Edm.Decimal: Numeric values with fixed precision and scale
- Edm.Double: IEEE 754 binary64 floating-point number (15-17 decimal digits)
- Edm.Int16: Signed 16-bit integer
- Edm.Int32: Signed 32-bit integer
- Edm.Int64: Signed 64-bit integer
- Edm.String: Sequence of UTF-8 characters
- Edm.TimeOfDay: Clock time 00:00-23:59:59.999999999999

#### Unsupported EDM Types
##### Complex Types
Complex types are basically collections of scalar types and currently not supported
by NeonBee.

## Entity Verticle
Entity verticles are an specific abstraction of NeonBee's Data Verticle concept. While
Data Verticles can handle most kind of data for data processing, it is mostly more
convenient to know how the data is structured. To define the data structure, Vert.x
utilizes the OASIS Open Data Protocol (OData) 4.0 standard, which is also internationally
certified by ISO/IEC. That means Entity Verticles are essentially data verticles
dealing with Olingo OData entities. This provides NeonBee the possibility to expose these
Entity Verticles via a standardized OData API endpoint, as well as to perform many optimizations
when data is processed.

## Supported OData Features
### Read Data
#### General

|Action                                                            |HTTP Request                                   |
|:-----------------------------------------------------------------|:----------------------------------------------|
|Read service metadata                                             |GET /odata/Example.Service/\$metadata          |
|Read entity set                                                   |GET /odata/Example.Service/Products            |
|Read single property of an dedicated entity identified by it's key|GET /odata/Example.Service/Products('ID1')/name|

#### Filtering
Filtering is possible by providing a $filter query.

##### Operators
In general, the following operators can be used within a filter query: *not*, *and*,
*or*, *eq*, *ne*, *ge*, *gt*, *le*, *lt*, *in*. Especially *and* and *or* can be
used to combine multiple predicates in the filter query.

|Action                                                                                                  |HTTP Request                                                                                                     |
|:-------------------------------------------------------------------------------------------------------|:----------------------------------------------------------------------------------------------------------------|
|Read entity set with filter and comparison via equals                                                   |GET /odata/Example.Service/Products?$filter=name eq 'Awesome Product 1'                                          |
|Read entity set with filter and comparison via ne                                                       |GET /odata/Example.Service/Products?\$filter=name ne 'Smart Product 1337'                                        |
|Filter entity set by a property. Provide a list of values to be filtered out.                           |GET /odata/Example.Service/Products?\$filter=name in ('Awesome Product 1', 'Smart Product 42')                   |
|Filter entity set by multiple properties thru combining multiple predicates via *and* and *or* operator.|GET /odata/Example.Service/Products?\$filter=name in ('Awesome Product 1', 'Smart Product 42') and id ne 'ID0815'|
|Filter Edm.Date property by date in yyyy-MM-dd format                                                   |GET /odata/Example.Service/Products?\$filter=productionDate eq 2014-05-24                                        |
|Filter Edm.Date property by boolean value                                                               |GET /odata/Example.Service/Products?\$filter=latestVersion eq true                                               |
|Filter Edm.DateTimeOffset in format yyyy-mm-ddThh:mm:ss.nnnnnnZ format                                  |GET /odata/Example.Service/Products?\$filter=deliveryDate eq 2013-04-23T08:47:11.000004Z                         |
|Filter Edm.DateTimeOffset in format yyyy-mm-ddThh:mm:ssZ format                                         |GET /odata/Example.Service/Products?\$filter=deliveryDate eq 2010-01-20T11:30:05Z                                |
|Filter Edm.Double property with greater then comparison                                                 |/odata/Example.Service/Products?\$filter=price gt 2.35                                                           |
|Filter Edm.Double property with greater then or equals comparison                                       |/odata/Example.Service/Products?\$filter=price ge 2.35                                                           |
|Filter Edm.Double property with lower then comparison                                                   |/odata/Example.Service/Products?\$filter=price lt 2.35                                                           |
|Filter Edm.Double property with lower then or equals comparison                                         |/odata/Example.Service/Products?\$filter=price le 2.35                                                           |

##### String Functions
The following string functions can be used with a filter query: contains, tolower,
toupper, substring, startswith, endswith, trim, concat and length.

|Action                                                                                                                  |HTTP Request                                                                                                              |
|:-----------------------------------------------------------------------------------------------------------------------|:-------------------------------------------------------------------------------------------------------------------------|
|Filter entity set by a property using the contains function. Please note that the contains function is case sensitively.|GET /odata/Example.Service/Products?\$filter=contains(name, 'Product')                                                    |
|Filter entity set by name using the tolower function                                                                    |GET /odata/Example.Service/Products?\$filter=tolower(name) eq 'smart product 42'                                          |
|Filter entity set by name using the toupper function                                                                    |GET /odata/Example.Service/Products?\$filter=toupper(name) eq 'AWESOME PRODUCT 1'                                         |
|Filter entity set by name using the substring function                                                                  |GET /odata/Example.Service/Products?\$filter=substring(name, 13) eq 'T 42'                                                |
|Filter entity set by name using the substring function                                                                  |GET /odata/Example.Service/Products?\$filter=substring(name, 13, 1) eq 'T'                                                |
|Filter entity set by name using the startswith function                                                                 |GET /odata/Example.Service/Products?\$filter=startswith(name, 'Awesome')                                                  |
|Filter entity set by name using the startswith function but using not to negate the expression                          |GET /odata/Example.Service/Products?\$filter=not startswith(name, 'Awesome')                                              |
|Filter entity set by name using the endswith function                                                                   |GET /odata/Example.Service/Products?\$filter=endswith(name, 'duct 42')                                                    |
|Filter entity set by name using the endswith function but using not to negate the expression                            |GET /odata/Example.Service/Products?\$filter=not endswith(name, 'duct 42')                                                |
|Filter entity set by name using the trim function to replace leading and trailing whitespace characters                 |GET /odata/Example.Service/Products?\$filter=trim(name) eq 'smart product 42'                                             |
|Filter entity set by name using the concat function                                                                     |GET /odata/Example.Service/Products?\$filter=concat(name, '-POSTFIX') eq 'Awesome Product 1-POSTFIX'                      |
|Filter entity set by name using the concat function nested                                                              |GET /odata/Example.Service/Products?\$filter=concat(concat(name, '-DELIMITER-'), ID) eq 'Awesome Product 1-DELIMITER-ID1'"|
|Filter entity set by name using the indexof function                                                                    |GET /odata/Example.Service/Products?\$filter=indexof(name, ' Product') eq 5                                               |
|Filter entity set by name using the length function                                                                     |GET /odata/Example.Service/Products?\$filter=length(name) eq 18                                                           |

##### Date Functions
|Action                                       |HTTP Request                                                                              |
|:--------------------------------------------|:-----------------------------------------------------------------------------------------|
|Filter Edm.Date property by year             |GET /odata/Example.Service/Products?\$filter=year(productionDate) eq 2010                 |
|Filter Edm.Date property by month            |GET /odata/Example.Service/Products?\$filter=month(productionDate) eq 5                   |
|Filter Edm.Date property by day              |GET /odata/Example.Service/Products?\$filter=day(productionDate) eq 24                    |
|Filter Edm.Date property by hour             |GET /odata/Example.Service/Products?\$filter=hour(productionDate) eq 6                    |
|Filter Edm.Date property by minute           |GET /odata/Example.Service/Products?\$filter=minute(productionDate) eq 46                 |
|Filter Edm.Date property by second           |GET /odata/Example.Service/Products?\$filter=second(productionDate) eq 59                 |
|Filter Edm.Date property by fractionalseconds|GET /odata/Example.Service/Products?\$filter=fractionalseconds(productionDate) eq 0.000002|

#### Counting
|Action                                           |HTTP Request                                                                |
|:------------------------------------------------|:---------------------------------------------------------------------------|
|Count entities of an entity set                  |GET /odata/Example.Service/Products/\$count                                 |
|Count entities of an entity set with filter query|GET /odata/Example.Service/Products/\$count?\$filter=contains(name, 'smart')|

#### Ordering
|Action                                     |HTTP Request                                           |
|:------------------------------------------|:------------------------------------------------------|
|Order entity set by name in ascending order|GET /odata/Example.Service/Products?\$orderby=name asc |
|Order entity set by name in ascending order|GET /odata/Example.Service/Products?\$orderby=name     |
|Order entity set by name in decending order|GET /odata/Example.Service/Products?\$orderby=name desc|

#### Skip and Top
|Action                                                                                             |HTTP Request                                         |
|:--------------------------------------------------------------------------------------------------|:----------------------------------------------------|
|Fetch the entity set but return only the top 10 products                                           |GET /odata/Example.Service/Products?\$top=10         |
|Fetch the entity set but skip the top 5 products                                                   |GET /odata/Example.Service/Products?\$skip=5         |
|Fetch the entity set but skip the top 5 products and return only the top 10 products (product 6-15)|GET /odata/Example.Service/Products?\$skip=5&\$top=10|

## Unsupported OData Features
### Read Data
#### Filtering
Currently the *has*, *add*, *sub*, *mul*, *div* and *mod* operators are not supported
in filter queries.

##### Math Functions
The math functions *round*, *floor* and *ceiling* are currently not supported in
filter queries.

##### Type Functions
Currently the *isof* type function is not supported in filter queries.

### OData Service Usage
Here you can find examples how to use OData services.

#### Requesting an Entity Collection
The request below returns the the collection of Products:

```
GET /odata/Example.Service/Products
```

#### Requesting an Individual Entity by ID
The request below returns an individual entity of type Person by the given ID "42-133".

```
GET /odata/Example.Service/Products('42-1337')
```

#### Requesting the Entity Collection's Size
The request below returns the number of Product Entities, that means the collection
size as text/plain response.

```
GET /odata/Example.Service/Products/$count
```

#### Requesting an Individual Property
To address an entity property you just need to append a path segment containing property
name to the URL of the entity.

```
GET /odata/Example.Service/Products('42-1337')/name
```

#### Create Entity of a Collection
The request below returns the the collection of Products.

```
GET /odata/Example.Service/Products
```

## Our Road Ahead
We would like to even simplify the creation and retrieval of entities. So, what you
see right now in the the conversion of data from data verticles to actual entities
is a manual mapping between data from data verticles to entities. We would like to
simplify this step to an degree where you could even utilize customizing to do this
conversion automatically.

## Glossary
|Term                                    |Definition                                                                                                                                                       |
|:---------------------------------------|:----------------------------------------------------------------------------------------------------------------------------------------------------------------|
|Common Schema Definition Language (CSDL)|The Common Schema Definition Language (CSDL) defines an XML-based representation of the entity model exposed by an OData service.                                |
|OData Metadata                          |Every defined OData service provides the Entity Data Model (EDM), consisting of EntitySets, Entities, ComplexTypes and Scalar Types via the *$metadata* endpoint.|

