package com.turbonomic.demo.spring;

import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
public class Application {

    private final Object serverLock = new Object();

    private Server grpcServer;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            System.out.println("Let's inspect the beans provided by Spring Boot:");

            final String[] beanNames = ctx.getBeanDefinitionNames();
            Arrays.sort(beanNames);
            for (final String beanName : beanNames) {
                System.out.println(beanName);
            }

            synchronized (serverLock) {
                System.out.println("Starting gRPC server...");
                grpcServer = ServerBuilder.forPort(9001)
                        .addService(echoRpcService())
                        .build();
                System.out.println("Started gRPC server...");
            }
        };
    }

    @Bean
    public EchoRpcService echoRpcService() {
        return new EchoRpcService();
    }

    @Bean
    public Docket appApi() {
        return new Docket(DocumentationType.SWAGGER_2)
            .select()
                .apis(RequestHandlerSelectors.basePackage("com.turbonomic.demo.spring"))
                .paths(PathSelectors.any())
                .build()
            .pathMapping("/");
    }
}
