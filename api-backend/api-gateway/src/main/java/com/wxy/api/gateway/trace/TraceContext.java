package com.wxy.api.gateway.trace;

import org.slf4j.MDC;
import java.util.UUID;

public class TraceContext {
    public static final String TRACE_ID = "traceId";
    public static final String SPAN_ID = "spanId";
    public static final String USER_ID = "userId";
    public static final String INTERFACE_ID = "interfaceId";

    public static String generateTraceId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String generateSpanId() {
        return String.valueOf(System.currentTimeMillis());
    }

    public static void setTraceId(String traceId) {
        MDC.put(TRACE_ID, traceId);
    }

    public static String getTraceId() {
        String traceId = MDC.get(TRACE_ID);
        if (traceId == null) {
            traceId = generateTraceId();
            setTraceId(traceId);
        }
        return traceId;
    }

    public static void setSpanId(String spanId) {
        MDC.put(SPAN_ID, spanId);
    }

    public static void setUserId(String userId) {
        MDC.put(USER_ID, userId);
    }

    public static void setInterfaceId(String interfaceId) {
        MDC.put(INTERFACE_ID, interfaceId);
    }

    public static void clear() {
        MDC.clear();
    }
}
