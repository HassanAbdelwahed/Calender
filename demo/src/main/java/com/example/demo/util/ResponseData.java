package com.example.demo.util;

import com.example.demo.util.utilInterfaces.Response;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResponseData<T> implements Response<T> {

    private T data;

    private String message;

    public ResponseData(T data, String message) {
        this.data = data;
        this.message = message;
    }
}
