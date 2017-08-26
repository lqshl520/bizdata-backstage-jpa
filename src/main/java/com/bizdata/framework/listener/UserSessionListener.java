package com.bizdata.framework.listener;

import com.bizdata.commons.constant.LoginLogoutType;
import com.bizdata.commons.utils.LogInOrOutManager;
import com.bizdata.framework.shiro.utils.UserNameSessionIDsMapOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListenerAdapter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.security.auth.Subject;
import java.io.Serializable;
import java.util.Deque;

/**
 * 用户session监听，拓展自SessionListenerAdapter,用于超时机制记录
 *
 * @author sdevil507
 * @version 1.0
 */
public class UserSessionListener extends SessionListenerAdapter {

    @Autowired
    private UserNameSessionIDsMapOperation userNameSessionIDsMapOperation;

    @Autowired
    private LogInOrOutManager logInOrOutManager;

    /**
     * 当session超时，进行处理,写入用户为超时退出
     *
     * @param session 会话
     */
    @Override
    public void onExpiration(Session session) {
        String username = (String) session.getAttribute("username");
        if (null != username) {
            Deque<Serializable> deque = userNameSessionIDsMapOperation.get(username);
            userNameSessionIDsMapOperation.remove(deque, session.getId().toString());
            userNameSessionIDsMapOperation.cacheNotify(username, deque);
        }
    }
}