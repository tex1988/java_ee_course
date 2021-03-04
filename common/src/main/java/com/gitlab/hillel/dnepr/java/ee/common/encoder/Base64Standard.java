package com.gitlab.hillel.dnepr.java.ee.common.encoder;

import java.util.Base64;

public class Base64Standard extends BaseEncoder {
    public static final Encoder ENCODER = new Base64StandardEncoder();
    public static final Decoder DECODER = new Base64StandardDecoder();

    public Base64Standard() {
        this(null);
    }

    public Base64Standard(BaseEncoder baseEncoder) {
        super(baseEncoder);
    }

    @Override
    public Encoder getEncoder() {
        return ENCODER;
    }

    @Override
    public Decoder getDecoder() {
        return DECODER;
    }

    public static class Base64StandardEncoder implements Encoder {
        private final static Base64.Encoder ENCODER = Base64.getEncoder();

        @Override
        public byte[] encode(byte[] byteArr) {
            return ENCODER.encode(byteArr);
        }
    }

    public static class Base64StandardDecoder implements Decoder {
        private final static Base64.Decoder DECODER = Base64.getDecoder();

        @Override
        public byte[] decode(byte[] byteArr) {
            return DECODER.decode(byteArr);
        }
    }
}

