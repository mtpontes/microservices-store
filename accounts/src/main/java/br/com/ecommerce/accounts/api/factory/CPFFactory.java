package br.com.ecommerce.accounts.api.factory;

import java.util.List;

import org.springframework.stereotype.Component;

import br.com.ecommerce.accounts.model.interfaces.Formatter;
import br.com.ecommerce.accounts.model.interfaces.Validator;
import br.com.ecommerce.accounts.model.valueobjects.CPF;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class CPFFactory {

	private List<Validator<CPF>> cpfValidators;
    private Formatter<CPF> cpfFormatter;


    public CPF createCPF(String cpfValue) {
        return new CPF(cpfValue, cpfValidators, cpfFormatter);
    }
}