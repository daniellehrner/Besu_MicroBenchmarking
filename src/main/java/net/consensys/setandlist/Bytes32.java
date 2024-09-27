package net.consensys.setandlist;

import java.util.Arrays;
import java.util.Random;

public class Bytes32 {
    private final byte[] data;

    public Bytes32(byte[] data) {
        if (data.length != 32) {
            throw new IllegalArgumentException("Bytes32 must be exactly 32 bytes.");
        }
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bytes32 bytes32 = (Bytes32) o;
        return Arrays.equals(data, bytes32.data);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(data);
    }

    public static Bytes32 randomBytes32() {
        Random random = new Random();
        byte[] data = new byte[32];
        random.nextBytes(data);
        return new Bytes32(data);
    }
}
