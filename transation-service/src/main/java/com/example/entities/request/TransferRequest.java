package com.example.entities.request;

import com.example.entities.response.TransferResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferRequest {
    private String from;
    private String to;
    private Double amount;
    private String message;

    public TransferResponse toEntity(){
        return new TransferResponse(from, to, amount, message);
    }
}
