package com.bizdata.framework.shiro.utils;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.Deque;
import java.util.LinkedList;

/**
 * 账号对应登录该账号的sessionID列表映射操作实现
 * <p>
 * Created by sdevil507 on 2017/8/18.
 */
@Component
public class UserNameSessionIDsMapOperationImpl implements UserNameSessionIDsMapOperation {

    private final static String cacheName = "shiro-currentUserSessionsMapCache";

    @Autowired
    private CacheManager cacheManager;

    private Cache<String, Deque<Serializable>> cache;

    @PostConstruct
    public void init() throws Exception {
        this.cache = cacheManager.getCache(cacheName);
    }

    @Override
    public Deque<Serializable> get(String username) {
        Deque<Serializable> deque = cache.get(username);
        if (deque == null) {
            deque = new LinkedList<>();
            cache.put(username, deque);
        }
        return deque;
    }

    @Override
    public void remove(Deque<Serializable> deque, String sessionID) {
        deque.remove(sessionID);
    }

    @Override
    public void add(Deque<Serializable> deque, String sessionID) {
        deque.push(sessionID);
    }

    public void cacheNotify(String username, Deque<Serializable> deque) {
        LinkedList<Serializable> result_deque = new LinkedList<>();
        result_deque.addAll(deque);
        cache.put(username, result_deque);
    }

}
