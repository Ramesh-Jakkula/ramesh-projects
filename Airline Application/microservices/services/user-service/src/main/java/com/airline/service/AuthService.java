package com.airline.service;

import com.airline.payload.dto.UserDTO;
import com.airline.payload.response.AuthResponse;

public interface AuthService {

    AuthResponse login(String email, String password) throws Exception;
    AuthResponse signup(UserDTO req) throws Exception;
}
