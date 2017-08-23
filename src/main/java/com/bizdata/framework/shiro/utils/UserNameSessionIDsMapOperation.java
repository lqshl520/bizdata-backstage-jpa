package com.bizdata.framework.shiro.utils;

import java.io.Serializable;
import java.util.Deque;

/**
 * 账号对应登录该账号的sessionID列表映射操作
 * <p>
 * Created by sdevil507 on 2017/8/18.
 */
public interface UserNameSessionIDsMapOperation {

    /**
     * 获取username对应sessionID列表,如果不存在则新建
     *
     * @param username 用户名
     * @return sessionID列表
     */
    Deque<Serializable> get(String username);

    /**
     * 在username对应session列表中移除该sessionID
     *
     * @param deque     sessionID会话列表
     * @param sessionID 会话ID
     */
    void remove(Deque<Serializable> deque, String sessionID);

    /**
     * 在username对应session列表中新增sessionID
     *
     * @param deque     sessionID会话列表
     * @param sessionID 会话ID
     */
    void add(Deque<Serializable> deque, String sessionID);

    /**
     * 通知集群缓存更新
     * <p></p>
     * 在ehcache中需要改变对象才能通知其余集群更新
     *
     * @param username 用户名
     * @param deque    队列
     */
    void cacheNotify(String username, Deque<Serializable> deque);
}
