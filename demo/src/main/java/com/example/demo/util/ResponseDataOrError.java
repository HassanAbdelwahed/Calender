package com.example.demo.util;

import com.example.demo.util.utilInterfaces.Response;
import lombok.Data;

@Data
public class ResponseDataOrError<T> implements Response<T> {
    private String message;

    public ResponseDataOrError(String message) {
        this.message = message;
    }
}
