package com.example.demo.util;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class ResponseData<T> {

    private T data;
    private String message;

    public ResponseData(T data, String message) {
        this.data = data;
        this.message = message;
    }
}
