package net.consensys.besunative;

import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.bytes.Bytes32;
import org.hyperledger.besu.datatypes.*;
import org.hyperledger.besu.crypto.KeyPair;
import org.hyperledger.besu.crypto.SECPPrivateKey;
import org.hyperledger.besu.crypto.SECPSignature;
import org.hyperledger.besu.crypto.SignatureAlgorithm;
import org.hyperledger.besu.crypto.SignatureAlgorithmFactory;
import org.hyperledger.besu.evm.code.CodeV0;
import org.hyperledger.besu.evm.fluent.SimpleBlockValues;
import org.hyperledger.besu.evm.fluent.SimpleWorld;
import org.hyperledger.besu.evm.frame.MessageFrame;
import org.hyperledger.besu.evm.precompile.BLS12G1AddPrecompiledContract;
import org.hyperledger.besu.evm.precompile.PrecompiledContract;
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
import org.openjdk.jmh.profile.AsyncProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import static org.hyperledger.besu.crypto.Hash.keccak256;

@Warmup(iterations = 50, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 50, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class BesuNativeBenchmarks {
    SignatureAlgorithm signatureAlgorithm;
    Bytes32 dataHash;
    SECPSignature signature;
    static MessageFrame fakeFrame;


    @Setup(Level.Iteration)
    public void setup() {
        signatureAlgorithm = SignatureAlgorithmFactory.getInstance();

        // Generate a random private key
        final SECPPrivateKey privateKey =
                signatureAlgorithm.createPrivateKey(
                        new BigInteger(256, new java.security.SecureRandom()));
        final KeyPair keyPair = signatureAlgorithm.createKeyPair(privateKey);

        final Bytes data = Bytes.wrap(
                ("This is a dynamically generated message: " + System.nanoTime()).getBytes(StandardCharsets.UTF_8));

        dataHash = keccak256(data);
        signature = signatureAlgorithm.sign(dataHash, keyPair);

        fakeFrame =
                MessageFrame.builder()
                        .type(MessageFrame.Type.CONTRACT_CREATION)
                        .contract(Address.ZERO)
                        .inputData(Bytes.EMPTY)
                        .sender(Address.ZERO)
                        .value(Wei.ZERO)
                        .apparentValue(Wei.ZERO)
                        .code(CodeV0.EMPTY_CODE)
                        .completer(__ -> {})
                        .address(Address.ZERO)
                        .blockHashLookup(n -> null)
                        .blockValues(new SimpleBlockValues())
                        .gasPrice(Wei.ZERO)
                        .miningBeneficiary(Address.ZERO)
                        .originator(Address.ZERO)
                        .initialGas(100_000L)
                        .worldUpdater(new SimpleWorld())
                        .build();
    }


   // @Benchmark
   // public void ecrecover(Blackhole bh) {
   //     bh.consume(signatureAlgorithm.recoverPublicKeyFromSignature(dataHash, signature));
   // }

    @Benchmark
    public void benchBLS12G1Add(final Blackhole bh) {
        final Bytes arg =
                Bytes.fromHexString(
                        "0000000000000000000000000000000012196c5a43d69224d8713389285f26b98f86ee910ab3dd668e413738282003cc5b7357af9a7af54bb713d62255e80f56"
                                + "0000000000000000000000000000000006ba8102bfbeea4416b710c73e8cce3032c31c6269c44906f8ac4f7874ce99fb17559992486528963884ce429a992fee"
                                + "000000000000000000000000000000000001101098f5c39893765766af4512a0c74e1bb89bc7e6fdf14e3e7337d257cc0f94658179d83320b99f31ff94cd2bac"
                                + "0000000000000000000000000000000003e1a9f9f44ca2cdab4f43a1a3ee3470fdf90b2fc228eb3b709fcd72f014838ac82a6d797aeefed9a0804b22ed1ce8f7");

        final BLS12G1AddPrecompiledContract contract = new BLS12G1AddPrecompiledContract();

        PrecompiledContract.PrecompileContractResult precompileContractResult = contract.computePrecompile(arg, fakeFrame);
        bh.consume(precompileContractResult);
    }

    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(BesuNativeBenchmarks.class.getSimpleName())
                .addProfiler(AsyncProfiler.class)
                .build();

        new Runner(opt).run();
    }
}
