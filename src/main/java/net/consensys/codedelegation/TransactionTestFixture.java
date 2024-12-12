package net.consensys.codedelegation;


import org.hyperledger.besu.crypto.KeyPair;
import org.hyperledger.besu.crypto.SECPSignature;
import org.hyperledger.besu.datatypes.AccessListEntry;
import org.hyperledger.besu.datatypes.Address;
import org.hyperledger.besu.datatypes.BlobsWithCommitments;
import org.hyperledger.besu.datatypes.TransactionType;
import org.hyperledger.besu.datatypes.VersionedHash;
import org.hyperledger.besu.datatypes.Wei;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import org.apache.tuweni.bytes.Bytes;
import org.hyperledger.besu.ethereum.core.CodeDelegation;
import org.hyperledger.besu.ethereum.core.Transaction;

public class TransactionTestFixture {
    BigInteger curveOrder = new BigInteger(
            "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141", 16);

    // Option 2: Use SECP256K1 class (if available)
    // BigInteger curveOrder = SECP256K1.CURVE_ORDER;

    // Sample valid r and s values from a real signature
    BigInteger r = new BigInteger(
            "37206a061099168556b1f3b58c587de1b2d1f05e03c3fa46ac65a6a2e63684ed", 16);
    BigInteger s = new BigInteger(
            "8ca63759c11531d5b0b3b0420d4535b8e13a6d4a9fa0d95a7c52f4706ef53d69", 16);
    byte recId = 0; // Or 1
    private final SECPSignature signature = SECPSignature.create(r, s, recId, curveOrder);

    private TransactionType transactionType = TransactionType.FRONTIER;

    private long nonce = 0;

    private Optional<Wei> gasPrice = Optional.empty();

    private long gasLimit = 5000;

    private Optional<Address> to = Optional.empty();
    private Address sender = Address.fromHexString(String.format("%020x", 1));

    private Wei value = Wei.of(4);

    private Bytes payload = Bytes.EMPTY;

    private Optional<BigInteger> chainId = Optional.of(BigInteger.valueOf(1337));

    private Optional<Wei> maxPriorityFeePerGas = Optional.empty();
    private Optional<Wei> maxFeePerGas = Optional.empty();
    private Optional<Wei> maxFeePerBlobGas = Optional.empty();

    private Optional<List<AccessListEntry>> accessListEntries = Optional.empty();
    private Optional<List<VersionedHash>> versionedHashes = Optional.empty();

    private Optional<BlobsWithCommitments> blobs = Optional.empty();
    private Optional<BigInteger> v = Optional.empty();
    private Optional<List<org.hyperledger.besu.datatypes.CodeDelegation>> codeDelegations =
            Optional.empty();

    public Transaction createTransaction(final KeyPair keys) {
        final Transaction.Builder builder = Transaction.builder();
        builder
                .type(transactionType)
                .gasLimit(gasLimit)
                .nonce(nonce)
                .payload(payload)
                .value(value)
                .sender(sender);

        switch (transactionType) {
            case FRONTIER:
                builder.gasPrice(gasPrice.orElse(Wei.of(5000)));
                break;
            case ACCESS_LIST:
                builder.gasPrice(gasPrice.orElse(Wei.of(5000)));
                builder.accessList(accessListEntries.orElse(List.of()));
                break;
            case EIP1559:
                builder.maxPriorityFeePerGas(maxPriorityFeePerGas.orElse(Wei.of(500)));
                builder.maxFeePerGas(maxFeePerGas.orElse(Wei.of(5000)));
                builder.accessList(accessListEntries.orElse(List.of()));
                break;
            case BLOB:
                builder.maxPriorityFeePerGas(maxPriorityFeePerGas.orElse(Wei.of(500)));
                builder.maxFeePerGas(maxFeePerGas.orElse(Wei.of(5000)));
                builder.accessList(accessListEntries.orElse(List.of()));
                builder.maxFeePerBlobGas(maxFeePerBlobGas.orElse(Wei.ONE));
                if (blobs.isPresent()) {
                    builder.kzgBlobs(
                            blobs.get().getKzgCommitments(), blobs.get().getBlobs(), blobs.get().getKzgProofs());
                } else if (versionedHashes.isPresent()) {
                    builder.versionedHashes(versionedHashes.get());
                }
                break;
            case DELEGATE_CODE:
                builder.maxPriorityFeePerGas(maxPriorityFeePerGas.orElse(Wei.of(500)));
                builder.maxFeePerGas(maxFeePerGas.orElse(Wei.of(5000)));
                builder.accessList(accessListEntries.orElse(List.of()));
                builder.codeDelegations(
                        codeDelegations.orElse(
                                List.of(new CodeDelegation(chainId.get(), sender, 0, signature))));
                break;
        }

        to.ifPresent(builder::to);
        chainId.ifPresent(builder::chainId);
        v.ifPresent(builder::v);

        return builder.signAndBuild(keys);
    }

    public TransactionTestFixture type(final TransactionType transactionType) {
        this.transactionType = transactionType;
        return this;
    }

    public TransactionTestFixture nonce(final long nonce) {
        this.nonce = nonce;
        return this;
    }

    public TransactionTestFixture gasPrice(final Wei gasPrice) {
        this.gasPrice = Optional.ofNullable(gasPrice);
        return this;
    }

    public TransactionTestFixture gasLimit(final long gasLimit) {
        this.gasLimit = gasLimit;
        return this;
    }

    public TransactionTestFixture to(final Optional<Address> to) {
        this.to = to;
        return this;
    }

    public TransactionTestFixture sender(final Address sender) {
        this.sender = sender;
        return this;
    }

    public TransactionTestFixture value(final Wei value) {
        this.value = value;
        return this;
    }

    public TransactionTestFixture payload(final Bytes payload) {
        this.payload = payload;
        return this;
    }

    public TransactionTestFixture chainId(final Optional<BigInteger> chainId) {
        this.chainId = chainId;
        return this;
    }

    public TransactionTestFixture maxPriorityFeePerGas(final Optional<Wei> maxPriorityFeePerGas) {
        this.maxPriorityFeePerGas = maxPriorityFeePerGas;
        return this;
    }

    public TransactionTestFixture maxFeePerGas(final Optional<Wei> maxFeePerGas) {
        this.maxFeePerGas = maxFeePerGas;
        return this;
    }

    public TransactionTestFixture maxFeePerBlobGas(final Optional<Wei> maxFeePerBlobGas) {
        this.maxFeePerBlobGas = maxFeePerBlobGas;
        return this;
    }

    public TransactionTestFixture accessList(final List<AccessListEntry> accessListEntries) {
        this.accessListEntries = Optional.ofNullable(accessListEntries);
        return this;
    }

    public TransactionTestFixture versionedHashes(
            final Optional<List<VersionedHash>> versionedHashes) {
        this.versionedHashes = versionedHashes;
        return this;
    }

    public TransactionTestFixture v(final Optional<BigInteger> v) {
        this.v = v;
        return this;
    }

    public TransactionTestFixture blobsWithCommitments(final Optional<BlobsWithCommitments> blobs) {
        this.blobs = blobs;
        return this;
    }

    public TransactionTestFixture codeDelegations(
            final List<org.hyperledger.besu.datatypes.CodeDelegation> codeDelegations) {
        this.codeDelegations = Optional.of(codeDelegations);
        return this;
    }
}

