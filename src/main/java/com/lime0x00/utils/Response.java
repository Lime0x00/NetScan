package com.lime0x00.utils;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public final class Response<T> {

    public enum ResponseCode {
        SUCCESS,
        FAILURE,
        EXCEPTION
    }

    private final Optional<T> data;  
    private final ResponseCode code;
    private final String message;
    private final Throwable error;
    private final LocalDateTime timestamp;

    private Response(Optional<T> data, ResponseCode code, String message, Throwable error) {
        this.data = data;
        this.code = Objects.requireNonNull(code, "Response code cannot be null");
        this.message = message != null ? message : "";
        this.error = error;
        this.timestamp = LocalDateTime.now();
        logActivity();
    }

    public static <T> Response<T> success(T data) {
        return new Response<>(Optional.of(data), ResponseCode.SUCCESS, null, null);
    }

    public static <T> Response<T> failure(String message) {
        return new Response<>(Optional.empty(), ResponseCode.FAILURE, message, null);
    }

    public static <T> Response<T> exception(String message, Throwable error) {
        return new Response<>(Optional.empty(), ResponseCode.EXCEPTION, message, error);
    }

    public Optional<T> getData() {
        return data;
    }

    public ResponseCode getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Throwable getError() {
        return error;
    }

    public LocalDateTime getTimeStamp() {
        return timestamp;
    }

    public boolean isSuccess() {
        return code == ResponseCode.SUCCESS;
    }

    public boolean isFailure() {
        return code == ResponseCode.FAILURE;
    }

    public boolean isException() {
        return code == ResponseCode.EXCEPTION;
    }

    @Override
    public String toString() {
        return "Response{" +
                "data=" + data.orElse(null) +
                ", code=" + code +
                ", message='" + message + '\'' +
                ", error=" + (error != null ? error.getClass().getName() + ": " + error.getMessage() : "null") +
                ", timestamp=" + timestamp +
                '}';
    }
    
    private void logActivity () {
    }
}
