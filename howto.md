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