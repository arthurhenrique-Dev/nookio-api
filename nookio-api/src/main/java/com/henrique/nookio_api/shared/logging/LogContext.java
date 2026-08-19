package com.henrique.nookio_api.shared.logging;

import org.slf4j.MDC;

import java.util.UUID;

public class LogContext {

    private static final String DEBUG_ID_KEY = "debugId";

    public static String getDebugId() {
        String id = MDC.get(DEBUG_ID_KEY);
        if (id == null || id.isBlank()) {
            id = UUID.randomUUID().toString().substring(0, 8);
            MDC.put(DEBUG_ID_KEY, id);
        }
        return id;
    }

    public static void setDebugId(String id) {
        if (id != null) {
            MDC.put(DEBUG_ID_KEY, id);
        }
    }

    public static void clear() {
        MDC.remove(DEBUG_ID_KEY);
    }
}
