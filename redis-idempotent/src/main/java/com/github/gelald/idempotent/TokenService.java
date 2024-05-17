package com.github.gelald.idempotent;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Edwin
 */
public interface TokenService {
    // 创建token
    String getToken();

    // 校验token并执行业务代码
    boolean checkToken(HttpServletRequest request);
}
