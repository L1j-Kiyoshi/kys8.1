package server.netty.coder.types;

public class UChar8 {
    /**
     * Converts a 32 bit unsigned/signed long array to a 8 bit unsigned char array.
     *
     * @param buff the array to convert
     * @return char[] an 8 bit unsigned char array
     */
    public char[] fromArray(long[] buff) {
        char[] charBuff = new char[buff.length * 4];

        for (int i = 0; i < buff.length; ++i) {
            charBuff[(i * 4) + 0] = (char) (buff[i] & 0xFF);
            charBuff[(i * 4) + 1] = (char) ((buff[i] >> 8) & 0xFF);
            charBuff[(i * 4) + 2] = (char) ((buff[i] >> 16) & 0xFF);
            charBuff[(i * 4) + 3] = (char) ((buff[i] >> 24) & 0xFF);
        }

        return charBuff;
    }

    public char[] fromArray(long[] buff, char[] charBuff) {
        for (int i = 0; i < buff.length; ++i) {
            charBuff[(i * 4) + 0] = (char) (buff[i] & 0xFF);
            charBuff[(i * 4) + 1] = (char) ((buff[i] >> 8) & 0xFF);
            charBuff[(i * 4) + 2] = (char) ((buff[i] >> 16) & 0xFF);
            charBuff[(i * 4) + 3] = (char) ((buff[i] >> 24) & 0xFF);
        }

        return charBuff;
    }

    /**
     * Converts an 8 bit unsigned byte array to an 8 bit unsigned char array.
     *
     * @param buff the array to convert
     * @return char[] an 8 bit unsigned char array
     */
    public char[] fromArray(byte[] buff) {
        char[] charBuff = new char[buff.length];

        for (int i = 0; i < buff.length; i++) {
            charBuff[i] = (char) (buff[i] & 0xFF);
        }

        return charBuff;
    }

    public char[] fromArray(byte[] buff, char[] data, int size) {
        for (int i = 0; i < size; i++) {
            data[i] = (char) (buff[i] & 0xFF);
        }

        return data;
    }

    /**
     * Converts an 8 bit unsigned byte to an 8 bit unsigned char.
     *
     * @param b the byte value to convert
     * @return char an 8 bit unsigned char
     */
    public char fromUByte8(byte b) {
        return (char) (b & 0xFF);
    }

    /**
     * Converts a 32 bit unsigned long to an 8 bit unsigned char.
     *
     * @param l the long value to convert
     * @return char an 8 bit unsigned char
     */
    public char[] fromULong32(long l) {
        char[] charBuff = new char[4];

        charBuff[0] = (char) (l & 0xFF);
        charBuff[1] = (char) ((l >> 8) & 0xFF);
        charBuff[2] = (char) ((l >> 16) & 0xFF);
        charBuff[3] = (char) ((l >> 24) & 0xFF);

        return charBuff;
    }
}
