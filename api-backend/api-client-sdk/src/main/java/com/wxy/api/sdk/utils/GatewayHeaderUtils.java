package com.wxy.api.sdk.utils;

import com.wxy.api.common.common.BusinessException;
import com.wxy.api.common.common.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import javax.servlet.http.HttpServletRequest;
import static com.wxy.api.common.constant.TraceCheckConstant.*;

/**
 * 网关染色校验
 */
@Slf4j
public class GatewayHeaderUtils {

    /**
     * 校验网关染色请求头
     * @param request HttpServletRequest
     * @throws BusinessException 如果校验失败
     */
    public static void validateGatewayHeaders(HttpServletRequest request) {
        log.info("网关染色校验开始");
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "未知请求");
        }
        String userId = request.getHeader(USER_ID_HEADER);
        String interfaceId = request.getHeader(INTERFACE_ID_HEADER);
        String gatewayInfo = request.getHeader(GATEWAY_HEADER);
        log.info("网关染色参数:\n userId: {} \n interfaceId: {} \n gatewayInfo: {} ", userId, interfaceId, gatewayInfo);
        if (StringUtils.isAnyBlank(userId, interfaceId, gatewayInfo)){
            log.error("网关染色校验不通过:\n userId: {} \n interfaceId: {} \n gatewayInfo: {} ", userId, interfaceId, gatewayInfo);
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "非法的网关请求");
        }
        if (!gatewayInfo.equals(SignUtils.getGatewaySign(userId, interfaceId))) {
            log.error("网关染色校验不通过:{}", gatewayInfo);
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "非法的网关请求");
        }
        log.info("网关染色校验通过");
    }
}
