package server.netty.coder;

import server.netty.coder.types.UByte8;
import server.netty.coder.types.UChar8;
import server.netty.coder.types.ULong32;

public class LineageEncryption {
    private LineageBlowfish _LineageBlowfish;
    private UByte8 ub8;
    private UChar8 uc8;
    private ULong32 ul32;

    private char[] Dac1;
    private char[] Eac1;

    private long[] encodeKey = { 0, 0 };
    private long[] decodeKey = { 0, 0 };

    private byte[] server_HashKey = new byte[256];
    private byte[] client_HashKey = new byte[256];

    public boolean le;

    public LineageEncryption() {
        ub8 = new UByte8();
        uc8 = new UChar8();
        ul32 = new ULong32();
        Dac1 = new char[400];
        Eac1 = new char[5000];
        _LineageBlowfish = new LineageBlowfish();
    }

    public UByte8 getUByte8() {
        return ub8;
    }

    public UChar8 getUChar8() {
        return uc8;
    }

    public ULong32 getULong32() {
        return ul32;
    }

    public void initKeys(long l) {
        long[] al = { l, 0x930FD7E2L };
        this._LineageBlowfish.getSeeds(al);

        this.encodeKey[0] = (this.decodeKey[0] = al[0]);
        this.encodeKey[1] = (this.decodeKey[1] = al[1]);

        byte[] hashkey = new byte[256];
        char[] tk = getUChar8().fromArray(al);

        init_enc_hashkey(hashkey, tk, 8);

        System.arraycopy(hashkey, 0, this.client_HashKey, 0, 256);
        System.arraycopy(hashkey, 0, this.server_HashKey, 0, 256);

        le = true;
    }

    public static void init_enc_hashkey(byte[] hashkey, char[] currentkey, int const_num) {
        int k = 0;
        for (int i = 0; i < 256; ++i) {
            hashkey[i] = (byte) i;
        }
        for (int j = 0; j < 256; ++j) {
            k = hashkey[j] + k + currentkey[(j % 8)] & 0xFF;
            byte tmp = hashkey[k];
            hashkey[k] = hashkey[j];
            hashkey[j] = tmp;
        }
    }

    public byte[] encrypt_S(byte[] buf) {
        int k = 0;
        int j = 0;
        for (int i = 0; i < buf.length; ++i) {
            j = i + 1 & 0xFF;

            k += this.server_HashKey[(i + 1)];
            k &= 255;
            byte tk = this.server_HashKey[j];
            this.server_HashKey[j] = this.server_HashKey[k];
            this.server_HashKey[k] = tk;

            int b3 = this.server_HashKey[j];
            int b4 = this.server_HashKey[k];
            b3 += b4;
            b3 &= 255;

            byte b5 = this.server_HashKey[b3];
            byte b6 = buf[i];
            b6 = (byte) (b6 ^ b5);
            buf[i] = b6;
        }
        return buf;
    }

    private char[] _encrypt(char[] ac) {
        this.Eac1 = this.uc8.fromArray(this.encodeKey, this.Eac1);
        int tmp21_20 = 0;
        char[] tmp21_19 = ac;
        tmp21_19[tmp21_20] = (char) (tmp21_19[tmp21_20] ^ this.Eac1[0]);
        for (int j = 1; j < ac.length; ++j) {
            int tmp42_41 = j;
            char[] tmp42_40 = ac;
            tmp42_40[tmp42_41] = (char) (tmp42_40[tmp42_41] ^ ac[(j - 1)] ^ this.Eac1[(j & 0x7)]);
        }
        ac[3] = (char) (ac[3] ^ this.Eac1[2]);
        ac[2] = (char) (ac[2] ^ ac[3] ^ this.Eac1[3]);
        ac[1] = (char) (ac[1] ^ ac[2] ^ this.Eac1[4]);
        ac[0] = (char) (ac[0] ^ ac[1] ^ this.Eac1[5]);
        return ac;
    }

    public char[] encrypt_S(char[] buf) {
        int k = 0;
        int j = 0;
        int m = 0;
        int o = 0;
        try {
            for (int i = 0; i < buf.length; ++i) {
                m = i + 1;
                o = m / 256;
                m -= o * 256;

                j = m & 0xFF;
                k += this.client_HashKey[m];
                k &= 255;

                byte tk = this.client_HashKey[j];
                this.client_HashKey[j] = this.client_HashKey[k];
                this.client_HashKey[k] = tk;

                int b3 = this.client_HashKey[j];
                int b4 = this.client_HashKey[k];

                b3 += b4;
                b3 &= 255;

                byte b5 = this.client_HashKey[b3];
                byte b6 = (byte) buf[i];
                b6 = (byte) (b6 ^ b5);
                buf[i] = (char) b6;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buf;
    }

    public char[] encrypt(char ac[]) {
        long l = ul32.fromArray(ac);
        _encrypt(ac);
        encodeKey[0] ^= l;
        encodeKey[1] = ul32.add(encodeKey[1], 679411651L);
        return ac;
    }

    public char[] decrypt(char ac[], int size) {
        _decrypt(ac, size);
        char[] buf1 = new char[size - 4];
        System.arraycopy(ac, 4, buf1, 0, ac.length - 4);
        long l = this.ul32.fromArray(buf1);
        this.decodeKey[0] ^= l;
        this.decodeKey[1] = this.ul32.add(this.decodeKey[1], 679411651L);
        return buf1;
    }

    public char[] decrypt_S(char[] buf) {
        int k = 0;
        int j = 0;

        for (int i = 0; i < buf.length; ++i) {
            j = i + 1 & 0xFF;

            k += this.client_HashKey[j];
            k &= 255;

            int tk = this.client_HashKey[j];
            this.client_HashKey[j] = this.client_HashKey[k];
            this.client_HashKey[k] = (byte) (tk & 0xFF);
            int b3 = this.client_HashKey[j];
            int b4 = this.client_HashKey[k];
            b3 += b4;
            b3 &= 255;

            byte b5 = this.client_HashKey[b3];
            byte b6 = (byte) buf[i];
            b6 = (byte) (b6 ^ b5);
            buf[i] = (char) b6;
        }
        return buf;
    }

    private char[] _decrypt(char ac[], int size) {
        this.Dac1 = this.uc8.fromArray(this.decodeKey, this.Dac1);
        char c = ac[3];
        int tmp25_24 = 3;
        char[] tmp25_23 = ac;
        tmp25_23[tmp25_24] = (char) (tmp25_23[tmp25_24] ^ this.Dac1[2]);
        char c1 = ac[2];
        int tmp43_42 = 2;
        char[] tmp43_41 = ac;
        tmp43_41[tmp43_42] = (char) (tmp43_41[tmp43_42] ^ c ^ this.Dac1[3]);
        char c2 = ac[1];
        int tmp63_62 = 1;
        char[] tmp63_61 = ac;
        tmp63_61[tmp63_62] = (char) (tmp63_61[tmp63_62] ^ c1 ^ this.Dac1[4]);
        char c3 = (char) (ac[0] ^ c2 ^ this.Dac1[5]);
        ac[0] = (char) (c3 ^ this.Dac1[0]);
        for (int j = 1; j < size; ++j) {
            char c4 = ac[j];
            int tmp124_122 = j;
            char[] tmp124_121 = ac;
            tmp124_121[tmp124_122] = (char) (tmp124_121[tmp124_122] ^ this.Dac1[(j & 0x7)] ^ c3);
            c3 = c4;
        }
        return ac;
    }
}