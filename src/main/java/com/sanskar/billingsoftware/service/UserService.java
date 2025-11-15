package com.sanskar.billingsoftware.service;

import com.sanskar.billingsoftware.io.UserRequest;
import com.sanskar.billingsoftware.io.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse createUser(UserRequest request);

    String getUserRole(String email);

    List<UserResponse> readUsers();

    void deleteUser(String id);
}
