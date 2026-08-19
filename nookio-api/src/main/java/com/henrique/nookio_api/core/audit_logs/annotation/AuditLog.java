package com.henrique.nookio_api.core.audit_logs.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuditLog {

    /**
     * O recurso auditado (ex: "SCHEDULE", "USER", "PROPERTY")
     */
    String resource() default "";
    /**
     * A operação realizada (ex: "CREATE", "UPDATE", "DELETE", "CANCEL")
     */
    String operation() default "";
    /**
     * A permissão requerida para a ação (ex: "SCHEDULE_WRITE", "USER_DELETE")
     */
}
