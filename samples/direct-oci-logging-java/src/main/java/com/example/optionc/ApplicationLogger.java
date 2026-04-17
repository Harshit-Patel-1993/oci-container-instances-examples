package com.example.optionc;

public interface ApplicationLogger extends AutoCloseable {
    void log(String message);

    @Override
    default void close() {
        // no-op for implementations without resources to release
    }
}
