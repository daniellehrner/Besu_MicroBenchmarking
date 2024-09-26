package net.consensys.besunative;

import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.bytes.Bytes32;
import org.hyperledger.besu.crypto.KeyPair;
import org.hyperledger.besu.crypto.SECPPrivateKey;
import org.hyperledger.besu.crypto.SECPSignature;
import org.hyperledger.besu.crypto.SignatureAlgorithm;
import org.hyperledger.besu.crypto.SignatureAlgorithmFactory;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.profile.AsyncProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.math.BigInteger;
import java.util.concurrent.TimeUnit;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hyperledger.besu.crypto.Hash.keccak256;

@Warmup(iterations = 100, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 100, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class BesuNativeBenchmarks {
    SignatureAlgorithm signatureAlgorithm;
    Bytes32 dataHash;
    SECPSignature signature;
    @Setup
    public void setup() {
        signatureAlgorithm = SignatureAlgorithmFactory.getInstance();

        final SECPPrivateKey privateKey =
                signatureAlgorithm.createPrivateKey(
                        new BigInteger("c85ef7d79691fe79573b1a7064c19c1a9819ebdbd1faaab1a8ec92344438aaf4", 16));
        final KeyPair keyPair = signatureAlgorithm.createKeyPair(privateKey);

        final Bytes data = Bytes.wrap("This is an example of a signed message.".getBytes(UTF_8));
        dataHash = keccak256(data);
        signature = signatureAlgorithm.sign(dataHash, keyPair);
    }


    @Benchmark
    public void ecrecover(Blackhole bh) {
        bh.consume(signatureAlgorithm.recoverPublicKeyFromSignature(dataHash, signature));
    }

    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(BesuNativeBenchmarks.class.getSimpleName())
                .addProfiler(AsyncProfiler.class)
                .build();

        new Runner(opt).run();
    }
}
