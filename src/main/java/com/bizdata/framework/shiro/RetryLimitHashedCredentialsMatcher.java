package com.bizdata.framework.shiro;

import com.bizdata.framework.shiro.config.ShiroConfigProperties;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 继承自HashedCredentialsMatcher，用于凭证验证 此处重写doCredentialsMatch，加入了密码输入次数验证功能
 * <p>
 * Created by sdevil507 on 2017/8/17.
 */
public class RetryLimitHashedCredentialsMatcher extends HashedCredentialsMatcher {

    @Autowired
    private ShiroConfigProperties shiroConfigProperties;

    /**
     * @Fields passwordRetryCache : 密码重试缓存
     */
    private Cache<String, AtomicInteger> passwordRetryCache;

    public RetryLimitHashedCredentialsMatcher(CacheManager cacheManager) {
        passwordRetryCache = cacheManager.getCache("passwordRetryCache");
    }

    /**
     * 重写凭证验证方法,加入密码输入错误次数统计
     *
     * @param token 令牌
     * @param info  信息
     * @return boolean
     */
    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        String username = (String) token.getPrincipal();
        // retry count + 1
        AtomicInteger retryCount = passwordRetryCache.get(username);
        //计数开始
        if (null == retryCount) {
            retryCount = new AtomicInteger(0);
            passwordRetryCache.put(username, retryCount);
        }
        //如果输入次数大于5次,则抛出异常
        if (retryCount.incrementAndGet() > shiroConfigProperties.getPassword().getRetryCount()) {
            // if retry count > 5 throw
            throw new ExcessiveAttemptsException();
        }

        // 调用原先的凭证验证方法，也就是盐值hash验证
        boolean matches = super.doCredentialsMatch(token, info);
        if (matches) {
            // clear retry count
            passwordRetryCache.remove(username);
        }
        return matches;
    }
}
