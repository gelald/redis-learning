package com.github.gelald.redis.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    @NotNull(message = "username不能为null")
    private String username;
    @Email(message = "请填写正确格式的Email地址")
    private String email;
}
