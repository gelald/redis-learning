package com.github.gelald.redis.business;

import com.github.gelald.redis.annotation.ParamKey;
import lombok.Data;

import java.util.List;

/**
 * @author ngwingbun
 * date: 2024/7/20
 */
@Data
public class AddReq {
    /**
     * 用户名称
     */
    @ParamKey
    private String userName;

    /**
     * 用户手机号
     */
    @ParamKey
    private String userPhone;

    /**
     * 角色ID列表
     */
    private List<Long> roleIdList;
}
