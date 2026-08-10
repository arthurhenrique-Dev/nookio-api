package com.henrique.nookio_api.modules.files.annotations.annotation;

import com.henrique.nookio_api.modules.files.annotations.aspect.ValidFileAspect;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE_USE, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidFileAspect.class)
public @interface ValidFile {

    String message() default "Arquivo inválido ou formato não suportado.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String[] allowedTypes() default {};
}
