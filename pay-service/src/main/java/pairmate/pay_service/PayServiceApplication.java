package pairmate.pay_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication(scanBasePackages = {"pairmate.pay_service", "pairmate.common_libs"})
@EnableFeignClients(basePackages = "pairmate.pay_service")
@EnableJpaAuditing
public class PayServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PayServiceApplication.class, args);
	}

}
