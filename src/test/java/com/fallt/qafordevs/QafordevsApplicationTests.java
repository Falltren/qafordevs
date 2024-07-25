package com.fallt.qafordevs;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class QafordevsApplicationTests {

	@Test
	void contextLoads() {
	}

}
