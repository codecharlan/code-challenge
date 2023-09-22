package com.codecharlan.codechallenge.dtos.response;

public record ApiResponse<T>(String msg, T data, boolean error) {
}
