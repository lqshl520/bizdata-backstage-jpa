package com.bizdata.framework.listener;

import com.bizdata.framework.shiro.utils.UserNameSessionIDsMapOperation;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;

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

    /**
     * 当session超时，进行处理,写入用户为超时退出
     *
     * @param session 会话
     */
    @Override
    public void onExpiration(Session session) {
        System.out.println("session过期了");
        String username = (String) session.getAttribute("username");
        if (null != username) {
            Deque<Serializable> deque = userNameSessionIDsMapOperation.get(username);
            userNameSessionIDsMapOperation.remove(deque, session.getId().toString());
            userNameSessionIDsMapOperation.cacheNotify(username, deque);
        }
    }
}
