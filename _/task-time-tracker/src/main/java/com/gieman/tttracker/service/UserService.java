package com.gieman.tttracker.service;

import java.util.List;
import com.gieman.tttracker.domain.User;
import com.gieman.tttracker.vo.Result;

public interface UserService {

    public Result<User> store(
        String username,
        String firstName,
        String lastName,
        String email,
        String password,
        Character adminRole,
        String actionUsername);

    public Result<User> remove(String username, String actionUsername);
    public Result<User> find(String username, String actionUsername);
    public Result<List<User>> findAll(String actionUsername);
    public Result<User> findByUsernamePassword(String username, String password);

}