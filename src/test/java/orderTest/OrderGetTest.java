package orderTest;

import io.qameta.allure.Description;
import io.restassured.response.ValidatableResponse;
import order.OrderApi;
import org.junit.Before;
import org.junit.Test;
import user.User;
import user.UserApi;
import user.UserData;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasSize;

public class OrderGetTest {
    private UserApi userApi;
    private OrderApi orderApi;
    private User testUser;
    private String accessToken;

    @Before
    public void setUp() {
        userApi = new UserApi();
        orderApi = new OrderApi();
        testUser = new User("test_user123@ya.ru", "test_password", "Test User");
        userApi.createUser(testUser);
        ValidatableResponse loginResponse = userApi.loginUser(UserData.from(testUser), "");
        accessToken = loginResponse.extract().path("accessToken");
    }

    @Test
    @Description("Получение заказов авторизованным пользователем")
    public void testGetOrdersByAuthorizedUser() {

        // Создание заказа для авторизованного пользователя
        ValidatableResponse createOrderResponse = orderApi.createOrderWithIngredients(List.of("61c0c5a71d1f82001bdaaa6f", "61c0c5a71d1f82001bdaaa70"), accessToken);
        createOrderResponse.statusCode(200);
        createOrderResponse.body("success", equalTo(true));

        // Получение заказов
        ValidatableResponse response = orderApi.getOrders(accessToken);
        response.statusCode(200);
        response.body("success", equalTo(true));

        // Проверка, что в полученных заказах есть созданный заказ
        response.body("orders", hasItem(hasEntry("number", createOrderResponse.extract().path("order.number"))));
    }

    @Test
    @Description("Получение заказов неавторизованным пользователем")
    public void testGetOrdersByUnauthorizedUser() {
        ValidatableResponse response = orderApi.getOrders("");
        response.statusCode(401);
        response.body("success", equalTo(false));
        response.body("message", equalTo("You should be authorised"));
    }
}
