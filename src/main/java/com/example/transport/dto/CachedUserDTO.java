package com.example.transport.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
//@AllArgsConstructor
public class CachedUserDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String email;
    private String password;
    private String role;

    public CachedUserDTO(String email, String password, String role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }

}