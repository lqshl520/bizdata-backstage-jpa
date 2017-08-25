package com.bizdata.framework.listener;

import com.bizdata.commons.constant.LoginLogoutType;
import com.bizdata.commons.utils.LogInOrOutManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 用户session监听，拓展自SessionListenerAdapter,用于超时机制记录
 *
 * @author sdevil507
 * @version 1.0
 */
public class UserSessionListener extends SessionListenerAdapter {

    @Autowired
    private LogInOrOutManager logInOrOutManager;

    /**
     * 当session超时，进行处理,写入用户为超时退出
     *
     * @param session 会话
     */
    @Override
    public void onExpiration(Session session) {
        if (null != session.getAttribute("username") && null != session.getAttribute("ip")) {
            String username = session.getAttribute("username").toString();
            String ip = session.getAttribute("ip").toString();
            logInOrOutManager.log(LoginLogoutType.TIME_OUT, username, ip);
        }
    }
}
