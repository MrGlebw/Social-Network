package com.springwebapplication.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

@SpringBootTest(classes = SpringWebApplicationTests.class)
class SpringWebApplicationTests {


	@Test
	void simpleFluxExample() {
		Flux<String> fluxColors = Flux.just("red", "green", "blue");
		fluxColors.subscribe(System.out::println);
	}

}
