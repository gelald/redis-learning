package com.github.gelald.redis.controller;

import cn.hutool.core.util.IdUtil;
import com.github.gelald.redis.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/spring-cache")
public class CacheController {

    @CachePut(value = "say", key = "'p_' + #p0")
    @GetMapping("/cachePut")
    public String cachePut(String name) {
        return "hello " + name + "-->" + IdUtil.randomUUID();
    }

    @Cacheable(value = "selectPage", key = "#p0 + '-' + #p1")
    @GetMapping("/page")
    public Object selectPage(@RequestParam(value = "page") Integer page,
                             @RequestParam(value = "size") Integer size) {
        return "selectPage: " + page + "," + size + "-->" + IdUtil.randomUUID();
    }

    @Cacheable(value = "condition", key = "#p0", condition = "#p0 % 2 == 0")
    @GetMapping("/condition")
    public String setByCondition(@RequestParam("age") Integer age) {
        return "condition: " + age + "-->" + IdUtil.randomUUID();
    }

    @Cacheable(value = "unless", key = "#p0", unless = "#p0 % 2 == 0")
    @GetMapping("/unless")
    public String setByUnless(@RequestParam("age") Integer age) {
        return "unless: " + age + "-->" + IdUtil.randomUUID();
    }


    /**
     * {@link CacheEvict}：补充参数释义, 其余参数可参考 @Cacheable
     * <p>
     * allEntries: 是否删除缓存中的所有条目. 默认情况下, 只移除相关键下的值. 注意: 参数设置为 true 会忽略key
     * beforeInvocation: 是否应该在调用方法之前进行驱逐.
     * true: 将导致不管方法结果如何(是否抛出异常)都将发生驱逐. 调用方法之前发生
     * false(默认值): 意味着在成功调用方法后, 缓存清除操作将发生(即仅当调用没有抛出异常时)。
     * <p>
     * 使用释义:
     * 新增数据, 查询时可自行加入缓存
     * 分页的情况, 会导致已缓存的分页数据结构异常, 清空分页数据, 从新加入缓存
     */
    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    @CacheEvict(value = "selectPage", allEntries = true, beforeInvocation = true)
    public Object insert(@Validated @RequestBody UserDTO userDTO) {
        return "insert: " + userDTO.toString() + "--->" + IdUtil.randomUUID();
    }

    @CacheEvict(value = "say", key = "'p_' + #name")
    @GetMapping(path = "evict")
    public String evict(String name) {
        return "hello " + name + "-->" + IdUtil.randomUUID();
    }

    /**
     * @CacheEvict： 删除接口, 建议配置allEntries = true, 指定 key 可能无法指定清除复杂缓存, 如分页的缓存
     * <p>
     * 使用释义:
     * 清空缓存, 这个地方可以使用组合注解: @Caching 根据ID清除指定的缓存(根据ID查询接口添加的缓存),
     * 在清除所有的分页缓存. (@Caching(evict={@CacheEvict(自行补充), @CacheEvict(自行补充)})
     * 删除对应的数据, 肯定要处理对应的缓存. 入参ID可通过指定 key 清除指定的缓存
     * 但是分页接口无法通过入参进行指定的缓存清除, 故干脆配置 allEntries = true 清除所有缓存
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @CacheEvict(value = {"selectById", "selectPage"}, allEntries = true, beforeInvocation = true)
    public Object deleteById(@PathVariable("id") Long id) {
        return "deleteById" + id + "-->" + IdUtil.randomUUID();
    }


    @Caching(cacheable = @Cacheable(value = "say", key = "'p_' + #p0"),
            evict = @CacheEvict(value = "condition", key = "#p1"))
    @GetMapping("/caching")
    public String caching(String name, Integer age) {
        return "caching " + name + "," + age + "-->" + IdUtil.randomUUID();
    }
}
