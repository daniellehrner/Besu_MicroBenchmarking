package net.consensys.besunative;

import org.apache.tuweni.bytes.Bytes;
import org.hyperledger.besu.datatypes.Address;
import org.hyperledger.besu.datatypes.Wei;
import org.hyperledger.besu.evm.code.CodeV0;
import org.hyperledger.besu.evm.fluent.SimpleBlockValues;
import org.hyperledger.besu.evm.fluent.SimpleWorld;
import org.hyperledger.besu.evm.frame.MessageFrame;
import org.hyperledger.besu.evm.gascalculator.IstanbulGasCalculator;
import org.hyperledger.besu.evm.precompile.AltBN128AddPrecompiledContract;
import org.hyperledger.besu.evm.precompile.AltBN128MulPrecompiledContract;
import org.hyperledger.besu.evm.precompile.AltBN128PairingPrecompiledContract;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Benchmark)
public class AltBN128Benchmarks {

    private MessageFrame fakeFrame;
    private AltBN128AddPrecompiledContract addPrecompile;
    private AltBN128MulPrecompiledContract mulPrecompile;
    private AltBN128PairingPrecompiledContract pairingPrecompile;
    private Bytes addArg;
    private Bytes mulArg;
    private Bytes pairingArg1;
    private Bytes pairingArg2;
    private Bytes pairingArg3;

    @Setup(Level.Iteration)
    public void setup() {

        fakeFrame =
                MessageFrame.builder()
                        .type(MessageFrame.Type.CONTRACT_CREATION)
                        .contract(Address.ZERO)
                        .inputData(Bytes.EMPTY)
                        .sender(Address.ZERO)
                        .value(Wei.ZERO)
                        .apparentValue(Wei.ZERO)
                        .code(CodeV0.EMPTY_CODE)
                        .completer(__ -> {
                        })
                        .address(Address.ZERO)
                        .blockHashLookup(n -> null)
                        .blockValues(new SimpleBlockValues())
                        .gasPrice(Wei.ZERO)
                        .miningBeneficiary(Address.ZERO)
                        .originator(Address.ZERO)
                        .initialGas(100_000L)
                        .worldUpdater(new SimpleWorld())
                        .build();

        final IstanbulGasCalculator gasCalculator = new IstanbulGasCalculator();
        addPrecompile = AltBN128AddPrecompiledContract.istanbul(gasCalculator);

        Bytes g1AddPoint0 = Bytes.concatenate(
                Bytes.fromHexString("0x17c139df0efee0f766bc0204762b774362e4ded88953a39ce849a8a7fa163fa9"),
                Bytes.fromHexString("0x01e0559bacb160664764a357af8a9fe70baa9258e0b959273ffc5718c6d4cc7c"));
        Bytes g1AddPoint1 = Bytes.concatenate(
                Bytes.fromHexString("0x17c139df0efee0f766bc0204762b774362e4ded88953a39ce849a8a7fa163fa9"),
                Bytes.fromHexString("0x2e83f8d734803fc370eba25ed1f6b8768bd6d83887b87165fc2434fe11a830cb"));
        addArg = Bytes.concatenate(g1AddPoint0, g1AddPoint1);

        mulPrecompile = AltBN128MulPrecompiledContract.istanbul(gasCalculator);

        Bytes g1MulPoint = Bytes.concatenate(
                Bytes.fromHexString("0x0000000000000000000000000000000000000000000000000000000000000001"),
                Bytes.fromHexString("0x30644e72e131a029b85045b68181585d97816a916871ca8d3c208c16d87cfd45"));
        Bytes mulScalar = Bytes.fromHexString("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff");
        mulArg = Bytes.concatenate(g1MulPoint, mulScalar);

        pairingPrecompile = AltBN128PairingPrecompiledContract.istanbul(gasCalculator);
        pairingArg1 = Bytes.fromHexString(
                "0x0fc6ebd1758207e311a99674dc77d28128643c057fb9ca2c92b4205b6bf57ed2"
                        + "1e50042f97b7a1f2768fa15f6683eca9ee7fa8ee655d94246ab85fb1da3f0b90"
                        + "198e9393920d483a7260bfb731fb5d25f1aa493335a9e71297e485b7aef312c2"
                        + "1800deef121f1e76426a00665e5c4479674322d4f75edadd46debd5cd992f6ed"
                        + "090689d0585ff075ec9e99ad690c3395bc4b313370b38ef355acdadcd122975b"
                        + "12c85ea5db8c6deb4aab71808dcb408fe3d1e7690c43d37b4ce6cc0166fa7daa");
        pairingArg2 = Bytes.fromHexString(
                "0x2b101be01b2f064cba109e065dc0b5e5bf6b64ed4054b82af3a7e6e34c1e2005"
                        + "1a4d9ceecf9115a98efd147c4abb2684102d3e925938989153b9ff330523cdb4"
                        + "08d554bf59102bbb961ba81107ec71785ef9ce6638e5332b6c1a58b87447d181"
                        + "01cf7cc93bfbf7b2c5f04a3bc9cb8b72bbcf2defcabdceb09860c493bdf1588d"
                        + "02cb2a424885c9e412b94c40905b359e3043275cd29f5b557f008cd0a3e0c0dc"
                        + "204e5d81d86c561f9344ad5f122a625f259996b065b80cbbe74a9ad97b6d7cc2"
                        + "07402fdc3bc28a434909f24695adea3e9418d9857efc8c71f67a470a17f3cf12"
                        + "255dbc3a8b5c2c1a7a3f8c59e2f5b6e04bc4d7b7bb82fcbe18b2294305c8473b"
                        + "19156e854972d656d1020003e5781972d84081309cdf71baacf6c6e29272f5ff"
                        + "2acded377df8902b7a75de6c0f53c161f3a2ff3f374470b78d5b3c4d826d84d5"
                        + "1731ef3b84913296c30a649461b2ca35e3fcc2e3031ea2386d32f885ff096559"
                        + "0919e7685f6ea605db14f311dede6e83f21937f05cfc53ac1dbe45891c47bf2a");
        pairingArg3 = Bytes.fromHexString(
                "0x1a3fabea802788c8aa88741c6a68f271b221eb75838bb1079381f3f1ae414f40"
                        + "126308d6cdb6b7efceb1ec0016b99cf7a1e5780f5a9a775d43bc7f2b6fd510e2"
                        + "11b35cf2c85531eab64b96eb2eef487e0eb60fb9207fe4763e7f6e02dcead646"
                        + "2cbea52f3417b398aed9e355ed16934a81b72d2646e3bf90dbc2dcba294b631d"
                        + "2c6518cd26310e541a799357d1ae8bc477b162f2040407b965ecd777e26d31f7"
                        + "125170b5860fb8f8da2c43e00ea4a83bcc1a974e47e59fcd657851d2b0dd1655"
                        + "130a2183533392b5fd031857eb4c199a19382f39fcb666d6133b3a6e5784d6a5"
                        + "2cca76f2bc625d2e61a41b5f382eadf1df1756dd392f639c3d9f3513099e63f9"
                        + "07ecba8131b3fb354272c86d01577e228c5bd5fb6404bbaf106d7f4858dc2996"
                        + "1c5d49a9ae291a2a2213da57a76653391fa1fc0fa7c534afa124ad71b7fdd719"
                        + "10f1a73f94a8f077f478d069d7cf1c49444f64cd20ed75d4f6de3d8986147cf8"
                        + "0d5816f2f116c5cc0be7dfc4c0b4c592204864acb70ad5f789013389a0092ce4"
                        + "2650b89e5540eea1375b27dfd9081a0622e03352e5c6a7593df72e2113328e64"
                        + "21991b3e5100845cd9b8f0fa16c7fe5f40152e702e61f4cdf0d98e7f213b1a47"
                        + "10520008be7609bdb92145596ac6bf37da0269f7460e04e8e4701c3afbae0e52"
                        + "0664e736b2af7bf9125f69fe5c3706cd893cd769b1dae8a6e3d639e2d76e66e2"
                        + "1cacce8776f5ada6b35036f9343faab26c91b9aea83d3cb59cf5628ffe18ab1b"
                        + "03b48ca7e6d84fca619aaf81745fbf9c30e5a78ed4766cc62b0f12aea5044f56");
    }

    @Benchmark
    public void benchBNADD(Blackhole bh) {
        bh.consume(addPrecompile.computePrecompile(addArg, fakeFrame));
    }

    @Benchmark
    public void benchBNMUL(Blackhole bh) {
        bh.consume(mulPrecompile.computePrecompile(mulArg, fakeFrame));
    }

    @Benchmark
    public void benchBNPairing1(Blackhole bh) {
        bh.consume(pairingPrecompile.computePrecompile(pairingArg1, fakeFrame));
    }

    @Benchmark
    public void benchBNPairing2(Blackhole bh) {
        bh.consume(pairingPrecompile.computePrecompile(pairingArg2, fakeFrame));
    }

    @Benchmark
    public void benchBNPairing3(Blackhole bh) {
        bh.consume(pairingPrecompile.computePrecompile(pairingArg3, fakeFrame));
    }
}