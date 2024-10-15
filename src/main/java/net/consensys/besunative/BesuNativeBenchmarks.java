package net.consensys.besunative;

import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.profile.AsyncProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class BesuNativeBenchmarks {

    // TODO SLD: Blake2bf?
    //  Non-Gnark libs?
    //  LibIpaMultipoint - Verkle?
    //  Other secp256k1 calls such as verify?
    //  LibSECP256R1?
    //  Ripe?

    public static void main(String[] args) throws Exception {
        Set<String> argsSet = new HashSet<>(Arrays.asList(args));
        boolean withProfiling = args.length > 0 && argsSet.contains("--profile");

        int iterations = argsSet.stream()
                .filter(a -> a.startsWith("--iterations=") || a.startsWith("-i="))
                .map(a -> a.split("=")[1])
                .findFirst()
                .map(Integer::parseInt)
                .orElse(50);

        var optsBuilder = new OptionsBuilder()
                .include(EcRecoverBenchmarks.class.getSimpleName())
                .include(ModExpBenchmarks.class.getSimpleName())
                .include(AltBN128Benchmarks.class.getSimpleName())
                .include(BLS12Benchmarks.class.getSimpleName())
                .warmupIterations(iterations)
                .warmupTime(TimeValue.seconds(1))
                .measurementIterations(iterations)
                .measurementTime(TimeValue.seconds(1))
                .forks(1)
                .mode(Mode.AverageTime)
                .timeUnit(TimeUnit.NANOSECONDS);

        if (withProfiling) {
            System.out.println("Profiling enabled");
            optsBuilder.addProfiler(AsyncProfiler.class);
        }
        Options opt = optsBuilder.build();

        new Runner(opt).run();
    }
}
