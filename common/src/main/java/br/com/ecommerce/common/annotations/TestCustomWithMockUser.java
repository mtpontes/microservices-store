package br.com.ecommerce.common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

@TestTemplate
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(RolesInvocationContextProvider.class)
public @interface TestCustomWithMockUser {
    String[] roles() default {};
    String[] userId() default {};
    IdRolePair[] idRolePair() default {};
}