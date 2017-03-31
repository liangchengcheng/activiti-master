package com.lcc.oa.shiro.credentials;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 输错5次密码锁定2分钟，ehcache.xml配置
 * Created by asus on 2017/3/31.
 */
public class RetryLimitHashedCredentialsMatcher extends HashedCredentialsMatcher{

    private Cache<String, AtomicInteger> passwordRetryCache;

    /**
     * 从manager中获取passwordRetryCache
     */
    public RetryLimitHashedCredentialsMatcher(CacheManager cacheManager) {
        passwordRetryCache = cacheManager.getCache("passwordRetryCache");
    }

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        //获取用户名
        String username = (String) token.getPrincipal();
        //retry count + 1
        AtomicInteger retryCount = passwordRetryCache.get(username);
        if (retryCount == null){
            retryCount = new AtomicInteger(0);
            passwordRetryCache.put(username,retryCount);
        }
        if (retryCount.incrementAndGet() > 5){
            throw new ExcessiveAttemptsException();
        }
        //通过就清空,没有的话就+1
        boolean matches = super.doCredentialsMatch(token,info);
        if (matches){
            passwordRetryCache.remove(username);
        }
        return matches;
    }
}
