package com.trackNest.trackNest_App;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TrackNestAppApplication {



	public static void main(String[] args) {
		SpringApplication.run(TrackNestAppApplication.class, args);
//		ApplicationContext context = SpringApplication.run(TrackNestAppApplication.class, args);
//		UserService userService = context.getBean(UserService.class);
//		UserEntity user = new UserEntity(); // only @Service, @Component, @Repository, @COntroller, @Bean can get from context
//		user.setFullName("Shivam Anand");
//		user.setEmail("a4anand0123@gmail.com");
//		user.setPhone("1234444444");
//		user.setIsActive(true);
//		user.setPasswordHash("#45jjfjdljlfl");
//		userService.saveUser(user);
	}



}
