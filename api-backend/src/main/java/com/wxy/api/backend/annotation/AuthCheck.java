package com.wxy.api.backend.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限校验
 *
 */
@Target(ElementType.METHOD)         // 描述注解的使用范围: 用于方法
@Retention(RetentionPolicy.RUNTIME) // 指定被注解的元素在什么时候有效: 注解不仅被保存到class文件中，jvm加载class文件之后，仍然存在
public @interface AuthCheck {

    /**
     * 有任何一个角色
     */
    String[] anyRole() default "";

    /**
     * 必须有某个角色
     */
    String mustRole() default "";

}

