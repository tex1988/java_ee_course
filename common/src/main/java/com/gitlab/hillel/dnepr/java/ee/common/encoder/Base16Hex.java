package com.gitlab.hillel.dnepr.java.ee.common.encoder;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * http://www.java2s.com/Code/Java/Data-Type/Hexencoderanddecoder.htm
 */
public class Base16Hex extends BaseEncoder {
    public static final Encoder ENCODER = new Base16Encoder();
    public static final Decoder DECODER = new Base16Decoder();
    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private static final char[] DIGITS = {
            '0', '1', '2', '3',
            '4', '5', '6', '7',
            '8', '9', 'a', 'b',
            'c', 'd', 'e', 'f'
    };

    public Base16Hex() {
        this(null);
    }

    public Base16Hex(BaseEncoder encoderDecoder) {
        super(encoderDecoder);
    }

    @Override
    public Encoder getEncoder() {
        return ENCODER;
    }

    @Override
    public Decoder getDecoder() {
        return DECODER;
    }

    public static class Base16Encoder implements Encoder {
        public String encode(String data) {
            return new String(encode(data.getBytes(CHARSET)), CHARSET);
        }

        @Override
        public byte[] encode(byte[] data) {
            final int length = data.length;
            final char[] charArr = new char[length << 1];
            // two characters form the hex value.
            for (int i = 0, j = 0; i < length; i++) {
                charArr[j++] = DIGITS[(0xF0 & data[i]) >>> 4];
                charArr[j++] = DIGITS[0x0F & data[i]];
            }
            return new String(charArr).getBytes(StandardCharsets.UTF_8);
        }
    }

    public static class Base16Decoder implements Decoder {
        private static int toDigit(char ch, int index) {
            final int digit = Character.digit(ch, 16);
            if (digit == -1) {
                throw new IllegalArgumentException("Illegal hexadecimal character " + ch + " at index " + index);
            }
            return digit;
        }

        public String decode(String data) {
            return new String(decode(data.getBytes(CHARSET)));
        }

        @Override
        public byte[] decode(byte[] data) {
            final char[] charArr = new String(data, CHARSET).toCharArray();
            final int length = data.length;
            if ((length & 0x01) != 0) {
                throw new IllegalArgumentException("Odd number of characters.");
            }
            final byte[] byteArr = new byte[length >> 1];
            // two characters form the hex value.
            for (int i = 0, j = 0; j < length; i++) {
                int f = toDigit(charArr[j], j) << 4;
                j++;
                f = f | toDigit(charArr[j], j);
                j++;
                byteArr[i] = (byte) (f & 0xFF);
            }
            return byteArr;
        }
    }
}