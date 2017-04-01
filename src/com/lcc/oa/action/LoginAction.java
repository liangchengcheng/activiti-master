package com.lcc.oa.action;

import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by asus on 2017/4/1.
 */
@Controller
@RequestMapping("/loginAction")
public class LoginAction {

    @RequestMapping(value = "/login")
    public String showLoginForm(HttpServletRequest request, Model model) {
        String exceptionClassName = (String) request.getAttribute("shiroLoginFailure");
        String error = null;
        if (UnknownAccountException.class.getName().equals(exceptionClassName)) {
            error = "用户不存在";
        } else if (IncorrectCredentialsException.class.getName().equals(exceptionClassName)) {
            error = "密码错误！";
        } else if (ExcessiveAttemptsException.class.getName().equals(exceptionClassName)) {
            error = "登录失败次数过多，请稍后再试！";
        } else if ("jCaptcha.error".equals(exceptionClassName)) {
            error = "验证码错误！";
        } else if (exceptionClassName != null) {
            error = "其他错误：" + exceptionClassName;
        }

        model.addAttribute("msg", error);
        if (request.getParameter("kickout") != null) {
            model.addAttribute("msg","您的账号在另一个地点登录,您已被踢出!");
        }
        if (request.getParameter("forceLogout")!=null){
            model.addAttribute("msg","您已经被管理员强制退出,请重新登录");
        }
        return "login";
    }
}
