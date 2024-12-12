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
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import static org.hyperledger.besu.crypto.Hash.keccak256;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(value = 1)
@Warmup(iterations = 3)
@Measurement(iterations = 5)
@State(Scope.Benchmark)
public class EcRecoverBenchmarks {

    private SignatureAlgorithm signatureAlgorithm;
    private Bytes32 dataHash;
    private SECPSignature signature;

    @Setup(Level.Iteration)
    public void setup() {
        signatureAlgorithm = SignatureAlgorithmFactory.getInstance();
        assert (signatureAlgorithm.isNative());

        // Generate a random private key
        final SECPPrivateKey privateKey =
                signatureAlgorithm.createPrivateKey(
                        new BigInteger(256, new java.security.SecureRandom()));
        final KeyPair keyPair = signatureAlgorithm.createKeyPair(privateKey);

        final Bytes data = Bytes.wrap(
                ("This is a dynamically generated message: " + System.nanoTime()).getBytes(StandardCharsets.UTF_8));

        dataHash = keccak256(data);
        signature = signatureAlgorithm.sign(dataHash, keyPair);
    }

    @Benchmark
    public void ecrecover(Blackhole bh) {
        bh.consume(signatureAlgorithm.recoverPublicKeyFromSignature(dataHash, signature));
    }

    public static void main(final String[] args) throws RunnerException, IOException {
        Options opt = new OptionsBuilder()
                .include(EcRecoverBenchmarks.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }
}
