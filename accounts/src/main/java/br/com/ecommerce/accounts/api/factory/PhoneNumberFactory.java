package br.com.ecommerce.accounts.api.factory;

import java.util.List;

import org.springframework.stereotype.Component;

import br.com.ecommerce.accounts.model.interfaces.Formatter;
import br.com.ecommerce.accounts.model.interfaces.Validator;
import br.com.ecommerce.accounts.model.valueobjects.PhoneNumber;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class PhoneNumberFactory {

	private List<Validator<PhoneNumber>> phoneNumberValidators;
    private Formatter<PhoneNumber> phoneNumberFormatter;


    public PhoneNumber createPhoneNumber(String phoneNumberValue) {
        return new PhoneNumber(phoneNumberValue, phoneNumberValidators, phoneNumberFormatter);
    }
}