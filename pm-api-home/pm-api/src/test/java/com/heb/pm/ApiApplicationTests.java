package com.heb.pm;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Basic test that the application can start up in a Spring context.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestConfiguration.class})
@WebAppConfiguration
public class ApiApplicationTests {

	@Test
	public void contextLoads() {
	}

}
