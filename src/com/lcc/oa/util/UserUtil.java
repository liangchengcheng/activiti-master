package com.lcc.oa.util;

import com.lcc.oa.entity.User;

import javax.servlet.http.HttpSession;

/**
 * Created by asus on 2017/3/26.
 */
public class UserUtil {

    /**
     * 设置用户到session
     */
    public static void saveUserToSession(HttpSession session, User user) {
        session.setAttribute(Constants.CURRENT_USER, user);
    }

    /**
     * 从Session获取当前用户信息
     */
    public static User getUserFromSession(HttpSession session) {
        Object attribute = session.getAttribute(Constants.CURRENT_USER);
        return attribute == null ? null : (User) attribute;
    }

    /**
     * 从Session移除当前用户信息
     */
    public static void removeUserFromSession(HttpSession session) {
        session.removeAttribute(Constants.CURRENT_USER);
    }
}
