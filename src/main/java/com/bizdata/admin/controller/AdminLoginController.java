package com.bizdata.admin.controller;

import com.bizdata.admin.controller.vo.LoginParamVO;
import com.bizdata.admin.domain.LoginLogout;
import com.bizdata.admin.service.LoginLogoutService;
import com.bizdata.admin.service.UserService;
import com.bizdata.commons.constant.LoginLogoutType;
import lombok.extern.slf4j.Slf4j;
import me.sdevil507.resp.ResultUtil;
import me.sdevil507.resp.ResultVO;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.tools.ant.taskdefs.Sleep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 登录验证Controller
 *
 * @author sdevil507
 * @version 1.0
 */
@Controller
@RequestMapping("/admin")
@Slf4j
public class AdminLoginController {

    @Autowired
    private LoginLogoutService loginLogoutService;

    @Autowired
    private UserService userService;

    /**
     * 展示登录页面
     *
     * @return ModelAndView
     */
    @RequestMapping(value = "/login")
    public ModelAndView showLoginForm() {
        return new ModelAndView("login");
    }

    /**
     * 执行ajax方式登录
     *
     * @param loginParamVO 登录表单VO
     * @return ResultVO
     * @see LoginParamVO
     */
    @RequestMapping(value = "/ajaxLogin", method = RequestMethod.POST)
    @ResponseBody
    public ResultVO ajaxLogin(@RequestBody LoginParamVO loginParamVO, HttpServletRequest request) {
        ResultVO resultVO;
        try {
            UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(loginParamVO.getUsername(), loginParamVO.getPassword(), loginParamVO.isRememberMe());
            SecurityUtils.getSubject().login(usernamePasswordToken);
            resultVO = ResultUtil.create(0, "登录成功!");
            // 更新最后登录时间
            updateLastLoginTime();
            // 执行登录日志的记录
            saveLog(request);
        } catch (Exception e) {
            String message = e.getClass().getSimpleName();
            String result = "";
            if ("UnknownAccountException".equals(message)) {
                result = "账号不存在!";
            } else if ("IncorrectCredentialsException".equals(message)) {
                result = "密码错误!";
            } else if ("LockedAccountException".equals(message)) {
                result = "账号被锁定!";
            } else if ("ExcessiveAttemptsException".equals(message)) {
                result = "输入错误次数太多,该账号被锁定10分钟!";
            } else if (StringUtils.isNotEmpty(message)) {
                result = "未知错误,请联系平台管理员!";
            }
            resultVO = ResultUtil.create(-1, result);
            log.error("登录失败:" + loginParamVO.getUsername() + " " + result + " " + LocalDateTime.now());
        }
        return resultVO;
    }

    /**
     * 更新用户最后登录时间
     */
    private void updateLastLoginTime() {
        // 获取当前用户对象
        Subject currentUser = SecurityUtils.getSubject();
        String username = currentUser.getPrincipal().toString();// 用户名
        Date date = new Date();
        userService.updateLastLoginTime(username, date);
    }

    /**
     * 记录登录日志，并且入库
     *
     * @param request 请求
     */
    private void saveLog(ServletRequest request) {
        // 获取当前用户对象
        Subject currentUser = SecurityUtils.getSubject();
        // 对象session
        Session session = currentUser.getSession();
        // 是否通过login认证通过,与isRemembered()是互斥的
        boolean authenticated = currentUser.isAuthenticated();
        // 是否通过remember认证登录
        boolean remembered = currentUser.isRemembered();
        // 如果认证通过，并且session没有值，则进行记录,作为新增用户。
        // 如果认证通过，session有值，则不记录，说明已经登录过的。
        if ((authenticated || remembered) && null == session.getAttribute("authenticated")) {
            session.setAttribute("authenticated", "true");// session记录是否认证
            String username = currentUser.getPrincipal().toString();// 用户名
            String ip = request.getRemoteAddr();// ip
            session.setAttribute("username", username);// session记录username，方便后面超时时进行日志写入
            session.setAttribute("ip", ip);// session记录ip，方便后面超时时进行日志写入
            loginLogoutService.log(loginFormat(username, ip));
        } else {
            // 不进行任何操作
        }
    }

    /**
     * 登录日志格式
     *
     * @param username
     * @param ip
     */
    private LoginLogout loginFormat(String username, String ip) {

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 时间
        Date date = new Date();
        // 格式化时间
        String dateString = dateFormat.format(date);
        // 操作内容
        String content = username + " 于 " + dateString + " 成功登录后台管理系统 ";

        // 封装对象
        LoginLogout sysLoginLogout = new LoginLogout();
        sysLoginLogout.setUsername(username);
        sysLoginLogout.setContent(content);
        sysLoginLogout.setDate(date);
        sysLoginLogout.setType(LoginLogoutType.LOGIN);
        sysLoginLogout.setIp(ip);
        return sysLoginLogout;
    }

}

