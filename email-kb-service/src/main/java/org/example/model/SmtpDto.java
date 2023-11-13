package org.example.model;


import com.example.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SmtpDto {
    private Long id;
    private String username;
    private UserDto userDto;



}