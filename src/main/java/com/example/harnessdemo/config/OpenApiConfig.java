package com.example.harnessdemo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI harnessDemoOpenApi() {
    return new OpenAPI()
        .info(new Info()
            .title("harness-demo API")
            .description("Claude Code Java harness 演示项目的 REST API")
            .version("0.1.0")
            .contact(new Contact().name("xingyun0812")));
  }
}
