package ru.netology.test.data;


import com.github.javafaker.Faker;
import lombok.Value;



public class DataGenerator {


    private DataGenerator() {
    }

    public static String generateLogin() {
        Faker faker = new Faker();
        return faker.name().username();
    }

    public static String generatePassword(){
        Faker faker = new Faker();
        return faker.internet().password(6, 20);
    }

    public static RegistrationDto registrationDto (String status) {
        return new RegistrationDto(generateLogin(), generatePassword(), status);
    }

    public static RegistrationDto witNewLogin (RegistrationDto registrationDto) {
        return new RegistrationDto(generateLogin(), registrationDto.password, registrationDto.status);
    }

    public static RegistrationDto witNewPassword (RegistrationDto registrationDto) {
        return new RegistrationDto(registrationDto.login, generatePassword(), registrationDto.status);
    }


    @Value
    public static class RegistrationDto {
        String login;
        String password;
        String status;
    }




}
