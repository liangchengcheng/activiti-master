package com.lcc.oa.shiro.filter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;

/**
 * Created by asus on 2017/3/31.
 */
public class CustomFormAuthenticationFilter extends FormAuthenticationFilter{

    //当验证码验证失败时不再走身份认证拦截器
    @Override
    protected boolean onAccessDenied(ServletRequest request,
                                     ServletResponse response, Object mappedValue) throws Exception {
        if(request.getAttribute(getFailureKeyAttribute()) != null) {
            return true;
        }
        return super.onAccessDenied(request, response, mappedValue);
    }
}
