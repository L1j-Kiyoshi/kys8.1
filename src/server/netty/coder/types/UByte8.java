package server.netty.coder.types;

public class UByte8 {
    /**
     * Converts a 32 bit unsigned/signed long array to a 8 bit unsigned byte array.
     *
     * @param buff the array to convert
     * @return byte[] an 8 bit unsigned byte array
     */
    public byte[] fromArray(long[] buff) {
        byte[] byteBuff = new byte[buff.length * 4];

        for (int i = 0; i < buff.length; ++i) {
            byteBuff[(i * 4) + 0] = (byte) (buff[i] & 0xFF);
            byteBuff[(i * 4) + 1] = (byte) ((buff[i] >> 8) & 0xFF);
            byteBuff[(i * 4) + 2] = (byte) ((buff[i] >> 16) & 0xFF);
            byteBuff[(i * 4) + 3] = (byte) ((buff[i] >> 24) & 0xFF);
        }

        return byteBuff;
    }

    /**
     * Converts an 8 bit unsigned char array to an 8 bit unsigned byte array.
     *
     * @param buff the array to convert
     * @return byte[] an 8 bit unsigned byte array
     */
    public byte[] fromArray(char[] buff) {
        byte[] byteBuff = new byte[buff.length];

        for (int i = 0; i < buff.length; ++i) {
            byteBuff[i] = (byte) (buff[i] & 0xFF);
        }

        return byteBuff;
    }

    public byte[] fromArray(char[] buff, byte[] data) {
        for (int i = 0; i < buff.length; ++i) {
            data[i] = (byte) (buff[i] & 0xFF);
        }

        return data;
    }

    /**
     * Converts an 8 bit unsigned char to an 8 bit unsigned byte.
     *
     * @param c the char value to convert
     * @return byte an 8 bit unsigned byte
     */
    public byte fromUChar8(char c) {
        return (byte) (c & 0xFF);
    }

    /**
     * Converts a 32 bit unsigned long to an 8 bit unsigned byte.
     *
     * @param l the long value to convert
     * @return byte an 8 bit unsigned char
     */
    public byte[] fromULong32(long l) {
        byte[] byteBuff = new byte[4];

        byteBuff[0] = (byte) (l & 0xFF);
        byteBuff[1] = (byte) ((l >> 8) & 0xFF);
        byteBuff[2] = (byte) ((l >> 16) & 0xFF);
        byteBuff[3] = (byte) ((l >> 24) & 0xFF);

        return byteBuff;
    }
}
