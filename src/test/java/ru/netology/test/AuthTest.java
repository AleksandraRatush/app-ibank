package ru.netology.test;

import com.codeborne.selenide.Condition;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.netology.test.data.DataGenerator;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static io.restassured.RestAssured.given;

public class AuthTest {

    private static DataGenerator.RegistrationDto registrationActiveDto = DataGenerator.registrationDto("active");
    private static DataGenerator.RegistrationDto registrationBlockedDto = DataGenerator.registrationDto("blocked");


    private static RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setPort(9999)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();

    @BeforeAll
    static void setUpAll() {
        registerUser(registrationActiveDto);
        registerUser(registrationActiveDto);
    }


    @Test
    public void loginSuccess() {
        login(registrationActiveDto);
        $("h2.heading" ).shouldBe(Condition.visible, Duration.ofSeconds(50))
                .shouldHave(Condition.exactText("Личный кабинет"));
    }



    @Test
    public void loginBlocked() {
        login(registrationBlockedDto);
        checkErrorNotification();
    }

    @Test
    public void loginIncorrectLogin() {
        login(DataGenerator.witNewLogin(registrationActiveDto));
        checkErrorNotification();
    }

    @Test
    public void loginIncorrectPassword() {
        login(DataGenerator.witNewPassword(registrationActiveDto));
        checkErrorNotification();
    }

    @Test
    public void loginIncorrectLoginAndPassword() {
        login(DataGenerator.registrationDto("active"));
        checkErrorNotification();
    }

    @Test
    public void loginWithoutLogin() {
        loginWithoutLogin(registrationActiveDto);
        $("[data-test-id='login'].input_invalid .input__sub")
                .shouldBe(Condition.visible)
                .shouldHave(Condition.exactText("Поле обязательно для заполнения"));


    }

    @Test
    public void loginWithoutPassword() {
        loginWithoutPassword(registrationActiveDto);
        $("[data-test-id='password'].input_invalid .input__sub")
                .shouldBe(Condition.visible)
                .shouldHave(Condition.exactText("Поле обязательно для заполнения"));


    }

    @Test
    public void loginWithoutLoginAndPassword() {
        open("http://0.0.0.0:9999");
        $("[data-test-id='action-login']").click();
        $("[data-test-id='login'].input_invalid .input__sub")
                .shouldBe(Condition.visible)
                .shouldHave(Condition.exactText("Поле обязательно для заполнения"));
        $("[data-test-id='password'].input_invalid .input__sub")
                .shouldBe(Condition.visible)
                .shouldHave(Condition.exactText("Поле обязательно для заполнения"));


    }

    private static void registerUser(DataGenerator.RegistrationDto registrationDto) {
        // сам запрос
        given() // "дано"
                .spec(requestSpec) // указываем, какую спецификацию используем
                .body(registrationDto) // передаём в теле объект, который будет преобразован в JSON
                .when() // "когда"
                .post("/api/system/users") // на какой путь, относительно BaseUri отправляем запрос
                .then() // "тогда ожидаем"
                .statusCode(200); // код 200 OK
    }

    private void login(DataGenerator.RegistrationDto registrationDto) {
        open("http://0.0.0.0:9999");
        $("[data-test-id='login'] .input__control")
                .setValue(registrationDto.getLogin());
        $("[data-test-id='password'] .input__control")
                .setValue(registrationDto.getPassword());
        $("[data-test-id='action-login']").click();
    }

    private void loginWithoutPassword(DataGenerator.RegistrationDto registrationDto) {
        open("http://0.0.0.0:9999");
        $("[data-test-id='login'] .input__control")
                .setValue(registrationDto.getLogin());
        $("[data-test-id='action-login']").click();
    }

    private void loginWithoutLogin(DataGenerator.RegistrationDto registrationDto) {
        open("http://0.0.0.0:9999");
        $("[data-test-id='password'] .input__control")
                .setValue(registrationDto.getPassword());
        $("[data-test-id='action-login']").click();
    }

    private void checkErrorNotification() {
        $("[data-test-id='error-notification'] .notification__title").shouldBe(Condition.visible, Duration.ofSeconds(50))
                .shouldHave(Condition.exactText("Ошибка"));
        $("[data-test-id='error-notification'] .notification__content").shouldBe(Condition.visible, Duration.ofSeconds(50))
                .shouldHave(Condition.exactText("Ошибка! Неверно указан логин или пароль"));
    }
}
