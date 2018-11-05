#!/bin/bash

###
#
# This memory benchmark is based on memory sampling using jstat
#
###

JAVA=/opt/java-8-oracle/bin/java
LOGFILE=logback.xml
WARMUPS=5
ROUNDS=5
DATA_DIR=memory-jstat

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

execute_benchmark()
{
    cd ${1}/target
    echo "Create..."
    echo "*** CREATE ***"
    > ../../memory/${1}_create.data
    ${JAVA} -jar -Dlogback.configurationFile=${LOGFILE} ${1}.jar -w ${WARMUPS} -r ${ROUNDS} -m ../../${DATA_DIR}/${1}_create.data create
    restart_repository
    echo "Batch create..."
    echo "*** BATCH CREATE ***"
    > ../../memory/${1}_create-batch.data
    ${JAVA} -jar -Dlogback.configurationFile=${LOGFILE} ${1}.jar -w ${WARMUPS} -r ${ROUNDS} -m ../../${DATA_DIR}/${1}_create-batch.data create-batch
    restart_repository
    echo "Retrieve..."
    echo "*** RETRIEVE ***"
    > ../../memory/${1}_retrieve.data
    ${JAVA} -jar -Dlogback.configurationFile=${LOGFILE} ${1}.jar -w ${WARMUPS} -r ${ROUNDS} -m ../../${DATA_DIR}/${1}_retrieve.data retrieve
    restart_repository
    echo "Retrieve all..."
    echo "*** RETRIEVE ALL ***"
    > ../../memory/${1}_retrieve-all.data
    ${JAVA} -jar -Dlogback.configurationFile=${LOGFILE} ${1}.jar -w ${WARMUPS} -r ${ROUNDS} -m ../../${DATA_DIR}/${1}_retrieve-all.data retrieve-all
    restart_repository
    echo "Update..."
    echo "*** UPDATE ***"
    > ../../memory/${1}_update.data
    ${JAVA} -jar -Dlogback.configurationFile=${LOGFILE} ${1}.jar -w ${WARMUPS} -r ${ROUNDS} -m ../../${DATA_DIR}/${1}_update.data update
    restart_repository
    echo "Delete..."
    echo "*** DELETE ***"
    > ../../memory/${1}_delete.data
    ${JAVA} -jar -Dlogback.configurationFile=${LOGFILE} ${1}.jar -w ${WARMUPS} -r ${ROUNDS} -m ../../${DATA_DIR}/${1}_delete.data delete
    restart_repository
    cd ../..
}

echo "Running benchmark..."
start_repository

#AliBaba Benchmark
echo "Running AliBaba..."
echo "---------------------------------------"
echo "|               AliBaba               |"
echo "---------------------------------------"
execute_benchmark "alibaba-benchmark"

#Empire Benchmark
echo "Running Empire..."
echo "---------------------------------------"
echo "|               Empire                |"
echo "---------------------------------------"
execute_benchmark "empire-benchmark"

# JOPA Benchmark
echo "Running JOPA..."
echo "---------------------------------------"
echo "|               JOPA                  |"
echo "---------------------------------------"
execute_benchmark "jopa-benchmark"


# KOMMA Benchmark
echo "Running KOMMA..."
echo "---------------------------------------"
echo "|               KOMMA                 |"
echo "---------------------------------------"
execute_benchmark "komma-benchmark"

# RDFBeans Benchmark
echo "Running RDFBeans..."
echo "---------------------------------------"
echo "|               RDFBeans              |"
echo "---------------------------------------"
execute_benchmark "rdfbeans-benchmark"

stop_repository
echo "Benchmark finished."
