package com.bizdata.admin.controller;

import com.bizdata.admin.domain.LoginLogout;
import com.bizdata.admin.service.LoginLogoutService;
import com.bizdata.commons.constant.LoginLogoutType;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    private LoginLogoutService loginLogoutService;

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
            // 在注销之前,在缓存中清除当前用户sessionID
            clearCache(username, sessionID);
            // 执行入库操作
            loginLogoutService.log(logoutFormat(subject, request));
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

    /**
     * 封装退出操作日志对象
     *
     * @param currentUser 当前用户名
     * @param request     请求实体
     * @return LoginLogout
     */
    private LoginLogout logoutFormat(Subject currentUser, HttpServletRequest request) {
        // 用户名
        String username = currentUser.getPrincipal().toString();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 时间
        Date date = new Date();
        // 格式化时间
        String dateString = dateFormat.format(date);
        // 操作内容
        String content = username + " 于 " + dateString + " 安全退出系统 ";
        // 获取ip
        String ip = request.getRemoteAddr();
        // 封装对象
        LoginLogout sysLoginLogout = new LoginLogout();
        sysLoginLogout.setUsername(username);
        sysLoginLogout.setContent(content);
        sysLoginLogout.setDate(date);
        sysLoginLogout.setType(LoginLogoutType.LOGOUT);
        sysLoginLogout.setIp(ip);
        return sysLoginLogout;
    }
}
