package com.github.gelald.redis.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserDTO {
    @NotNull
    private Long id;
    @NotNull
    private String username;
    @Email
    private String email;
}
