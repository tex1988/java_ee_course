package com.gitlab.hillel.dnepr.java.ee.common.encoder;

public abstract class BaseEncoder implements Encoder, Decoder {
    private final BaseEncoder baseEncoderDecorator;

    public BaseEncoder() {
        this(null);
    }

    public BaseEncoder(BaseEncoder baseEncoderDecorator) {
        this.baseEncoderDecorator = baseEncoderDecorator;
    }

    public abstract Encoder getEncoder();

    public abstract Decoder getDecoder();

    @Override
    public byte[] decode(byte[] byteArr) {
        final Decoder decoder = getDecoder();
        if (baseEncoderDecorator == null) {
            return decoder.decode(byteArr);
        } else {
            return baseEncoderDecorator.decode(decoder.decode(byteArr));
        }
    }

    @Override
    public byte[] encode(byte[] byteArr) {
        final Encoder encoder = getEncoder();
        if (baseEncoderDecorator == null) {
            return encoder.encode(byteArr);
        } else {
            return baseEncoderDecorator.encode(encoder.encode(byteArr));
        }
    }
}
