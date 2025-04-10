package com.odp.walled.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class APIResponse<T> {
    private String status;
    private String message;
    private T data;
}
