package br.com.ecommerce.products.business.formatter;

import org.springframework.stereotype.Component;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;

import br.com.ecommerce.products.infra.entity.manufacturer.Phone;
import br.com.ecommerce.products.infra.entity.tools.interfaces.Formatter;

@Component
public class PhoneFormatter implements Formatter<Phone> {

    private final PhoneNumberUtil phoneUtils = PhoneNumberUtil.getInstance();


    @Override
    public String format(Phone obj) {
        String value = obj.getValue();

        this.validateStringPhoneNumber(value);
        com.google.i18n.phonenumbers.Phonenumber.PhoneNumber phone = this.parsePhoneNumber(value);
        this.validatePhoneNumber(phone);

        return this.phoneUtils.format(phone, PhoneNumberFormat.INTERNATIONAL);
    }

    private com.google.i18n.phonenumbers.Phonenumber.PhoneNumber parsePhoneNumber(String value) {
        try {
            PhoneNumberUtil phoneNumberUtils = PhoneNumberUtil.getInstance();
            return phoneNumberUtils.parse(value, "BR");

        } catch (NumberParseException e) {
            throw new IllegalArgumentException("Unable to parse phone number");
        }
    }

    private void validateStringPhoneNumber(String phone) {
        if (!phoneUtils.isPossibleNumber(phone, "BR"))
            throw new IllegalArgumentException("Invalid phone number");
    }

    private void validatePhoneNumber(com.google.i18n.phonenumbers.Phonenumber.PhoneNumber phone) {
        if (!phoneUtils.isValidNumberForRegion(phone, "BR"))
            throw new IllegalArgumentException("Invalid phone number");
    }
}