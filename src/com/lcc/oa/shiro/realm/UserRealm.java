package com.lcc.oa.shiro.realm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.lcc.oa.entity.GroupAndResource;
import com.lcc.oa.entity.Resource;
import com.lcc.oa.entity.User;
import com.lcc.oa.service.IGroupAndResourceService;
import com.lcc.oa.service.IResourceService;
import com.lcc.oa.service.IUserService;
import com.lcc.oa.util.BeanUtils;
import com.lcc.oa.util.Constants;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * Shiro从从Realm获取安全数据 （如用户、 角色、 权限）
 * 可以把UserRealm看为安全数据源
 * Created by asus on 2017/4/1.
 */
public class UserRealm extends AuthorizingRealm {
    private static final Logger logger = Logger.getLogger(UserRealm.class);

    @Autowired
    private IUserService userService;

    @Autowired
    private IGroupAndResourceService grService;

    @Autowired
    private IResourceService resourceService;

    /**
     * 权限的授权管理
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String username = (String) principals.getPrimaryPrincipal();
        // AUTHORIZATION 授权,就是权限验证,验证某个已经验证的用户是否有某个权限
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        try{
            // 通过名字获取用户
            User user = this.userService.getUserByName(username);
            Set<String> roles = new HashSet<>();
            // 本系统设计为一个用户属于一个用户组，
            // 即用户组就是用户的角色（employee、finance、hr、boss..）；
            // 每个用户组有不同的权限（资源）
            // 其他系统中可以设置 一个用户有多个角色，一个角色有多个权限
            // 在本系统中 除了管理员是admin其他组都用user标识，除了老板，其他用户组的操作都和员工组一样的。
            roles.add("admin".equals(user.getGroup().getType())?"admin":"user");

            List<GroupAndResource> grList = this.grService.getResource(user.getGroup().getId());
            Set<String> resources = new HashSet<String>();
            for(GroupAndResource gr : grList){
                Resource resource = this.resourceService.getPermissions(gr.getResourceId());
                if(!BeanUtils.isBlank(resource)){
                    resources.add(resource.getPermission());
                }
            }
            authorizationInfo.setRoles(roles);
            authorizationInfo.setStringPermissions(resources);
        }catch (Exception e){
            e.printStackTrace();
            logger.error("realm 错误！");
        }
        return authorizationInfo;
    }

    /**
     * 认证服务
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String username = (String) token.getPrincipal();
        User user = null;
        try{
            user = this.userService.getUserByName(username);
        }catch (Exception e){
            e.printStackTrace();
        }

        if (user == null){
            //没有找到账号
            throw  new UnknownAccountException();
        }

        if (Boolean.TRUE.equals(user.getLocked())){
            //帐号锁定
            throw new LockedAccountException();
        }

        Session currentSession = SecurityUtils.getSubject().getSession();
        //Authenticator的职责是验证用户帐号，是Shiro API中身份验证核心的入口点
        //交给AuthenticatingRealm使用CredentialsMatcher进行密码匹配

        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                user.getName(), //用户名
                user.getPasswd(), //密码
                ByteSource.Util.bytes(user.getCredentialsSalt()),//salt=username+salt
                getName()  //realm name
        );
        //把user设置到当前的session
        currentSession.setAttribute(Constants.CURRENT_USER, user);
        return authenticationInfo;
    }

    //系统登出后 会自动清理授权和认证缓存
    @Override
    public void clearCachedAuthorizationInfo(PrincipalCollection principals) {
        super.clearCachedAuthorizationInfo(principals);
    }

    @Override
    public void clearCachedAuthenticationInfo(PrincipalCollection principals) {
        super.clearCachedAuthenticationInfo(principals);
    }

    @Override
    public void clearCache(PrincipalCollection principals) {
        super.clearCache(principals);
    }

    public void clearAllCachedAuthorizationInfo() {
        getAuthorizationCache().clear();
    }

    public void clearAllCachedAuthenticationInfo() {
        getAuthenticationCache().clear();
    }

    public void clearAllCache() {
        clearAllCachedAuthenticationInfo();
        clearAllCachedAuthorizationInfo();
    }
}
