package com.VsmartEngine.Chatbot;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
	
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000", "http://127.0.0.1:5500")
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
    
}
