package br.com.ecommerce.accounts.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.extension.ExtendWith;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(RolesInvocationContextProvider.class)
public @interface ContextualizeUserTypeWithRoles {
    String[] roles();
}