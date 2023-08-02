package userTest;

import io.qameta.allure.Description;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import user.User;
import user.UserApi;
import user.UserData;

import java.util.Random;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;

public class UserRegistrationTest {

    private UserApi userApi;
    private User createdUser;

    @Before
    public void setUp() {
        userApi = new UserApi();
    }

    @After
    public void tearDown() {
        if (createdUser != null) {
            // Получение токена для созданного пользователя
            ValidatableResponse loginResponse = userApi.loginUser(UserData.from(createdUser), createdUser.getEmail());
            String accessToken = loginResponse.extract().path("accessToken");

            // Удаляем пользователя
            ValidatableResponse deleteResponse = userApi.deleteUser(accessToken);
            deleteResponse.statusCode(202);
        }
    }

    @Test
    @Description("Тест регистрации уникального пользователя")
    public void testRegUniqueUser() {
        // Создание нового уникального пользователя
        Random random = new Random();
        String email = "testuni" + random.nextInt(1000) + "@ya.ru";
        createdUser = new User(email, "password123", "Unique");
        ValidatableResponse response = userApi.createUser(createdUser);

        // Проверка, что запрос завершился успешно
        response.statusCode(200);

        // Получение accessToken из ответа
        String accessToken = response.extract().path("accessToken");
        assertNotNull("Access token should not be null", accessToken);

        // Проверка, что пользователь существует и его данные совпадают с отправленными
        ValidatableResponse loginResponse = userApi.loginUser(UserData.from(createdUser), accessToken);
        loginResponse.statusCode(200);
        loginResponse.body("user.email", equalTo(createdUser.getEmail()));
        loginResponse.body("user.name", equalTo(createdUser.getName()));
    }

    @Test
    @Description("Регистрация пользователя с уже существующимим данными")
    public void testRegExistingUser() {
        // Создание нового пользователя
        Random random = new Random();
        String email = "testuni" + random.nextInt(1000) + "@ya.ru";
        createdUser = new User(email, "password123", "Unique");
        ValidatableResponse response = userApi.createUser(createdUser);

        // Проверка, что запрос завершился успешно
        response.statusCode(200);

        // Создание пользователя с теми же данными
        ValidatableResponse duplicateResponse = userApi.createUser(createdUser);

        // Проверка, что запрос завершился с ошибкой 403
        duplicateResponse.statusCode(403);
        duplicateResponse.body("success", equalTo(false));
        duplicateResponse.body("message", equalTo("User already exists"));
    }

    @Test
    @Description("Тест регистрации пользователя без указания обезательного атрибута")
    public void testRegUserWithMissingFields() {
        UserApi userApi = new UserApi();

        // Создание нового пользователя без указания имени
        User newUser = new User("user_without_name@example.com", "password123", null);
        ValidatableResponse response = userApi.createUser(newUser);

        // Проверка, что запрос завершился с ошибкой
        response.statusCode(403);
        response.body("success", equalTo(false));
        response.body("message", equalTo("Email, password and name are required fields"));
    }

}
