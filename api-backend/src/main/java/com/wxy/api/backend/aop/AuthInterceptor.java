package com.wxy.api.backend.aop;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.wxy.api.backend.annotation.AuthCheck;
import com.wxy.api.backend.service.UserService;
import com.wxy.api.common.common.BusinessException;
import com.wxy.api.common.common.ErrorCode;
import com.wxy.api.common.model.entity.User;
import com.wxy.api.common.model.enums.UserRoleEnum;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * 权限校验 AOP
 */
@Aspect    // 作用是把当前类标识为一个切面供容器读取
@Component // 标注一个类为Spring容器的Bean
public class AuthInterceptor
{

    @Resource
    private UserService userService;

    /**
     * 执行拦截(AOP切面执行校验)
     *
     * @param joinPoint 环绕通知
     * @param authCheck 权限校验注解
     * @return 通过权限校验，放行
     */
    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable
    {
        List<String> anyRole = Arrays.stream(authCheck.anyRole()).filter(StringUtils::isNotBlank).toList();
        String mustRole = authCheck.mustRole();
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        // 当前登录用户
        User user = userService.getLoginUser(request);
        String userRole = user.getUserRole();
        // 检查账号是否被锁定
        if (UserRoleEnum.Is_locked.getValue().equals(userRole)) {
            throw new BusinessException(ErrorCode.Account_blocked_ERROR);
        }
        // 拥有任意权限即通过
        if (CollectionUtils.isNotEmpty(anyRole) && !anyRole.contains(userRole)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 必须有所有权限才通过
        if (StringUtils.isNotBlank(mustRole) && !mustRole.equals(userRole)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 通过权限校验，放行
        return joinPoint.proceed();
    }
}

