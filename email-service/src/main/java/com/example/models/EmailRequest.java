package com.example.models;

import com.example.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailRequest {
    private String to;
    private String from;
    private String subject;
    private String content;

//    public Email toEntity(){
//        return new Email(to,from,subject,content);
//    }
}
