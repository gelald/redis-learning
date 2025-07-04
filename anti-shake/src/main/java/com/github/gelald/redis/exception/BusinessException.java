package com.github.gelald.redis.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author ngwingbun
 * date: 2024/7/20
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BusinessException extends RuntimeException {
    private int code;
    private String message;

    public BusinessException(String message) {
        super(message);
        this.code = 1;
        this.message = message;
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
}
