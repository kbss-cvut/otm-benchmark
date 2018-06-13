#!/bin/bash

JAVA=/opt/java-8-oracle/bin/java
OUTPUT=benchmark.log
LOGFILE=logback.xml
WARMUPS=20
ROUNDS=100
EXECUTIONS=5
MEMORY=(32m 64m 128m 256m 512m 1g)

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
    echo "*** CREATE ***" >> ../../${OUTPUT}
    ${JAVA} -jar -Xms${2} -Xmx${2} -Dlogback.configurationFile=${LOGFILE} ${1}.jar -w ${WARMUPS} -r ${ROUNDS} -o ../../data/${2}/${1}_create.data create >> ../../${OUTPUT}
    restart_repository
    echo "Batch create..."
    echo "*** BATCH CREATE ***" >> ../../${OUTPUT}
    ${JAVA} -jar -Xms${2} -Xmx${2} -Dlogback.configurationFile=${LOGFILE} ${1}.jar -w ${WARMUPS} -r ${ROUNDS} -o ../../data/${2}/${1}_create-batch.data create-batch >> ../../${OUTPUT}
    restart_repository
    echo "Retrieve..."
    echo "*** RETRIEVE ***" >> ../../${OUTPUT}
    ${JAVA} -jar -Xms${2} -Xmx${2} -Dlogback.configurationFile=${LOGFILE} ${1}.jar -w ${WARMUPS} -r ${ROUNDS} -o ../../data/${2}/${1}_retrieve.data retrieve >> ../../${OUTPUT}
    restart_repository
    echo "Retrieve all..."
    echo "*** RETRIEVE ALL ***" >> ../../${OUTPUT}
    ${JAVA} -jar -Xms${2} -Xmx${2} -Dlogback.configurationFile=${LOGFILE} ${1}.jar -w ${WARMUPS} -r ${ROUNDS} -o ../../data/${2}/${1}_retrieve-all.data retrieve-all >> ../../${OUTPUT}
    restart_repository
    echo "Update..."
    echo "*** UPDATE ***" >> ../../${OUTPUT}
    ${JAVA} -jar -Xms${2} -Xmx${2} -Dlogback.configurationFile=${LOGFILE} ${1}.jar -w ${WARMUPS} -r ${ROUNDS} -o ../../data/${2}/${1}_update.data update >> ../../${OUTPUT}
    restart_repository
    echo "Delete..."
    echo "*** DELETE ***" >> ../../${OUTPUT}
    ${JAVA} -jar -Xms${2} -Xmx${2} -Dlogback.configurationFile=${LOGFILE} ${1}.jar -w ${WARMUPS} -r ${ROUNDS} -o ../../data/${2}/${1}_delete.data delete >> ../../${OUTPUT}
    restart_repository
    cd ../..
}

execute_round()
{
    #AliBaba Benchmark
    echo "Running AliBaba..."
    echo "---------------------------------------" >> ${OUTPUT}
    echo "|               AliBaba               |" >> ${OUTPUT}
    echo "---------------------------------------" >> ${OUTPUT}
    execute_benchmark "alibaba-benchmark" ${1}

    #Empire Benchmark
    echo "Running Empire..."
    echo "---------------------------------------" >> ${OUTPUT}
    echo "|               Empire                |" >> ${OUTPUT}
    echo "---------------------------------------" >> ${OUTPUT}
    execute_benchmark "empire-benchmark" ${1}

    # JOPA Benchmark
    echo "Running JOPA..."
    echo "---------------------------------------" >> ${OUTPUT}
    echo "|               JOPA                  |" >> ${OUTPUT}
    echo "---------------------------------------" >> ${OUTPUT}
    execute_benchmark "jopa-benchmark" ${1}

    # KOMMA Benchmark
    echo "Running KOMMA..."
    echo "---------------------------------------" >> ${OUTPUT}
    echo "|               KOMMA                 |" >> ${OUTPUT}
    echo "---------------------------------------" >> ${OUTPUT}
    execute_benchmark "komma-benchmark" ${1}

    # RDFBeans Benchmark
    echo "Running RDFBeans..."
    echo "---------------------------------------" >> ${OUTPUT}
    echo "|               RDFBeans              |" >> ${OUTPUT}
    echo "---------------------------------------" >> ${OUTPUT}
    execute_benchmark "rdfbeans-benchmark" ${1}
}

execute_benchmark()
{
    start_repository

    mkdir -p data/${1}/

    ####
    #
    # Execute the whole benchmark several times, so that we have output from multiple JVM executions
    #
    ####
    for i in $(seq 1 ${EXECUTIONS})
    do
        echo "Running JVM round ${i}..."
        execute_round ${1}
    done

    stop_repository
}

> ${OUTPUT}
echo "Running benchmark..."

for mem in "${MEMORY[@]}"
do
    echo "Running benchmark with memory size ${mem}"
    execute_benchmark $mem
done

echo "Benchmark finished."
