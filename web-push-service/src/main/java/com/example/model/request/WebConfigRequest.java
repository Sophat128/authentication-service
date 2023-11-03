package com.example.model.request;

import com.example.model.entities.WebDataConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebConfigRequest {
    private String privateKey;
    private String publicKey;

    public WebDataConfig toEntity(){
        return new WebDataConfig(privateKey, publicKey);
    }
}
