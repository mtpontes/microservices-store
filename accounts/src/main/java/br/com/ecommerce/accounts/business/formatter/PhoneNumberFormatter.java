package br.com.ecommerce.accounts.business.formatter;

import org.springframework.stereotype.Component;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;

import br.com.ecommerce.accounts.model.interfaces.Formatter;
import br.com.ecommerce.accounts.model.valueobjects.PhoneNumber;

@Component
public class PhoneNumberFormatter implements Formatter<PhoneNumber> {

	private PhoneNumberUtil phoneNumberUtils = PhoneNumberUtil.getInstance();

	
    @Override
    public String format(PhoneNumber phoneNumber) {
		String value = phoneNumber.getValue();

		com.google.i18n.phonenumbers.Phonenumber.PhoneNumber phone = this.parsePhoneNumber(value);
		this.validatePhoneNumber(phone);

		return this.phoneNumberUtils.format(phone, PhoneNumberFormat.INTERNATIONAL);
    }

	private com.google.i18n.phonenumbers.Phonenumber.PhoneNumber parsePhoneNumber(String value) {
		try {
			PhoneNumberUtil phoneNumberUtils = PhoneNumberUtil.getInstance();
			return phoneNumberUtils.parse(value, "BR");

		} catch (NumberParseException e) {
			throw new IllegalArgumentException("Unable to parse phone number");
		}
	}

	private void validatePhoneNumber(com.google.i18n.phonenumbers.Phonenumber.PhoneNumber phone) {
		if (!phoneNumberUtils.isValidNumberForRegion(phone, "BR"))
			throw new IllegalArgumentException("Invalid phone number");
	}
}