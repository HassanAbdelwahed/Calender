package com.example.demo.util;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class ResponseData<T> extends ResponseDataOrError<T> {

    private T data;

    public ResponseData(T data, String message) {
        super(message);
        this.data = data;
    }
}
