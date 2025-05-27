package com.wxy.api.common.service;

public interface InnerLoginAttemptLogService
{
    boolean checkBlockedIP(String ipAddress);
}
