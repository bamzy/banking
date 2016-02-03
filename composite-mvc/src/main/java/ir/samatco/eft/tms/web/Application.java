package ir.samatco.eft.tms.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author Meysam Zarezadeh
 */
@ImportResource({"classpath:META-INF/spring/applicationContextRepository.xml","classpath:META-INF/spring/applicationContext.xml"})
@SpringBootApplication
@EnableTransactionManagement
public class Application{
	public static void main(String[] args){
		SpringApplication.run(Application.class, args);
	}
}
