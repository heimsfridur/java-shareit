package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = ShareItServer.class)
//        properties = {"spring.datasource.driverClassName=org.h2.Driver",
//                "spring.datasource.url=jdbc:h2:mem:shareit",
//                "spring.datasource.username=dbuser",
//                "spring.datasource.password=12345"},
//        webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ShareItServerTests {

    @Test
    void contextLoads() {
    }

}
