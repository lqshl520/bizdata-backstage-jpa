
package com.bizdata.framework.filter;

import com.bizdata.framework.shiro.utils.UserNameSessionIDsMapOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.DefaultSessionKey;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.Serializable;
import java.util.Deque;

/**
 * 同一账号同时使用人数控制与处理
 *
 * @author sdevil507
 * @version 1.0
 */
@Slf4j
public class KickoutSessionControlFilter extends AccessControlFilter {

    @Autowired
    private UserNameSessionIDsMapOperation userNameSessionIDsMapOperation;

    /**
     * 踢出后到的地址
     */
    private String kickoutUrl;
    /**
     * true:踢出之后登录的用户,false:踢出之前登录的用户
     */
    private boolean kickoutAfter = false;

    /**
     * 同一个帐号同时登录人数,默认1
     */
    private int maxSession = 1;

    private SessionManager sessionManager;

    public void setKickoutUrl(String kickoutUrl) {
        this.kickoutUrl = kickoutUrl;
    }

    public void setKickoutAfter(boolean kickoutAfter) {
        this.kickoutAfter = kickoutAfter;
    }

    public void setMaxSession(int maxSession) {
        this.maxSession = maxSession;
    }

    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue)
            throws Exception {
        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        Subject subject = getSubject(request, response);
        if (!subject.isAuthenticated() && !subject.isRemembered()) {
            // 如果没有登录，直接进行之后的流程
            return true;
        }

        Session session = subject.getSession();
        String username = subject.getPrincipal().toString();
        String sessionId = session.getId().toString();

        // 获取session列表队列
        Deque<Serializable> deque = userNameSessionIDsMapOperation.get(username);

        // 如果队列里没有此sessionId，且用户没有被踢出；放入队列
        if (!deque.contains(sessionId) && session.getAttribute("kickout") == null) {
            userNameSessionIDsMapOperation.add(deque, sessionId);
        }

        // 如果队列里的sessionId数超出最大会话数，开始踢人
        while (deque.size() > maxSession) {
            Serializable kickoutSessionId;
            if (kickoutAfter) { // 如果踢出后者
                kickoutSessionId = deque.removeFirst();
            } else { // 否则踢出前者
                kickoutSessionId = deque.removeLast();
            }
            try {
                Session kickoutSession = sessionManager.getSession(new DefaultSessionKey(kickoutSessionId));
                if (kickoutSession != null) {
                    // 设置会话的kickout属性表示踢出了
                    kickoutSession.setAttribute("kickout", true);
                }
            } catch (Exception e) {
                log.error("控制登录人数异常!", e);
            }
        }

        // 如果被踢出了，直接退出，重定向到踢出后的地址
        if (session.getAttribute("kickout") != null) {
            // 会话被踢出了
            try {
                userNameSessionIDsMapOperation.remove(deque, sessionId);
                userNameSessionIDsMapOperation.cacheNotify(username, deque);
                subject.logout();
            } catch (Exception e) { // ignore
            }
            saveRequest(request);
            WebUtils.issueRedirect(request, response, kickoutUrl);
            return false;
        }
        return true;
    }
}
