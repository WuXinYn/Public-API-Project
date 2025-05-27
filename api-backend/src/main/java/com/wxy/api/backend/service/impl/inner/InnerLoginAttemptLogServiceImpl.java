package com.wxy.api.backend.service.impl.inner;


import com.wxy.api.common.service.InnerLoginAttemptLogService;

public class InnerLoginAttemptLogServiceImpl implements InnerLoginAttemptLogService
{
    /**
     * 查询IP是否被封禁
     *
     * @param ipAddress
     * @return
     */
    @Override
    public boolean checkBlockedIP(String ipAddress) {
        return true;
    }
}
