package com.redis.avro;

import java.util.HashMap;
import java.util.Map;
import org.apache.pulsar.client.api.Schema;
import org.apache.pulsar.client.internal.DefaultImplementation;

public class SchemaHelper {

    private static final Map<String, Schema<?>> schemas = new HashMap<>();

    static {
        schemas.put("byte[]", DefaultImplementation.newBytesSchema());
        schemas.put("ByteBuffer", DefaultImplementation.newByteBufferSchema());
        schemas.put("String", DefaultImplementation.newStringSchema());
        schemas.put("Byte", DefaultImplementation.newByteSchema());
        schemas.put("Short", DefaultImplementation.newShortSchema());
        schemas.put("Integer", DefaultImplementation.newIntSchema());
        schemas.put("Long", DefaultImplementation.newLongSchema());
        schemas.put("Boolean", DefaultImplementation.newBooleanSchema());
        schemas.put("Float", DefaultImplementation.newFloatSchema());
        schemas.put("Double", DefaultImplementation.newDoubleSchema());
        schemas.put("Date", DefaultImplementation.newDateSchema());
        schemas.put("Time", DefaultImplementation.newTimeSchema());
        schemas.put("Timestamp", DefaultImplementation.newTimestampSchema());
    }

    public static <T> Schema<T> getSchema(String name) {
        return (Schema<T>) schemas.get(name);
    }
}
