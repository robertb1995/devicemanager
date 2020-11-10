package com.bialas.robert.devicemanager.contoller;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successful operation"),
        @ApiResponse(code = 204, message = "No Content"),
        @ApiResponse(code = 400, message = "Invalid request."),
        @ApiResponse(code = 404, message = "Not found"),
        @ApiResponse(code = 405, message = "Method not allowed"),
        @ApiResponse(code = 409, message = "Conflict."),
        @ApiResponse(code = 500, message = "Server Error")
})
@interface ResponseContract {
}
