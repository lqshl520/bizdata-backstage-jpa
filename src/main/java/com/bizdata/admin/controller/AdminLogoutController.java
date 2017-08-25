package com.bizdata.admin.controller;

import com.bizdata.commons.constant.LoginLogoutType;
import com.bizdata.commons.utils.LogInOrOutManager;
import com.bizdata.framework.shiro.utils.UserNameSessionIDsMapOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.Deque;

/**
 * 安全退出操作
 *
 * @author sdevil507
 * @version 1.0
 */
@Controller
@RequestMapping("/admin")
public class AdminLogoutController {

    @Autowired
    private LogInOrOutManager logInOrOutManager;

    @Autowired
    private UserNameSessionIDsMapOperation userNameSessionIDsMapOperation;

    /**
     * 执行安全退出操作
     *
     * @param request  请求实体
     * @param response 返回实体
     * @throws IOException
     */
    @RequestMapping(value = "/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Subject subject = SecurityUtils.getSubject();
        if (null != subject.getPrincipal()) {
            // 获取注销前sessionId
            String sessionID = (String) subject.getSession().getId();
            // 获取注销前用户名
            String username = subject.getPrincipal().toString();
            // 获取请求ip
            String ip = request.getRemoteAddr();
            // 在注销之前,在缓存中清除当前用户sessionID
            clearCache(username, sessionID);
            // 执行入库操作
            logInOrOutManager.log(LoginLogoutType.LOGOUT, username, ip);
            if (subject.isAuthenticated() || subject.isRemembered()) {
                subject.logout();// session会销毁，在SessionListener监听session销毁，清理权限缓存
            }
        }
        // 执行跳转到登陆页
        response.sendRedirect(request.getContextPath() + "/admin/login");
    }

    /**
     * 退出时清除该用户在映射中的sessionID
     *
     * @param username  用户名
     * @param sessionID 会话ID
     */
    private void clearCache(String username, String sessionID) {
        Deque<Serializable> deque = userNameSessionIDsMapOperation.get(username);
        userNameSessionIDsMapOperation.remove(deque, sessionID);
        userNameSessionIDsMapOperation.cacheNotify(username, deque);
    }
}
