package com.bizdata.framework.shiro;

import com.bizdata.framework.shiro.utils.UserNameSessionIDsMapOperation;
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
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import com.bizdata.admin.domain.User;
import com.bizdata.admin.service.UserService;

import java.io.Serializable;
import java.util.Deque;

/**
 * 用户认证授权相关，类似dataSource，负责用户账户验证，获取所持有的角色<br>
 * 继承AuthorizingRealm（授权），其继承了AuthenticatingRealm即身份验证），<br>
 * 而且也间接继承了CachingRealm（带有缓存实现）
 *
 * @author sdevil507
 * @version 1.0
 */
public class UserRealm extends AuthorizingRealm {

    @Autowired
    private UserService userService;

    @Autowired
    private UserNameSessionIDsMapOperation userNameSessionIDsMapOperation;

    /**
     * 返回对应用户所拥有的授权信息
     *
     * @param principals 账号信息集合
     * @return 授权信息
     * @see SimpleAuthorizationInfo
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        // 获取用户名
        String username = (String) principals.getPrimaryPrincipal();
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        authorizationInfo.setRoles(userService.findRoles(username));
        authorizationInfo.setStringPermissions(userService.findPermissions(username));
        return authorizationInfo;
    }

    /**
     * 根据用户名，执行相关的验证操作
     *
     * @param token 验证token
     * @return 身份验证信息
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        String username = (String) token.getPrincipal();

        User user = null;

        try {
            user = userService.selectUserDetailByUsername(username);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 用户名不存在
        if (user == null) {
            throw new UnknownAccountException();
        }

        // 账号被锁定(true:锁定,false:未锁定)
        if (user.isLocked()) {
            throw new LockedAccountException();
        }

        // 交给AuthenticatingRealm使用CredentialsMatcher进行密码匹配
        String salt = user.getSalt();
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(user.getUsername(), // 用户名
                user.getPassword(), // 密码
                ByteSource.Util.bytes(salt), // 此处为盐值，salt=username+salt
                getName() // realm name
        );

        // 验证成功,设置用户名与session_id的映射
        Deque<Serializable> deque = userNameSessionIDsMapOperation.get(user.getUsername());
        userNameSessionIDsMapOperation.add(deque, SecurityUtils.getSubject().getSession().getId().toString());
        userNameSessionIDsMapOperation.cacheNotify(user.getUsername(), deque);
        // 如果身份认证验证成功，返回一个AuthenticationInfo实现；
        return authenticationInfo;
    }

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
