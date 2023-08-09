package userTest;

import io.qameta.allure.Description;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import user.User;
import user.UserApi;
import user.UserData;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;

public class UserLoginTest {

    private UserApi userApi;
    private User testUser;

    @Before
    public void setUp() {
        userApi = new UserApi();
        // Регистрация нового пользователя для тестов
        testUser = User.getRandomUser();
        userApi.createUser(testUser);
    }

    @After
    public void tearDown() {
        // Удаление тестового пользователя после выполнения тестов
        if (testUser != null) {
            ValidatableResponse response = userApi.loginUser(UserData.from(testUser), "");
            String accessToken = response.extract().path("accessToken");
            userApi.deleteUser(accessToken).statusCode(202);
        }
    }

    @Test
    @Description("Тест успешной авторизации пользователя")
    public void testSuccessfulUserLogin() {
        // Логин с правильными данными
        ValidatableResponse response = userApi.loginUser(UserData.from(testUser), "");
        response.statusCode(200);
        response.body("success", equalTo(true));
        assertNotNull("Access token should not be null", response.extract().path("accessToken"));
    }

    @Test
    @Description("Тест логина с некорректными данными")
    public void testUserLoginWithInvalidCredentials() {
        // Создание объекта с некорректными данными
        UserData invalidCredentials = new UserData("invalid_email@example.com", "invalid_password");

        // Логин с неверными данными
        ValidatableResponse response = userApi.loginUser(invalidCredentials, "");
        response.statusCode(401);
        response.body("success", equalTo(false));
        response.body("message", equalTo("email or password are incorrect"));
    }

}
