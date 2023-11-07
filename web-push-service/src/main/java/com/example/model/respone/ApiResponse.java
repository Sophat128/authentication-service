package com.example.model.respone;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponse<T> {
    @JsonInclude(JsonInclude.Include.NON_NULL)

    String message ;
    @JsonInclude(JsonInclude.Include.NON_NULL)

    Integer status;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    T payload;
    public ApiResponse(String message, Integer status) {
        this.message = message;
        this.status = status;
    }


}