package net.consensys.keccak;

import net.consensys.keccak.cryptohash.Keccak256;
import net.consensys.keccak.bouncycastle.Hash;
import org.apache.tuweni.bytes.Bytes32;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(5)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class KeccakBenchmark {

    @Param({"32"}) int SIZE;
    Bytes32 bytes;
    @Setup

    public void setup() {
        byte[] src = new byte[SIZE];
        final Random r = new Random(7L);
        r.nextBytes(src);
        bytes = Bytes32.wrap(src);
    }

    @Benchmark
    public Bytes32 keccakBCVersionjdk15on () {
        return Hash.keccak256(bytes);
    }

    @Benchmark
    public Bytes32 keccakCryptoHash() {
        return Bytes32.wrap(sha3(bytes.toArray()));
    }

    public static byte[] sha3(byte[] input) {
        Keccak256 digest =  new Keccak256();
        digest.update(input);
        return digest.digest();
    }

    public static void main(String[] args) throws RunnerException, IOException {
        Options opt = new OptionsBuilder()
                .include(KeccakBenchmark.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }
}
