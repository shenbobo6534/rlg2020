package com.itdr.service;

import com.itdr.common.ServerResponse;
import com.itdr.pojo.Users;

public interface UserService {
    ServerResponse login(String username, String password);

    ServerResponse register(Users u);

    ServerResponse<Users> checkValid(String str, String type);

    ServerResponse<Users> updateInformation(String email, String phone, String question, String answer,Users users);

    ServerResponse<Users> forgetGetQuestion(String username);

    ServerResponse<Users> forgetCheckAnswer(String username, String question, String answer);

    ServerResponse<Users> forgetResetPassword(String username, String passwordNew, String forgetToken);

    ServerResponse<Users> resetPassword(Users users,String passwordOld, String passwordNew);

}
