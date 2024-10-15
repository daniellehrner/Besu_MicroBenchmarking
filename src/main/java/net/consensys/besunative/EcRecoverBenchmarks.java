package net.consensys.besunative;

import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.bytes.Bytes32;
import org.hyperledger.besu.crypto.KeyPair;
import org.hyperledger.besu.crypto.SECPPrivateKey;
import org.hyperledger.besu.crypto.SECPSignature;
import org.hyperledger.besu.crypto.SignatureAlgorithm;
import org.hyperledger.besu.crypto.SignatureAlgorithmFactory;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

import static org.hyperledger.besu.crypto.Hash.keccak256;

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
}
