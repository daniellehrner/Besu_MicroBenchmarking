# Besu_MicroBenchmarking
Some microbenchmarks on Hyperledger Besu

## Prerequisites
AsyncProfiler setup:
`-Djava.library.path=/path/to/your/async-profiler-3.0-macos/lib`

## Building

```shell
mvn install
```

## Running

### Without profiling

```shell
java -jar target/Besu_MicroBenchmarking-1.0-SNAPSHOT.jar
```

### With profiling (async-profiler)

```shell
java -XX:+UnlockDiagnosticVMOptions -jar target/Besu_MicroBenchmarking-1.0-SNAPSHOT.jar --profile async:output=jfr;event=cpu
```

### Configure iterations (defaults to 50 for warmup, 50 for measurement)

```shell
java -jar target/Besu_MicroBenchmarking-1.0-SNAPSHOT.jar --iterations=1
```
or
```shell
java -jar target/Besu_MicroBenchmarking-1.0-SNAPSHOT.jar -i=1
```
