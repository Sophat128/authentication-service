package org.example.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.model.Application;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationRequest {
    private String name;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String plateformType;
}
