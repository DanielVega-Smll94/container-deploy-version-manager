package com.deploymanager.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.io.Serializable;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SSHRequestDto implements Serializable {
    @NotBlank
    private String host;
    @NotBlank
    private int port = 22;
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @NotBlank
    private String command;
}

/*private String privateKeyPath;*/
