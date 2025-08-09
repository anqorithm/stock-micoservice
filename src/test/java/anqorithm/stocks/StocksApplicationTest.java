package anqorithm.stocks;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "server.port=0"
})
class StocksApplicationTest {

    @Test
    void contextLoads() {
        // This test verifies that the Spring Boot application context loads successfully
        // If the context fails to load, this test will fail
    }

    @Test
    void mainMethod() {
        // Test that the main method can be called without throwing exceptions
        // We don't actually want to start the application, just verify the method exists
        String[] args = {};
        
        // This would normally start the application, but we'll just verify the class structure
        // StocksApplication.main(args); // Commented out to avoid starting actual application in test
        
        // Instead, we'll just verify the class and method exist
        try {
            Class<?> clazz = StocksApplication.class;
            clazz.getMethod("main", String[].class);
        } catch (NoSuchMethodException e) {
            throw new AssertionError("Main method should exist", e);
        }
    }
}