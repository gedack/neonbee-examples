# CDS OData Examples

## 1. Getting Started
Set up your local build and development environment:

1. Download and install Node.js (version 12 or higher): https://nodejs.org/en/download/
2. Download and install Yarn: https://yarnpkg.com/en/docs/install
3. Configure NPM & Yarn to use a (e.g. public) repository where the @sap/cds-dk node package is available e.g. https://registry.npmjs.org/
4. Download and install Java 11 e.g. [SapMachine](https://github.com/SAP/SapMachine)

## 2. Build & Run
1. Install the NPM dependencies in the local node_modules directory:
  ```
  npm install
  yarn install
  ```
2. Execute the build:
  ```
  ./gradlew build
  ```
Please note that the ```build``` task will automatically run the ```clean``` task first, to clean up the project and the ```working_dir/models``` directory. 

Furthermore, the build task runs the ```compileCds``` task, which compiles the ```working_dir/models/*.cds``` files to
```working_dir/models/*.csn``` and ```working_dir/models/*.edmx``` files. See ```build.gradle``` for details.

3. Run the main method in the io.neonbee.examples.cds.odata.Application:
  ```
  ./gradlew run
  ```

### 2.1. Clean
After you run the example or e.g. in the case you did changes in the CDS model file, you can run the following command to delete all generated files (CSN, EDMX) again and clean the project up again:
  ```
  ./gradlew clean
  ```

## 3. Use
### 3.1. BookstoreService Metadata
Fetching the OData metadata of the BookstoreService as XML.

- Request:
  ```
  curl -X GET -i -H "Accept: application/xml" http://localhost:8080/odata/BookstoreService/$metadata
  ```
- Response
  ```xml
  HTTP/1.1 200 OK
  OData-Version: 4.0
  ETag: "d12a10a3261de2c438d649ce465aeb99"
  Content-Type: application/xml
  content-length: 394
  X-Instance-Info: NeonBee-9af59b69-dff1-4bad-8781-1729f9379573
  cache-control: no-cache, no-store, must-revalidate
  pragma: no-cache
  expires: 0

  <?xml version="1.0" encoding="UTF-8"?>
  <edmx:Edmx xmlns:edmx="http://docs.oasis-open.org/odata/ns/edmx" Version="4.0">
      <edmx:DataServices>
          <Schema xmlns="http://docs.oasis-open.org/odata/ns/edm" Namespace="BookstoreService">
              <EntityType Name="Books">
                  <Key>
                      <PropertyRef Name="Id" />
                  </Key>
                  <Property Name="Id" Type="Edm.Int32" Nullable="false" />
                  <Property Name="Title" Type="Edm.String" />
                  <Property Name="Author" Type="Edm.String" />
              </EntityType>
              <EntityContainer Name="EntityContainer">
                  <EntitySet Name="Books" EntityType="BookstoreService.Books" IncludeInServiceDocument="false" />
              </EntityContainer>
          </Schema>
      </edmx:DataServices>
  </edmx:Edmx>
  ```

### 3.2. Fetch BookstoreService's Books as XML
- Request:
  ```
  curl -X GET -i -H "Accept: application/xml" http://localhost:8080/odata/BookstoreService/Books
  ```
- Response
  ```xml
  HTTP/1.1 200 OK
  OData-Version: 4.0
  Content-Type: application/xml
  content-length: 1206
  X-Instance-Info: NeonBee-9af59b69-dff1-4bad-8781-1729f9379573
  cache-control: no-cache, no-store, must-revalidate
  pragma: no-cache
  expires: 0

  <a:feed xmlns:a="http://www.w3.org/2005/Atom" xmlns:d="http://docs.oasis-open.org/odata/ns/data" xmlns:m="http://docs.oasis-open.org/odata/ns/metadata" m:context="$metadata#Books/$entity" m:metadata-etag="&quot;d12a10a3261de2c438d649ce465aeb99&quot;">
      <a:id>/BookstoreService/Books</a:id>
      <a:entry>
          <a:title />
          <a:summary />
          <a:updated>2021-04-18T16:52:13Z</a:updated>
          <a:author>
              <a:name />
          </a:author>
          <a:category scheme="http://docs.oasis-open.org/odata/ns/scheme" term="#BookstoreService.Books" />
          <a:content type="application/xml">
              <m:properties>
                  <d:Id m:type="Int32">0</d:Id>
                  <d:Title>Lord Citrange - Mold On The Horizon</d:Title>
                  <d:Author>God</d:Author>
              </m:properties>
          </a:content>
      </a:entry>
      <a:entry>
          <a:title />
          <a:summary />
          <a:updated>2021-04-18T16:52:13Z</a:updated>
          <a:author>
              <a:name />
          </a:author>
          <a:category scheme="http://docs.oasis-open.org/odata/ns/scheme" term="#BookstoreService.Books" />
          <a:content type="application/xml">
              <m:properties>
                  <d:Id m:type="Int32">1</d:Id>
                  <d:Title>Crispbread - Crumbs In The Eye</d:Title>
                  <d:Author>Bernd The Bread</d:Author>
              </m:properties>
          </a:content>
      </a:entry>
  </a:feed>ml version="1.0" encoding="UTF-8"?>
  ```

### 3.2. Fetch BookstoreService's Books as JSON
- Request:
  ```
  curl -X GET -i -H "Accept: application/json" http://localhost:8080/odata/BookstoreService/Books
  ```
- Response
  ```json
  HTTP/1.1 200 OK
  OData-Version: 4.0
  Content-Type: application/json;odata.metadata=minimal
  content-length: 263
  X-Instance-Info: NeonBee-9af59b69-dff1-4bad-8781-1729f9379573
  cache-control: no-cache, no-store, must-revalidate
  pragma: no-cache
  expires: 0

  {
      "@odata.context":"$metadata#Books/$entity",
      "@odata.metadataEtag":"\"d12a10a3261de2c438d649ce465aeb99\"",
      "value":[
          {
              "Id":0,
              "Title":"Lord Citrange - Mold On The Horizon ",
              "Author":"God"
          },
          {
              "Id":1,
              "Title":"Crispbread - Crumbs In The Eye",
              "Author":"Bernd The Bread"
          }
      ]
  }
  ```
### 3.3. Use OData features
- Order by Author ascending
  ```
  curl -X GET -i -H "Accept: application/json" "http://localhost:8080/odata/BookstoreService/Books?$orderby=Author%20asc"
  ```
- Filter by Author 'Bernd The Bread'
  ```
  curl -X GET -i -H "Accept: application/json" "http://localhost:8080/odata/BookstoreService/Books?$filter=Author%20eq%20%27Bernd%20The%20Bread%27"  ```
  ```
- ... and many more. See NeonBee's OData documentation for details.

## 4. Project Explanation
### 4.1. working_dir
The work directory of this example has the name ```working_dir```.
It contains a directory for configs, logs, models and verticles.

#### 4.1.1. The working_dir/configs directory
The ```working_dir/configs``` directory can contain configuration files YAML format.
There are pre-defined configuration possibilities.

#### 4.1.2. Logger Configuration
The logger can be configured via the ```working_dir/configs/logback.xml``` file.

##### 4.1.2.1. ServerVerticle Configuration
NeonBee's ServerVerticle can be configured via the ```working_dir/configs/io.neonbee.internal.verticle.ServerVerticle.yaml``` file.

##### 4.1.2.2. OData URI Mapping:
In this example we changed the default property```endpoints.odata.uriConversion``` to ```strict```.
This has influence to the service name URI mapping.
Using the ```strict``` property, the created OData service of the implemented EntityVerticle will exactly like defined in the EntityVerticles's FullQualifiedName (FQN).

That means that if the FQN is defined as
```
FullQualifiedName FQN = new FullQualifiedName("BookstoreService.Books");
```
then the OData service's metadata will be available at ```http(s)://<host>:<port>/odata/BookstoreService/$metadata```
and the OData service will be available at ```http(s)://<host>:<port>/odata/BookstoreService/Books```.

#### 4.1.3. The working_dir/logs Directory
The ```working_dir/logs``` directory can contain logs, if the logger is configured accordingly via the ```working_dir/configs/logback.xml``` file.

#### 4.1.4. The working_dir/models Directory
The ```working_dir/models``` directory can contain [Core Data Services (CDS)](https://cap.cloud.sap/docs/cds/) files.
Using the Node.js based CDS compiler contained in the [@sap/cds-dk](https://www.npmjs.com/package/@sap/cds-dk) package of the [SAP Cloud Application Programming Model](https://cap.cloud.sap/docs/get-started/) framework
the CDS files can be compiled to the CDS Schema Notation (CSN) and Entity Data Model XML (EDMX) format.

#### 4.1.5. The working_dir/verticles Directory
The ```working_dir/verticles``` directory can contain pre-compiled verticles to be loaded at runtime.
In this example this directory is not used and remains empty.

## 5. Used Standards

### 5.1. OData
OData (Open Data Protocol) is an ISO/IEC approved, OASIS standard that defines RESTful
APIs and it's features. The advantage of OData compared to custom RESTful APIs is
that the OData standard defines the supported features like ordering, navigation
between entities, filtering and so on in detail. This means a user od an OData API
can expect a dedicated set of features. Furthermore, OData defines request and response
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

#### 5.1.1. OData Service Definition
OData services are defined by describing the service's Entity Data Model (EDM). The
central concepts in the EDM are entities, entity sets, and relationships.

#### 5.1.2. OData Entity/Entities
An OData entity is an instance of a defined entity type e.g. Customer, Product, Book, 
or Business Partner. Entity types are nominal structured types with a (compound)
key.

#### 5.1.3. OData Entity Set
An OData entity set is a collection of OData entities.

#### 5.1.4. OData Relationships
OData supports creating or removing relationships between two existing entities.
In OData version 4 terminology, the relationship is a **reference**. References from
one entity to another are represented as navigation properties.

#### 5.1.5. OData Root Endpoint
The OData root endpoint is the main entry point for every incoming OData HTTP
request. This root endpoint handles the routing to the available OData services (Entity
Verticles) internally.

By default, NeonBee exposes the **/odata** HTTP root endpoint but can be configured.
See [working_dir/config/io.neonbee.internal.verticle.ServerVerticle.yaml](working_dir/config/io.neonbee.internal.verticle.ServerVerticle.yaml)
for an example.

## 5.2. Core Data Services (CDS)
NeonNee uses CDS as definition format for models. During build time these models are compiled to the CDS Schema Notation (CSN) and Entity Data Model XML (EDMX) format which are internally used together with an EntityVerticle implementation to automatically create OData service endpoints with features like ordering, sorting, and so on.
See [SAP Cloud Application Programming Model CDS Documentation](https://cap.cloud.sap/docs/cds/) for details.
