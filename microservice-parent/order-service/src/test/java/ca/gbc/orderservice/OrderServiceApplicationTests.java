package ca.gbc.orderservice;

import ca.gbc.orderservice.stub.InventoryClientStub;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@AutoConfigureWireMock(port = 0)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class OrderServiceApplicationTests {

	@LocalServerPort
	private int port;

	@Autowired
	private MockMvc mockMvc;

	static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest")
			.withDatabaseName("testdb")
			.withUsername("testuser")
			.withPassword("testpass");

	static {
		postgreSQLContainer.start();
	}

	@BeforeEach
	void setUp() {
		RestAssured.port = port;
	}

	@Test
	void shouldSubmitOrderTest() throws Exception {
		String submitOrderJson = """
                {
                    "skuCode": "SKU100",
                    "price": 150.00,
                    "quantity": 3
                }
                """;

		InventoryClientStub.stubInventoryCall("SKU100", 3);

		mockMvc.perform(post("/api/order")
						.contentType(MediaType.APPLICATION_JSON)
						.content(submitOrderJson))
				.andExpect(status().isCreated())
				.andExpect(content().string("Order Placed Successfully"));
	}
}
