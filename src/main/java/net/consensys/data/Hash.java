package net.consensys.data;




import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.function.Supplier;

import com.google.common.base.Suppliers;
import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.bytes.Bytes32;

import static net.consensys.data.MessageDigestFactory.BLAKE2BF_ALG;
import static net.consensys.data.MessageDigestFactory.KECCAK256_ALG;
import static net.consensys.data.MessageDigestFactory.RIPEMD160_ALG;
import static net.consensys.data.MessageDigestFactory.SHA256_ALG;

/** Various utilities for providing hashes (digests) of arbitrary data. */
public abstract class Hash {
    private Hash() {}

    private static final Supplier<MessageDigest> KECCAK256_SUPPLIER =
            Suppliers.memoize(() -> messageDigest(KECCAK256_ALG));
    private static final Supplier<MessageDigest> SHA256_SUPPLIER =
            Suppliers.memoize(() -> messageDigest(SHA256_ALG));
    private static final Supplier<MessageDigest> RIPEMD160_SUPPLIER =
            Suppliers.memoize(() -> messageDigest(RIPEMD160_ALG));
    private static final Supplier<MessageDigest> BLAKE2BF_SUPPLIER =
            Suppliers.memoize(() -> messageDigest(BLAKE2BF_ALG));

    private static MessageDigest messageDigest(final String algorithm) {
        try {
            return MessageDigestFactory.create(algorithm);
        } catch (final NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Helper method to generate a digest using the provided algorithm.
     *
     * @param input The input bytes to produce the digest for.
     * @param digestSupplier the digest supplier to use
     * @return A digest.
     */
    private static byte[] digestUsingAlgorithm(
            final Bytes input, final Supplier<MessageDigest> digestSupplier) {
        try {
            final MessageDigest digest = (MessageDigest) digestSupplier.get().clone();
            input.update(digest);
            return digest.digest();
        } catch (final CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Digest using SHA2-256.
     *
     * @param input The input bytes to produce the digest for.
     * @return A digest.
     */
    public static Bytes32 sha256(final Bytes input) {
        return Bytes32.wrap(digestUsingAlgorithm(input, SHA256_SUPPLIER));
    }

    /**
     * Digest using keccak-256.
     *
     * @param input The input bytes to produce the digest for.
     * @return A digest.
     */
    public static Bytes32 keccak256(final Bytes input) {
        return Bytes32.wrap(digestUsingAlgorithm(input, KECCAK256_SUPPLIER));
    }

    /**
     * Digest using RIPEMD-160.
     *
     * @param input The input bytes to produce the digest for.
     * @return A digest.
     */
    public static Bytes ripemd160(final Bytes input) {
        return Bytes.wrap(digestUsingAlgorithm(input, RIPEMD160_SUPPLIER));
    }

    /**
     * Digest using Blake2f compression function.
     *
     * @param input The input bytes to produce the digest for.
     * @return A digest.
     */
    public static Bytes blake2bf(final Bytes input) {
        return Bytes.wrap(digestUsingAlgorithm(input, BLAKE2BF_SUPPLIER));
    }
}
