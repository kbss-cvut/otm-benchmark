# How to

This guide explains how to install, build and run the benchmark.

## Requirements

The benchmark requires the following platforms.

Build:

* JDK 8 (note that JOPA currently does not support later versions of Java)
* Apache Maven 3.3 or later (to build the benchmark artifacts)

Execution:

* RDF4J-compatible repository server
   * The benchmark has been tested on [RDF4J](http://rdf4j.org/), [GraphDB](http://graphdb.ontotext.com/), [Virtuoso](https://virtuoso.openlinksw.com/)
* A Debian shell (e.g., `dash`, `bash`)*

\* _A shell is required to execute the benchmark using the attached scripts_


## Configuration

Configuration of the OTM framework-specific runners is done through `config.properties` files in the respective modules. The files can be found in `src/main/resources`.

For example, the _AliBaba_ configuration is thus in `alibaba-benchmark/src/main/resources/config.properties`.

Each configuration has two parameters:
* `url` - URL of the repository to be used for benchmark execution
* `memory.runtime` - time in milliseconds specifying for how long the memory benchmark should be executed. During this time, garbage collection data are gathered for later analysis.

The only exception to these configuration rules is _Empire_ for which repository URL is set in a special file called `empire.configuration` 
(placed in the same location as `config.properties`). This is required by _Empire_ itself.


## Build

To build the benchmark (including runners of the individual OTM frameworks), invoke `mvn clean package` in the root benchmark directory.

The build produces JAR files for each of the OTM framework benchmark runners, which can be found in the `target` directory of each respective module. These
JAR files are used by the benchmark executor scripts.

## Execution

The benchmark cleans up the repository after each round and uses a new persistence context for each round.

The execution can be configured using the following parameters:

* **-w** is the number of warmup rounds, which are not measured.
* **-r** is the number of measured rounds.
* **-f** is the scaling factor, which configures the size of the benchmark dataset. Default is 1.
* **-o** is the file into which individual round execution times should be written. This is useful for separate processing of the raw execution times e.g. in [R](https://www.r-project.org/).
* **-m** is the file into which memory tracking statistics should be output. These are collected using `jstat`.

### Performance - Using `benchmark.sh`

The easiest way to execute the benchmark is to use the associated `benchmark.sh` script. This script contains a predefined configuration which:

* Executes the benchmark using each of the supported OTM frameworks
* Runs the benchmark for each framework with multiple heap size configurations (using the `Xmx` and `Xms` JVM parameters)
* Uses a GraphDB instance as a repository
* Restarts the GraphDB between operation executions
* Runs the benchmark repeatedly to get performance data from multiple JVM executions
* Outputs raw performance data (using the **-o** switch into a directory called `data`) as well as overall statistics (written into `benchmark.log`)

The script expects GraphDB to be installed in `~/Java/graphdb` and writes GraphDB process id to `/tmp/.graphdbpid` (it is used to stop the GraphDB instance when needed).

The current configuration is:
* Execution count: **5**
* Heap Memory sizes: **32MB**, **64MB**, **128MB**, **256MB**, **512MB**, **1GB**
* Warmup rounds: **20**
* Measured rounds: **100**


### Memory - Using `memory-benchmark-gc.sh`

Memory benchmark using the `memory-benchmark-gc.sh` is the preferred way of benchmarking memory usage of the tested libraries. It runs a subset of operations of the 
performance benchmark in a loop for a predefined time interval (configured using the `config.properties` files described above), outputting garbage collection (GC) data into
a preconfigured file. The GC logging is set using the `-XX:+PrintGCDetails -XX:+PrintGCTimeStamps -Xloggc` JVM parameters.

The script runs the memory benchmark for each of the supported OTM framework, restarting the GraphDB repository between running individual frameworks.

All the GC data are written into separate files named after the respective OTM frameworks (e.g., `alibaba-gc.log`) and stored in a directory called `memory`.

### Memory - Using `memory-benchmark-jstat.sh`

**`jstat`-based memory benchmark is not used anymore because of its inconclusive results.**

## Adapting Benchmark to an OTM Framework

Adapting the benchmark to a new library consists of creating implementations of several key interfaces and abstract classes. The benchmark
algorithm itself is implemented in the _core_ module, the OTM benchmarking modules need to provide only parts specific to the evaluated framework.

The best way to integrate it into the benchmark is to create a new Maven module and add it into the root `pom.xml`, so that it is built together
with all the other modules.

### Model

The object model is specified in the _core_ module in the form of interfaces. Modules testing OTM frameworks need to provide
concrete implementations (or their own interfaces from which proxies are generated by the OTM framework) specifying mapping in
the form supported by the selected OTM framework. `Event`, `Occurrence` and `OccurrenceReport` use generics, so that implementations
can use the correct concrete type.

### CRUD Operations

Benchmark operations (create, batch create, retrieve, retrieve all, update, delete) are performed by implementations of CRUD operation
executors - `Saver`, `Finder`, `Updater`, `Deleter`. These interfaces can be found in the `cz.cvut.kbss.benchmark.util` package in _core_.

Instructions for implementations are provided in javadoc of the respective executors.

### DataGenerator

`DataGenerator` provides the benchmark with data. Again, the configuration of how many instances of what classes and how to interconnect them
is in `DataGenerator`. Concrete subclasses only need to provide factory methods for creating instances of the model classes and invoke
`generate` to pre-generate test data. Consult the class' javadoc for details.

### Benchmark Runners

Benchmark runners represent implementations for the individual benchmark operations. They should implement suitable setup methods, mostly to 
create `DataGenerator`, let it generate test data and initialize the OTM framework. Then, in `execute`, they invoke appropriate superclass methods
for the benchmarked operation, e.g. batch create runner should invoke `executeBatchCreate`, passing operation executor as a parameter.

It is also important to implement an tear down method, which should clear the repository after each round (by invoking `BenchmarkUtil.clearRepository()` with
repository URL). Usually, it is good to extract this behaviour into a common superclass for all benchmark runners.

### Benchmark Application

Last, it is necessary to provide an implementation of `AbstractBenchmark` (`AbstractMemoryBenchmark`), which creates an appropriate benchmark runner
based on application CLI parameters. This implementation should have a `main` method, which create a new instance of the benchmark application class
and invoke `run` with command line parameters.

To get a better understanding of how to extends the benchmark for a concrete OTM framework, see the classes and interfaces in module _core_ and their
implementations, for example, in module _jopa-benchmark_ or _empire-benchmark_.

