package com.example.models.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiResponse<T> {
    private String message;
    private T payload;
    private int statusCode;
    private boolean success;

    public  ApiResponse(String message, int statusCode, boolean success){
        this.message = message;
        this.statusCode = statusCode;
        this.success = success;
    }

    public ApiResponse(T payload, int statusCode, boolean success){
        this.payload = payload;
        this.statusCode = statusCode;
        this.success = success;
    }

}
