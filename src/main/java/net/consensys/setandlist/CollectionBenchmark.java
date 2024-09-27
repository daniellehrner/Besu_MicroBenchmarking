package net.consensys.setandlist;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.profile.AsyncProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class CollectionBenchmark {

    private ArrayList<Bytes32> arrayList;
    private HashSet<Bytes32> hashSet;
    private Bytes32[] testElements;
    private int elementCount = 1_000_000;

    @Setup(Level.Iteration)
    public void setup() {
        arrayList = new ArrayList<>(elementCount);
        hashSet = new HashSet<>(elementCount);
        testElements = new Bytes32[elementCount];

        // Generate random Bytes32 elements
        for (int i = 0; i < elementCount; i++) {
            Bytes32 element = Bytes32.randomBytes32();
            testElements[i] = element;
        }
    }

    @Benchmark
    public void testArrayListAdd() {
        for (Bytes32 element : testElements) {
            arrayList.add(element);
        }
    }

    @Benchmark
    public void testHashSetAdd() {
        for (Bytes32 element : testElements) {
            hashSet.add(element);
        }
    }

    @Benchmark
    public void testArrayListContains() {
        for (Bytes32 element : testElements) {
            arrayList.contains(element);
        }
    }

    @Benchmark
    public void testHashSetContains() {
        for (Bytes32 element : testElements) {
            hashSet.contains(element);
        }
    }

    @TearDown(Level.Iteration)
    public void tearDown() {
        arrayList.clear();
        hashSet.clear();
    }

    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(CollectionBenchmark.class.getSimpleName())
                .addProfiler(AsyncProfiler.class)
                .build();

        new Runner(opt).run();
    }

}
