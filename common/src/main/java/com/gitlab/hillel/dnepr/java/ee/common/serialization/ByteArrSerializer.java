package com.gitlab.hillel.dnepr.java.ee.common.serialization;

public interface ByteArrSerializer {
    <T> byte[] toByteArr(T object);

    <T> T fromByteArr(byte[] byteArr, Class<T> objectType);
}
