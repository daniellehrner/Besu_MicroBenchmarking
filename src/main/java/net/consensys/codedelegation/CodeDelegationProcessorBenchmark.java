package net.consensys.codedelegation;

/*
 * Copyright contributors to Besu.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import org.hyperledger.besu.crypto.KeyPair;
import org.hyperledger.besu.crypto.SECP256K1;
import org.hyperledger.besu.crypto.SignatureAlgorithm;
import org.hyperledger.besu.crypto.SignatureAlgorithmFactory;
import org.hyperledger.besu.datatypes.Address;
import org.hyperledger.besu.datatypes.TransactionType;
import org.hyperledger.besu.ethereum.core.Transaction;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

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
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.profile.AsyncProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;


@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(value = 1)
@Warmup(iterations = 3)
@Measurement(iterations = 5)
@State(Scope.Benchmark)
public class CodeDelegationProcessorBenchmark {

    private SignatureAlgorithm signatureAlgorithm;
    private static final SECP256K1 secp256k1 = new SECP256K1();
    private Transaction transaction;

    @Param({"10", "100", "1000"})
    private int delegationCount;

    @Setup
    public void setup() {
        signatureAlgorithm = SignatureAlgorithmFactory.getInstance();
        assert (signatureAlgorithm.isNative());
        // Create a transaction with code delegations
        List<org.hyperledger.besu.datatypes.CodeDelegation> codeDelegations =
                createCodeDelegations(delegationCount);
        transaction = createTransactionWithDelegations(codeDelegations);
    }

    private List<org.hyperledger.besu.datatypes.CodeDelegation> createCodeDelegations(
            final int count) {
        List<org.hyperledger.besu.datatypes.CodeDelegation> delegations = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            final KeyPair keyPair =
                    SignatureAlgorithmFactory.getInstance().generateKeyPair();
            org.hyperledger.besu.datatypes.CodeDelegation codeDelegation =
                    org.hyperledger.besu.ethereum.core.CodeDelegation.builder()
                            .chainId(BigInteger.valueOf(20211))
                            .address(Address.fromHexString("0x" + String.format("%040x", i)))
                            .nonce(i)
                            .signAndBuild(keyPair);
            delegations.add(codeDelegation);
        }
        return delegations;
    }

    private Transaction createTransactionWithDelegations(
            final List<org.hyperledger.besu.datatypes.CodeDelegation> delegations) {
        return new TransactionTestFixture().codeDelegations(delegations).type(TransactionType.DELEGATE_CODE).createTransaction(SignatureAlgorithmFactory.getInstance().generateKeyPair());
    }

    @Benchmark
    public void sequentialProcessing(final Blackhole bh) {
        transaction
                .getCodeDelegationList()
                .ifPresent(
                        delegations -> {
                            for (org.hyperledger.besu.datatypes.CodeDelegation codeDelegation : delegations) {
                                final Optional<Address> authorizer = codeDelegation.authorizer();
                                bh.consume(authorizer);
                            }
                        });
    }

    @Benchmark
    public void parallelProcessing(final Blackhole bh) {
        transaction
                .getCodeDelegationList()
                .ifPresent(
                        delegations -> {
                            delegations.parallelStream()
                                    .forEach(
                                            codeDelegation -> {
                                                final Optional<Address> authorizer = codeDelegation.authorizer();
                                                bh.consume(authorizer);
                                            });
                        });
    }

    public static void main(final String[] args) throws RunnerException, IOException {
        Options opt = new OptionsBuilder()
                .include(CodeDelegationProcessorBenchmark.class.getSimpleName())
                .addProfiler(AsyncProfiler.class)
                .build();

        new Runner(opt).run();
    }
}
