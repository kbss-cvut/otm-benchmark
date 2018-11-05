#!/bin/bash

###
#
# This memory benchmark is based on instructing JVM to output GC details.
#
###

JAVA=/opt/java-8-oracle/bin/java
LOGFILE=logback.xml
MEM=40m
DATA_DIR=memory

GRAPHDB_HOME=~/Java/graphdb-free-8.4.1/
GRAPHDB_PIDFILE=/tmp/.graphdbpid
start_graphdb()
{
    ${GRAPHDB_HOME}/bin/graphdb -d -s -p ${GRAPHDB_PIDFILE};
    sleep 20    # Sleep to give GraphDB time to start
}

stop_graphdb()
{
    kill $(<"${GRAPHDB_PIDFILE}")
    sleep 2
}

restart_graphdb()
{
    stop_graphdb
    start_graphdb
}

start_repository()
{
    start_graphdb
}

stop_repository()
{
    stop_graphdb
}

restart_repository()
{
    stop_repository
    start_repository
}

clear_repository()
{
    curl -X DELETE http://localhost:7200/repositories/benchmark/statements
}

start_repository

echo "Executing benchmark..."
mkdir -p ${DATA_DIR}/${MEM}/

echo "AliBaba"
cd alibaba-benchmark/target
${JAVA} -cp alibaba-benchmark.jar -Dlogback.configurationFile=${LOGFILE} -Xms${MEM} -Xmx${MEM} -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -Xloggc:alibaba-gc.log cz.cvut.kbss.benchmark.alibaba.AliBabaMemoryBenchmark
mv alibaba-gc.log ../../${DATA_DIR}/${MEM}/
cd ../..

clear_repository

echo "Empire"
cd empire-benchmark/target
${JAVA} -cp empire-benchmark.jar -Dlogback.configurationFile=${LOGFILE} -Xms${MEM} -Xmx${MEM} -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -Xloggc:empire-gc.log cz.cvut.kbss.benchmark.empire.EmpireMemoryBenchmark
mv empire-gc.log ../../${DATA_DIR}/${MEM}/
cd ../..

clear_repository

echo "JOPA"
cd jopa-benchmark/target
${JAVA} -cp jopa-benchmark.jar -Dlogback.configurationFile=${LOGFILE} -Xms${MEM} -Xmx${MEM} -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -Xloggc:jopa-gc.log cz.cvut.kbss.benchmark.jopa.JopaMemoryBenchmark
mv jopa-gc.log ../../${DATA_DIR}/${MEM}/
cd ../..

clear_repository

echo "KOMMA"
cd komma-benchmark/target
${JAVA} -cp komma-benchmark.jar -Dlogback.configurationFile=${LOGFILE} -Xms${MEM} -Xmx${MEM} -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -Xloggc:komma-gc.log cz.cvut.kbss.benchmark.komma.KommaMemoryBenchmark
mv komma-gc.log ../../${DATA_DIR}/${MEM}/
cd ../..

clear_repository

echo "RDFBeans"
cd rdfbeans-benchmark/target
${JAVA} -cp rdfbeans-benchmark.jar -Dlogback.configurationFile=${LOGFILE} -Xms${MEM} -Xmx${MEM} -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -Xloggc:rdfbeans-gc.log cz.cvut.kbss.benchmark.rdfbeans.RdfBeansMemoryBenchmark
mv rdfbeans-gc.log ../../${DATA_DIR}/${MEM}/
cd ../..

clear_repository

stop_repository
echo "Benchmark finished."
