package com.gitlab.hillel.dnepr.java.ee.common.encoder;

import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Base2Morse extends BaseEncoder {
    public static final Encoder ENCODER = new Base2MorseEncoder();
    public static final Decoder DECODER = new Base2MorseDecoder();
    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private static final List<Character> SYMBOL_LIST = new ArrayList<>();
    private static final List<String> CODE_LIST = new ArrayList<>();

    static {
        SYMBOL_LIST.add('a');
        CODE_LIST.add(".-");

        SYMBOL_LIST.add('b');
        CODE_LIST.add("-...");

        SYMBOL_LIST.add('c');
        CODE_LIST.add("-.-.");

        SYMBOL_LIST.add('d');
        CODE_LIST.add("-..");

        SYMBOL_LIST.add('e');
        CODE_LIST.add(".");

        SYMBOL_LIST.add('f');
        CODE_LIST.add("..-.");

        SYMBOL_LIST.add('g');
        CODE_LIST.add("--.");

        SYMBOL_LIST.add('h');
        CODE_LIST.add("....");

        SYMBOL_LIST.add('i');
        CODE_LIST.add("..");

        SYMBOL_LIST.add('j');
        CODE_LIST.add(".---");

        SYMBOL_LIST.add('k');
        CODE_LIST.add("-.-");

        SYMBOL_LIST.add('l');
        CODE_LIST.add(".-..");

        SYMBOL_LIST.add('m');
        CODE_LIST.add("--");

        SYMBOL_LIST.add('n');
        CODE_LIST.add("-.");

        SYMBOL_LIST.add('o');
        CODE_LIST.add("---");

        SYMBOL_LIST.add('p');
        CODE_LIST.add(".---.");

        SYMBOL_LIST.add('q');
        CODE_LIST.add("--.-");

        SYMBOL_LIST.add('r');
        CODE_LIST.add(".-.");

        SYMBOL_LIST.add('s');
        CODE_LIST.add("...");

        SYMBOL_LIST.add('t');
        CODE_LIST.add("-");

        SYMBOL_LIST.add('u');
        CODE_LIST.add("..-");

        SYMBOL_LIST.add('v');
        CODE_LIST.add("...-");

        SYMBOL_LIST.add('w');
        CODE_LIST.add(".--");

        SYMBOL_LIST.add('x');
        CODE_LIST.add("-..-");

        SYMBOL_LIST.add('y');
        CODE_LIST.add("-.--");

        SYMBOL_LIST.add('z');
        CODE_LIST.add("--..");

        SYMBOL_LIST.add('1');
        CODE_LIST.add(".----");

        SYMBOL_LIST.add('2');
        CODE_LIST.add("..---");

        SYMBOL_LIST.add('3');
        CODE_LIST.add("...--");

        SYMBOL_LIST.add('4');
        CODE_LIST.add("....-");

        SYMBOL_LIST.add('5');
        CODE_LIST.add(".....");

        SYMBOL_LIST.add('6');
        CODE_LIST.add("-....");

        SYMBOL_LIST.add('7');
        CODE_LIST.add("--...");

        SYMBOL_LIST.add('8');
        CODE_LIST.add("---..");

        SYMBOL_LIST.add('9');
        CODE_LIST.add("----.");

        SYMBOL_LIST.add('0');
        CODE_LIST.add("-----");

        SYMBOL_LIST.add(',');
        CODE_LIST.add("--..--");

        SYMBOL_LIST.add('.');
        CODE_LIST.add(".-.-.-");

        SYMBOL_LIST.add('?');
        CODE_LIST.add("..--..");

        SYMBOL_LIST.add(' ');
        CODE_LIST.add("/");
    }

    public Base2Morse() {
        this(null);
    }

    public Base2Morse(BaseEncoder baseEncoder) {
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

    public static class Base2MorseEncoder implements Encoder {
        @Override
        public byte[] encode(byte[] byteArr) {
            return Stream.of(new String(byteArr, CHARSET))
                    .map(String::toLowerCase)
                    .map(String::toCharArray)
                    .map(CharBuffer::wrap)
                    .flatMap(charBuffer -> charBuffer.chars().mapToObj(value -> (char) value))
                    .map(SYMBOL_LIST::indexOf)
                    .map(CODE_LIST::get)
                    .collect(Collectors.joining(" "))
                    .getBytes();
        }
    }

    public static class Base2MorseDecoder implements Decoder {
        @Override
        public byte[] decode(byte[] byteArr) {
            return Stream.of(new String(byteArr, CHARSET))
                    .map(string -> string.split(" "))
                    .flatMap(Arrays::stream)
                    .map(String::trim)
                    .filter(Predicate.not(String::isBlank))
                    .map(encodedSymbolArr -> encodedSymbolArr.split(" "))
                    .map(Stream::of)
                    .map(strings -> strings.map(CODE_LIST::indexOf))
                    .map(strings -> strings.map(SYMBOL_LIST::get))
                    .map(characterStream -> characterStream.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append))
                    .collect(Collectors.joining(""))
                    .getBytes();
        }
    }
}

