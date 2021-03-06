package com.itdr.service.impl;

import com.itdr.common.ServerResponse;
import com.itdr.config.ConstCode;
import com.itdr.config.TokenCache;
import com.itdr.mapper.UsersMapper;
import com.itdr.pojo.Users;
import com.itdr.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UsersMapper usersMapper;

    //用户登录
    @Override
    public ServerResponse login(String username, String password) {
        if (StringUtils.isEmpty (username)){
            return ServerResponse.defeatedRS (ConstCode.UserEnum.EMPTY_USERNAME.getCode (),ConstCode.UserEnum.EMPTY_USERNAME.getDesc ());
        }
        if (StringUtils.isEmpty (password)){
            return ServerResponse.defeatedRS (ConstCode.UserEnum.EMPTY_PASSWORD.getCode (),ConstCode.UserEnum.EMPTY_PASSWORD.getDesc ());
        }
        Users u = usersMapper.selectByUserNameAndPassword(username,password);
        if (u == null){
            return ServerResponse.defeatedRS (ConstCode.UserEnum.FAIL_LOGIN.getCode (),ConstCode.UserEnum.FAIL_LOGIN.getDesc ());
        }

        return ServerResponse.successRS (u);
    }

    //用户注册
    @Override
    public ServerResponse register(Users u) {
        //参数非空判断
        if (StringUtils.isEmpty (u.getUsername ())){
            return ServerResponse.defeatedRS (ConstCode.UserEnum.EMPTY_USERNAME.getCode (), ConstCode.UserEnum.EMPTY_USERNAME.getDesc ());
        }
        if (StringUtils.isEmpty (u.getPassword ())){
            return ServerResponse.defeatedRS (ConstCode.UserEnum.EMPTY_PASSWORD.getCode (),ConstCode.UserEnum.EMPTY_PASSWORD.getDesc ());
        }
        if (StringUtils.isEmpty (u.getQuestion ())){
            return ServerResponse.defeatedRS (ConstCode.UserEnum.EMPTY_QUESTION.getCode (),ConstCode.UserEnum.EMPTY_QUESTION.getDesc ());
        }
        if (StringUtils.isEmpty (u.getAnswer ())){
            return ServerResponse.defeatedRS (ConstCode.UserEnum.EMPTY_ANSWER.getCode (),ConstCode.UserEnum.EMPTY_ANSWER.getDesc ());
        }
        //查找用户名是否存在
        ServerResponse<Users> username = checkValid (u.getUsername (), "username");
//        Users users = usersMapper.selectByUserName(u.getUsername ());
//        if (users != null){
//            return ServerResponse.defeatedRS (ConstCode.UserEnum.EXIST_USER.getCode (),ConstCode.UserEnum.EXIST_USER.getDesc ());
//        }
        //查找邮箱是否存在
        ServerResponse<Users> email = checkValid (u.getEmail (), "email");
        if (!username.isSuccess () || !email.isSuccess ()){
            return ServerResponse.defeatedRS (ConstCode.UserEnum.EXIST_USEROREMAIL.getCode (),ConstCode.UserEnum.EXIST_USEROREMAIL.getDesc ());
        }

        //往数据表里创建一个新用户
        int insert = usersMapper.insert (u);
        if (insert<=0){
            return ServerResponse.defeatedRS (ConstCode.UserEnum.FAIL_REGISTER.getCode (),ConstCode.UserEnum.FAIL_REGISTER.getDesc ());
        }
        return ServerResponse.successRS (ConstCode.UserEnum.SUCCESS_USER.getCode (),ConstCode.UserEnum.SUCCESS_USER.getDesc ());
    }

    //检查用户名和邮箱是否存在
    @Override
    public ServerResponse<Users> checkValid(String str, String type) {
        //参数非空判断
        if (StringUtils.isEmpty (str)){
            return ServerResponse.defeatedRS (ConstCode.DEFAULT_FAIL, ConstCode.UserEnum.EMPTY_CHECKVAILd.getDesc ());
        }
        if (StringUtils.isEmpty (type)){
            return ServerResponse.defeatedRS (ConstCode.DEFAULT_FAIL,ConstCode.UserEnum.EMPTY_TYPE.getDesc ());
        }
        //查找用户名或者邮箱是否存在
        int i = usersMapper.selectByUserNameOrEmail(str,type);
        if (i>0){
            return ServerResponse.defeatedRS (ConstCode.UserEnum.EXIST_USEROREMAIL.getCode (),ConstCode.UserEnum.EXIST_USEROREMAIL.getDesc ());
        }
        return ServerResponse.successRS (ConstCode.DEFAULT_SUCCRSS,ConstCode.UserEnum.SUCCESS_MSG.getDesc ());
    }

    //更新信息
    @Override
    public ServerResponse<Users> updateInformation(String email, String phone, String question, String answer,Users users) {
        Users u = new Users ();
        u.setId (users.getId ());
        u.setEmail (email);
        u.setPhone (phone);
        u.setQuestion (question);
        u.setAnswer (answer);
        int i = usersMapper.updateByPrimaryKeySelective (u);
        if (i<=0) {
            return ServerResponse.defeatedRS (ConstCode.DEFAULT_FAIL,"信息更新失败");
        }
        return ServerResponse.successRS (ConstCode.UserEnum.SUCCESS_USERMSG.getDesc ());
    }

    //忘记密码
    @Override
    public ServerResponse<Users> forgetGetQuestion(String username) {
        //参数非空判断
        if (StringUtils.isEmpty (username)){
            return ServerResponse.defeatedRS (ConstCode.UserEnum.EMPTY_USERNAME.getCode (),ConstCode.UserEnum.EMPTY_USERNAME.getDesc ());
        }

        //该用户是否存在（在前端可以通过ajax方式进行判断）
        Users user = usersMapper.selectByUserName (username);
        if ( user == null){
            return ServerResponse.defeatedRS (ConstCode.UserEnum.INEXISTENCE_USER.getCode (),ConstCode.UserEnum.INEXISTENCE_USER.getDesc ());
        }

        //获取用户密保问题
        String question = user.getQuestion ();
        if (StringUtils.isEmpty (question)){
            return ServerResponse.defeatedRS (ConstCode.UserEnum.NO_QUESTION.getCode (),ConstCode.UserEnum.NO_QUESTION.getDesc ());
        }

        return ServerResponse.successRS (200,question);
    }

    //提交问题答案
    @Override
    public ServerResponse<Users> forgetCheckAnswer(String username, String question, String answer) {
        //参数非空判断
        if (StringUtils.isEmpty (username)){
            return ServerResponse.defeatedRS (ConstCode.UserEnum.EMPTY_USERNAME.getCode (),ConstCode.UserEnum.EMPTY_USERNAME.getDesc ());
        }
        if (StringUtils.isEmpty (question)){
            return ServerResponse.defeatedRS (ConstCode.UserEnum.EMPTY_QUESTION.getCode (),ConstCode.UserEnum.EMPTY_QUESTION.getDesc ());
        }
        if (StringUtils.isEmpty (answer)){
            return ServerResponse.defeatedRS (ConstCode.UserEnum.EMPTY_ANSWER.getCode (),ConstCode.UserEnum.EMPTY_ANSWER.getDesc ());
        }
        int i = usersMapper.selectUserNameAndQuestionAndPassWord(username,question,answer);
        if (i<=0){
            return ServerResponse.defeatedRS (ConstCode.UserEnum.ERROR_ANSWER.getCode (),ConstCode.UserEnum.ERROR_ANSWER.getDesc ());
        }

        //返回随机令牌
        String s = UUID.randomUUID ().toString ();
        //把令牌放入缓存中，这里使用的是Google的guava缓存，后期会使用redis替代
        TokenCache.set("token_" + username, s );
        return ServerResponse.successRS (ConstCode.DEFAULT_SUCCRSS,s);
    }

    @Override
    public ServerResponse<Users> forgetResetPassword(String username, String passwordNew, String forgetToken) {
        //参数非空判断
        if (StringUtils.isEmpty (username)){
            return ServerResponse.defeatedRS (ConstCode.UserEnum.EMPTY_USERNAME.getCode (),ConstCode.UserEnum.EMPTY_USERNAME.getDesc ());
        }
        if (StringUtils.isEmpty (passwordNew)){
            return ServerResponse.defeatedRS (ConstCode.UserEnum.EMPTY_PASSWORD.getCode (),ConstCode.UserEnum.EMPTY_PASSWORD.getDesc ());
        }
        if (StringUtils.isEmpty (forgetToken)){
            return ServerResponse.defeatedRS (ConstCode.UserEnum.EMPTY_TOKEN.getCode (),ConstCode.UserEnum.EMPTY_TOKEN.getDesc ());
        }
        String token = TokenCache.get("token_" + username);
        if (token == null || token.equals("")){
            return ServerResponse.defeatedRS(ConstCode.UserEnum.LOSE_EFFICACY.getCode(), ConstCode.UserEnum.LOSE_EFFICACY.getDesc()
        );}
        if (!token.equals(forgetToken)){
            return ServerResponse.defeatedRS(ConstCode.DEFAULT_FAIL, ConstCode.UserEnum.UNLAWFULNESS_TOKEN.getDesc());
        }
        //重置密码
        int i = usersMapper.updateByUserNameAndPasswordNew(username,passwordNew);
        if (i<=0){
            return ServerResponse.defeatedRS(ConstCode.UserEnum.DEFEACTED_PASSWORDNEW.getCode (), ConstCode.UserEnum.DEFEACTED_PASSWORDNEW.getDesc());
        }
        return ServerResponse.successRS (ConstCode.UserEnum.SUCCESS_PASSWORDNEW.getCode (), ConstCode.UserEnum.SUCCESS_PASSWORDNEW.getDesc());
    }

    @Override
    public ServerResponse<Users> resetPassword(Users users,String passwordOld, String passwordNew) {
        //参数非空判断
        if (StringUtils.isEmpty (passwordOld)){
            return ServerResponse.defeatedRS (ConstCode.UserEnum.EMPTY_PASSWORD.getCode (),ConstCode.UserEnum.EMPTY_PASSWORD.getDesc ());
        }
        if (StringUtils.isEmpty (passwordNew)){
            return ServerResponse.defeatedRS (ConstCode.UserEnum.EMPTY_PASSWORD.getCode (),ConstCode.UserEnum.EMPTY_PASSWORD.getDesc ());
        }
        int i = usersMapper.updateByUserNameAndPasswordOldAndPasswordNew (users.getUsername (),passwordOld,passwordNew);
        if (i<=0){
            return ServerResponse.defeatedRS(ConstCode.UserEnum.DEFEACTED_PASSWORDNEW.getCode (), ConstCode.UserEnum.DEFEACTED_PASSWORDNEW.getDesc());
        }
        return ServerResponse.successRS (ConstCode.UserEnum.SUCCESS_PASSWORDNEW.getCode (), ConstCode.UserEnum.SUCCESS_PASSWORDNEW.getDesc());
    }
}
