package br.com.ecommerce.accounts.utils;

import java.util.Arrays;
import java.util.Base64;

public class TokenFormatValidatorUtils {

    public static boolean isValidTokenFormat(String token) {
        var parts = Arrays.asList(token.split("\\."));

        if (parts.size() != 3)
            return false;

        try {
            parts.forEach(part -> Base64.getUrlDecoder().decode(part));
            return true;

        } catch (IllegalArgumentException ex) {
            return false;
        }
    }
}
