package order;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import utils.Specification;

import java.util.List;

import static io.restassured.RestAssured.given;

public class OrderApi extends Specification {
    private static final String ORDER_PATH = "api/orders";

    @Step("Creating a new order without ingredients and authorization")
    public ValidatableResponse createOrderWithoutAuthorization() {
        return given()
                .spec(Specification.requestSpecification())
                .header("Authorization", "")
                .body(new Order(List.of()))
                .when()
                .post(ORDER_PATH)
                .then();
    }

    @Step("Creating a new order with invalid ingredient hash")
    public ValidatableResponse createOrderWithInvalidIngredients(List<String> ingredientIds, String accessToken) {
        return given()
                .spec(Specification.requestSpecification())
                .header("Authorization", accessToken)
                .body(new Order(ingredientIds))
                .when()
                .post(ORDER_PATH)
                .then();
    }

    @Step("Creating a new order with ingredients")
    public ValidatableResponse createOrderWithIngredients(List<String> ingredientIds, String accessToken) {
        return given()
                .spec(Specification.requestSpecification())
                .header("authorization", accessToken)
                .body(new Order(ingredientIds))
                .when()
                .post(ORDER_PATH)
                .then();
    }

    @Step("Creating a new order without ingredients")
    public ValidatableResponse createOrderWithoutIngredients(String accessToken) {
        return given()
                .spec(Specification.requestSpecification())
                .header("authorization", accessToken)
                .when()
                .post(ORDER_PATH)
                .then();
    }
    @Step("Getting orders by authorization user")
    public ValidatableResponse getOrders(String accessToken) {
        return given()
                .spec(Specification.requestSpecification())
                .header("Authorization", accessToken)
                .when()
                .get(ORDER_PATH)
                .then();
    }

}
