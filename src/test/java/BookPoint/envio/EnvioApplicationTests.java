package BookPoint.envio;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EnvioApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void mainClassExist(){
		assertNotNull(EnvioApplication.class);
	}
}
