package com.lcc.oa.interceptor;

import com.lcc.oa.entity.User;
import com.lcc.oa.util.BeanUtils;
import com.lcc.oa.util.UserUtil;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by asus on 2017/3/26.
 */
public class LoginInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        User user = UserUtil.getUserFromSession(session);
        if(!BeanUtils.isBlank(user)){
            return true;
        }
        response.sendRedirect(request.getContextPath() + "/userAction/login_view");
        return false;
    }
}
