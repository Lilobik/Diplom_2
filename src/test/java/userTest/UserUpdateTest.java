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

public class UserUpdateTest {

    private UserApi userApi;
    private User testUser;
    private String accessToken;

    @Before
    public void setUp() {
        userApi = new UserApi();
        testUser = User.getRandomUser();
        userApi.createUser(testUser);

        // Логин для получения accessToken
        ValidatableResponse loginResponse = userApi.loginUser(UserData.from(testUser), "");
        accessToken = loginResponse.extract().path("accessToken");
    }

    @After
    public void tearDown() {
        if (testUser != null) {
            // Удаление пользователя, используя сохраненный accessToken
            ValidatableResponse deleteResponse = userApi.deleteUser(accessToken);
            deleteResponse.statusCode(202);
        }
    }

    @Test
    @Description("Изменение данных пользователя с авторизацией")
    public void testUpdateUserDataWithAuthorization() {
        // Логин для получения accessToken
        ValidatableResponse loginResponse = userApi.loginUser(UserData.from(testUser), "");
        String accessToken = loginResponse.extract().path("accessToken");

        // Обновление данных пользователя
        User updatedUser = new User("updated_email572@ya.ru", "updated_password", "Updated User");
        ValidatableResponse updateResponse = userApi.updateUserData(updatedUser, accessToken);
        updateResponse.statusCode(200);
        updateResponse.body("success", equalTo(true));

        // Проверка, что данные пользователя действительно обновились
        ValidatableResponse getUserResponse = userApi.getUserData(accessToken);
        getUserResponse.statusCode(200);
        getUserResponse.body("success", equalTo(true));
        getUserResponse.body("user.email", equalTo(updatedUser.getEmail()));
        getUserResponse.body("user.name", equalTo(updatedUser.getName()));
    }

    @Test
    @Description("Изменение данных пользователя без авторизацией")
    public void testUpdateUserDataWithoutAuthorization() {
        // Изменение данных пользователя без авторизации
        User updatedUser = new User("updated_email271@ya.ru", "updated_password", "Updated User");
        ValidatableResponse updateResponse = userApi.updateUserData(updatedUser,"");
        updateResponse.statusCode(401);
        updateResponse.body("success", equalTo(false));
        updateResponse.body("message", equalTo("You should be authorised"));

        // Проверка, что данные пользователя не изменились
        ValidatableResponse getUserResponse = userApi.getUserData("null");
        getUserResponse.statusCode(401);
        getUserResponse.body("success", equalTo(false));
    }
}
