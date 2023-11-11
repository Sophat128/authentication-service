package com.example.entities.request;

import com.example.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailRequest {
    private List<String> to;
    private String from;
    private String subject;
    private String content;
    private String attachmentFilePath;

    public Email toEntity(){
        return new Email(to,from,subject,content,attachmentFilePath);
    }
}
