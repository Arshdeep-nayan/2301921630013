package com.logging.logging_middleware.controller;

import com.logging.logging_middleware.middleware.LoggerMiddleware;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    private final LoggerMiddleware logger;

    public TestController(LoggerMiddleware logger) {
        this.logger = logger;
    }

    @GetMapping("/test")
    public String testLogger() {

        logger.log(
                "backend",
                "info",
                "controller",
                "Testing Affordmed Logging Middleware"
        );

        return "Log Sent Successfully";

    }

}