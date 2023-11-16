package com.example.entities.request;

import com.example.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
    @NotNull
    @NotEmpty
    private String content;
    private String attachmentFilePath;

    public Email toEntity(){
        return new Email(to,from,subject,content,attachmentFilePath);
    }
}
