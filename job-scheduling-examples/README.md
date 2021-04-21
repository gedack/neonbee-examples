# Job Scheduling Examples
The DailyJobVerticle in this example shows how to implement a [JobVerticle](https://github.com/SAP/neonbee/blob/main/src/main/java/io/neonbee/job/JobVerticle.java)
to execute a piece of code based on a defined [JobSchedule](https://github.com/SAP/neonbee/blob/main/src/main/java/io/neonbee/job/JobSchedule.java).

## 1. Getting Started
Set up your local build and development environment:

1. Download and install Java 11 e.g. [SapMachine](https://github.com/SAP/SapMachine)

## 2. Build & Run
1. Build and run the main method in the io.neonbee.examples.job.schedule.Application:
  ```
  ./gradlew build run
  ```

## 3. Use
Take a look at the **DailyJobVerticle** implementation and at the log/console output.
The **DailyJobVerticle** will execute the implementation on Monday to Friday every 5 seconds,
but on Saturday and Sunday only every 15 seconds.

## 4. Project Explanation
### 4.1. working_dir
The work directory of this example has the name ```working_dir```.
It contains a directory for configs, logs, models and verticles.

#### 4.1.1. The working_dir/configs directory
The ```working_dir/configs``` directory can contain configuration files YAML format.
There are pre-defined configuration possibilities.

#### 4.1.2. Logger Configuration
The logger can be configured via the ```working_dir/configs/logback.xml``` file.
In this example the log level is set to **DEBUG**.

##### 4.1.2.1. ServerVerticle Configuration
NeonBee's ServerVerticle can be configured via the ```working_dir/configs/io.neonbee.internal.verticle.ServerVerticle.yaml``` file.

#### 4.1.3. The working_dir/logs Directory
The ```working_dir/logs``` directory can contain logs, if the logger is configured accordingly via the ```working_dir/configs/logback.xml``` file.

#### 4.1.4. The working_dir/models Directory
The ```working_dir/models``` directory can contain [Core Data Services (CDS)](https://cap.cloud.sap/docs/cds/) files.
Using the Node.js based CDS compiler contained in the [@sap/cds-dk](https://www.npmjs.com/package/@sap/cds-dk) package of the [SAP Cloud Application Programming Model](https://cap.cloud.sap/docs/get-started/) framework
the CDS files can be compiled to the CDS Schema Notation (CSN) and Entity Data Model XML (EDMX) format.
In this example this directory is not used and remains empty.

#### 4.1.5. The working_dir/verticles Directory
The ```working_dir/verticles``` directory can contain pre-compiled verticles to be loaded at runtime.
In this example this directory is not used and remains empty.
