package br.com.ecommerce.accounts.business.formatter;

import org.springframework.stereotype.Component;

import br.com.ecommerce.accounts.model.interfaces.Formatter;
import br.com.ecommerce.accounts.model.valueobjects.CPF;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class CPFFormatter implements Formatter<CPF> {
    
    private final br.com.caelum.stella.format.CPFFormatter formatter =
        new br.com.caelum.stella.format.CPFFormatter();


    @Override
    public String format(CPF cpf) {
        String value = cpf.getValue();

        if (formatter.isFormatted(value)) return value;
        if (formatter.canBeFormatted(value)) return formatter.format(value);

        throw new IllegalArgumentException("Invalid CPF");
    }
}