package net.consensys.data;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

import org.bouncycastle.jcajce.provider.digest.Keccak;
import org.bouncycastle.jcajce.provider.digest.RIPEMD160;
import org.bouncycastle.jcajce.provider.digest.SHA256;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/** The Message digest factory. */
public class MessageDigestFactory {

    private MessageDigestFactory() {}

    /** Keccak-256 */
    public static final String KECCAK256_ALG = "KECCAK-256";
    /** SHA-256 */
    public static final String SHA256_ALG = "SHA-256";
    /** RipeMD-160 */
    public static final String RIPEMD160_ALG = "RIPEMD160";
    /** Blake2b F Function */
    public static final String BLAKE2BF_ALG = "BLAKE2BF";

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * Create message digest.
     *
     * @param algorithm the algorithm
     * @return the message digest
     * @throws NoSuchAlgorithmException the no such algorithm exception
     */
    @SuppressWarnings("DoNotInvokeMessageDigestDirectly")
    public static MessageDigest create(final String algorithm) throws NoSuchAlgorithmException {
        return switch (algorithm) {
            case KECCAK256_ALG -> new Keccak.Digest256();
            case SHA256_ALG -> new SHA256.Digest();
            case RIPEMD160_ALG -> new RIPEMD160.Digest();
            default -> MessageDigest.getInstance(algorithm);
        };
    }
}

