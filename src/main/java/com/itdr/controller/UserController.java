package com.itdr.controller;

import com.itdr.common.ServerResponse;
import com.itdr.config.ConstCode;
import com.itdr.pojo.Users;
import com.itdr.pojo.vo.UserVo;
import com.itdr.service.UserService;
import com.itdr.utils.ObjectToVoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@ResponseBody
@RequestMapping("/portal/user/")
public class UserController {

    @Autowired
    UserService userService;

    /**
     * 用户登录
     * @param username
     * @param password
     * @param session
     * @return
     */
    @RequestMapping("login.do")
    public ServerResponse<Users> login(String username, String password, HttpSession session){

        ServerResponse sr = userService.login(username,password);
        //把用户数据保存在session中
        if (sr.isSuccess ()) {
            session.setAttribute ("user", sr.getData ());
        }

        return sr;
    }

    /**
     * 用户注册
     * @param u
     * @return
     */
    @RequestMapping("register.do")
    public ServerResponse<Users> register(Users u){
        ServerResponse sr = userService.register(u);
        System.out.println (u.getUsername ());
        return sr;
    }

    /**
     * 检查用户名和邮箱是否存在
     * @param str
     * @param type
     * @return
     */
    @RequestMapping("check_valid.do")
    public ServerResponse<Users> checkValid(String str, String type){
        return userService.checkValid(str,type);
    }

    /**
     * 获取用户登录数据
     * @param session
     * @return
     */
    @RequestMapping("get_user_info.do")
    public ServerResponse<Users> getUserInfo(HttpSession session){
        Users users = (Users) session.getAttribute ("user");
        //判断用户是否登录
        if (users == null){
            return ServerResponse.defeatedRS (ConstCode.UserEnum.FORCE_EXIT.getCode (),ConstCode.UserEnum.FORCE_EXIT.getDesc ());
        }
        UserVo userVo = ObjectToVoUtil.UserToUserVo (users);
        return ServerResponse.successRS (userVo);
    }

    /**
     * 获取用户详细数据
     * @param session
     * @return
     */
    @RequestMapping("get_inforamtion.do")
    public ServerResponse<Users> getInforamtion(HttpSession session){
        Users users = (Users) session.getAttribute ("user");
        //判断用户是否登录
        if (users == null){
            return ServerResponse.defeatedRS (ConstCode.UserEnum.FORCE_EXIT.getCode (),ConstCode.UserEnum.FORCE_EXIT.getDesc ());
        }
        return ServerResponse.successRS (users);
    }

    /**
     * 登陆状态下更新个人信息
     * @param email
     * @param phone
     * @param question
     * @param answer
     * @param session
     * @return
     */
    @RequestMapping("update_information.do")
    public ServerResponse<Users> updateInformation(String email, String phone, String question, String answer, HttpSession session){
        Users users = (Users) session.getAttribute ("user");
        //判断用户是否登录
        if (users == null){
            return ServerResponse.defeatedRS (ConstCode.UserEnum.FORCE_EXIT.getCode (),ConstCode.UserEnum.FORCE_EXIT.getDesc ());
        }
        return userService.updateInformation (email,phone,question,answer,users);
    }

    /**
     * 退出登录
     * @param session
     * @return
     */
    @RequestMapping("logout.do")
    public ServerResponse<Users> logout(HttpSession session){
        Users users = (Users) session.getAttribute ("user");
        //判断用户是否登录
        if (users == null){
            return ServerResponse.defeatedRS (ConstCode.UserEnum.FORCE_EXIT.getCode (),ConstCode.UserEnum.FORCE_EXIT.getDesc ());
        }
        session.removeAttribute ("user");
        return ServerResponse.successRS (ConstCode.UserEnum.LOGOUT.getCode (),ConstCode.UserEnum.LOGOUT.getDesc ());
    }

    /**
     * 忘记密码（输入用户名跳转到问题页面）
     * @param username
     * @return
     */
    @RequestMapping("forget_get_question.do")
    public ServerResponse<Users> forgetGetQuestion(String username){

        return userService.forgetGetQuestion(username);
    }

    /**
     * 提交问题答案
     * @param username
     * @param question
     * @param answer
     * @return
     */
    @RequestMapping("forget_check_answer.do")
    public ServerResponse<Users> forgetCheckAnswer(String username,String question, String answer){
        return userService.forgetCheckAnswer(username,question,answer);
    }

    /**
     * 重设密码
     * @param username
     * @param passwordNew
     * @param forgetToken
     * @param session
     * @return
     */
    @RequestMapping("forget_reset_password.do")
    public ServerResponse<Users> forgetResetPassword(String username,String passwordNew, String forgetToken,HttpSession session){
        ServerResponse<Users> usersServerResponse = userService.forgetResetPassword (username, passwordNew, forgetToken);
        if (usersServerResponse.isSuccess ()){
            session.removeAttribute ("user");
        }
        return usersServerResponse;
    }

    /**
     * 登录状态下重设密码
     * @param passwordOld
     * @param passwordNew
     * @param session
     * @return
     */
    @RequestMapping("reset_password.do")
    public ServerResponse<Users> resetPassword(String passwordOld,String passwordNew,HttpSession session){
        Users users = (Users) session.getAttribute ("user");
        //判断用户是否登录
        if (users == null){
            return ServerResponse.defeatedRS (ConstCode.UserEnum.FORCE_EXIT.getCode (),ConstCode.UserEnum.FORCE_EXIT.getDesc ());
        }
        session.removeAttribute ("user");
        return userService.resetPassword(users,passwordOld,passwordNew);
    }




}
