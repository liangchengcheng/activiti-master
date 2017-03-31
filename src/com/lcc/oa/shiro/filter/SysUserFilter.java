package com.lcc.oa.shiro.filter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.lcc.oa.entity.User;
import com.lcc.oa.service.IUserService;
import com.lcc.oa.util.Constants;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.web.filter.PathMatchingFilter;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 每次把用户信息放入request中，暂时没用。
 */
public class SysUserFilter extends PathMatchingFilter {

    @Autowired
    private IUserService userService;

    @Override
    protected boolean onPreHandle(ServletRequest request, ServletResponse response,
                                  Object mappedValue) throws Exception {
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        User user = this.userService.getUserByName(username);
        request.setAttribute(Constants.CURRENT_USER, user);
        return true;
    }
}