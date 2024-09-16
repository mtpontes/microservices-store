package br.com.ecommerce.products.utils.util;

import java.util.List;
import java.util.Random;

import org.springframework.boot.test.context.TestComponent;
import org.springframework.test.util.ReflectionTestUtils;

import br.com.ecommerce.products.infra.entity.manufacturer.Phone;

@TestComponent
public class PhoneUtils {

    private Random random = new Random();
    private List<String> phones = List.of(
        "11", "21", "31", "41", "51", "61", "71", "81", "91", "92", "93", "94", 
        "95", "96", "97", "98", "99", "12", "13", "14", "15", "16", "17", "18", 
        "19", "22", "24", "27", "28", "32", "33", "34", "35", "37", "38", "42", 
        "43", "44", "45", "46", "47", "48", "49", "53", "54", "55", "62", "63", 
        "64", "65", "66", "67", "68", "69", "73", "74", "75", "77", "79", "82", 
        "83", "84", "85", "86", "87", "88", "89"
    );

    public Phone getPhoneInstance() {
        String phoneString = this.getRandomPhoneString();
        Phone phone = new Phone();
        ReflectionTestUtils.setField(phone, "value", phoneString);

        return phone;
    }

    public String getRandomPhoneString() {
        String ddd = this.getRandomDdd();

        String number = this.getRandomNumber();
        String topFive = number.substring(0, 5);
        String lastFour = number.substring(5);

        return String.format("+55 %s %s-%s", ddd, topFive, lastFour);
    }

    public String getRandomNumber() {
        return String.valueOf(900000000 + random.nextInt(100000000));
    }

    private String getRandomDdd() {
        return this.phones.get(random.nextInt(phones.size()));
    }
}