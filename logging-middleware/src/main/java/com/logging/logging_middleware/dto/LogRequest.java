package com.logging.logging_middleware.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogRequest {
    private String stack;
    private String level;
    private String packageName;
    private String message;

}
