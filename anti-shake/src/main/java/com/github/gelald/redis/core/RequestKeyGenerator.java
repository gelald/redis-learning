package com.github.gelald.redis.core;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ReflectUtil;
import com.github.gelald.redis.annotation.ParamKey;
import com.github.gelald.redis.annotation.RequestCache;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @author ngwingbun
 * date: 2024/7/20
 */
public class RequestKeyGenerator {
    public static String generate(ProceedingJoinPoint joinPoint) {
        //获取连接点的方法签名对象
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        //Method对象
        Method method = methodSignature.getMethod();
        //获取Method对象上的注解对象
        RequestCache requestLock = method.getAnnotation(RequestCache.class);
        //获取方法参数
        final Object[] args = joinPoint.getArgs();
        //获取Method对象上所有的注解
        final Parameter[] parameters = method.getParameters();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parameters.length; i++) {
            final ParamKey keyParam = parameters[i].getAnnotation(ParamKey.class);
            //如果属性不是RequestKeyParam注解，则不处理
            if (keyParam == null) {
                continue;
            }
            //如果属性是RequestKeyParam注解，则拼接 连接符 "& + RequestKeyParam"
            sb.append(requestLock.delimiter()).append(args[i]);
        }
        //如果方法上没有加RequestKeyParam注解
        if (CharSequenceUtil.isEmpty(sb.toString())) {
            //获取方法上的多个注解（为什么是两层数组：因为第二层数组是只有一个元素的数组）
            final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
            //循环注解
            for (int i = 0; i < parameterAnnotations.length; i++) {
                final Object object = args[i];
                //获取注解类中所有的属性字段
                final Field[] fields = object.getClass().getDeclaredFields();
                for (Field field : fields) {
                    //判断字段上是否有RequestKeyParam注解
                    final ParamKey annotation = field.getAnnotation(ParamKey.class);
                    //如果没有，跳过
                    if (annotation == null) {
                        continue;
                    }
                    //如果有，设置Accessible为true（为true时可以使用反射访问私有变量，否则不能访问私有变量）
                    field.setAccessible(true);
                    //如果属性是RequestKeyParam注解，则拼接 连接符" & + RequestKeyParam"
                    sb.append(requestLock.delimiter()).append(ReflectUtil.getFieldValue(object, field));
                }
            }
        }
        //返回指定前缀的key
        return requestLock.prefix() + sb;
    }
}
