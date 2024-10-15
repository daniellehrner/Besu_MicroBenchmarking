package net.consensys.besunative;

import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.profile.AsyncProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.concurrent.TimeUnit;

public class BesuNativeBenchmarks {

    // TODO SLD: Blake2bf?
    //  Non-Gnark libs?
    //  LibIpaMultipoint - Verkle?
    //  Other secp256k1 calls such as verify?
    //  LibSECP256R1?
    //  Ripe?

    public static void main(String[] args) throws Exception {
        boolean withProfiling = args.length > 0 && args[0].equals("--profile");

        var optsBuilder = new OptionsBuilder()
                .include(EcRecoverBenchmarks.class.getSimpleName())
                .include(ModExpBenchmarks.class.getSimpleName())
                .include(AltBN128Benchmarks.class.getSimpleName())
                .include(BLS12Benchmarks.class.getSimpleName())
                .warmupIterations(50)
                .warmupTime(TimeValue.seconds(1))
                .measurementIterations(50)
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
