package net.consensys.streams;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;

import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.stream.LongStream;
import java.util.stream.Stream;


@Fork(value = 2, jvmArgs = {"-Xms4g", "-Xmx4g"})
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
public class ParallelSteamsBenchmark {

    @Param({"1000","1000000"}) int SIZE;

    @Benchmark
    public long sequentialSum() {
        return Stream.iterate(1L, i -> i+1).limit(SIZE).reduce(0L, Long::sum);
    }

    @Benchmark
    public long parallelSum() {
        return Stream.iterate(1L, i -> i+1).limit(SIZE).parallel().reduce(0L, Long::sum);
    }

    @Benchmark
    public long iterativeSum() {
        long result = 0;
        for (int i = 0; i <= SIZE ; i++) {
            result += i;
        }
        return result;
    }

    @Benchmark
    public long rangedSum () {
        return LongStream.rangeClosed(1,SIZE).reduce(0, Long::sum);
    }

    @Benchmark
    public long parallelRangedSum () {
        return LongStream.rangeClosed(1,SIZE).parallel().reduce(0, Long::sum);
    }

    @TearDown(Level.Invocation)
    public void tearDown() {
        System.gc();
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(ParallelSteamsBenchmark.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }
}