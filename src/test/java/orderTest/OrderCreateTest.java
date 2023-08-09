package orderTest;

import io.qameta.allure.Description;
import io.restassured.response.ValidatableResponse;
import order.OrderApi;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import user.User;
import user.UserApi;
import user.UserData;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class OrderCreateTest {

    private UserApi userApi;
    private OrderApi orderApi;
    private User testUser;
    private String accessToken;

    @Before
    public void setUp() {
        userApi = new UserApi();
        orderApi = new OrderApi();
        testUser = User.getRandomUser();
        userApi.createUser(testUser);
        ValidatableResponse loginResponse = userApi.loginUser(UserData.from(testUser), "");
        accessToken = loginResponse.extract().path("accessToken");
    }

    @After
    public void tearDown() {
        if (testUser != null) {
            userApi.deleteUser(accessToken).statusCode(202);
        }
    }

    @Test
    @Description("Создание нового заказа с ингредиентами и авторизацией")
    public void testCreateOrderWithIngredientsAndAuthorization() {
        List<String> ingredientIds = Arrays.asList("61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa6f");
        ValidatableResponse response = orderApi.createOrderWithIngredients(ingredientIds, accessToken);
        response.statusCode(200);
        response.body("success", equalTo(true));
        response.body("order.number", notNullValue());
    }

    @Test
    @Description("Создание нового заказа без ингредиентов и авторизации")
    public void createOrderWithoutAuthorization() {
        ValidatableResponse response = orderApi.createOrderWithoutAuthorization();
        response.statusCode(400);
        response.body("success", equalTo(false));
        response.body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @Description("Создание с неверным хешем ингредиентов")
    public void testCreateOrderWithInvalidIngredientsAndAuthorization() {
        List<String> ingredientIds = Arrays.asList("invalid_hash1", "invalid_hash2");
        ValidatableResponse response = orderApi.createOrderWithInvalidIngredients(ingredientIds, accessToken);
        response.statusCode(500);
    }

    @Test
    @Description("Создание нового заказа с авторизацией, но без ингредиентов")
    public void testCreateOrderWithAuthorizationWithoutIngredients() {
        ValidatableResponse response = orderApi.createOrderWithoutIngredients(accessToken);
        response.statusCode(400);
        response.body("success", equalTo(false));
        response.body("message", equalTo("Ingredient ids must be provided"));
    }

}
