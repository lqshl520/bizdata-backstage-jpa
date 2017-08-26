package com.bizdata.framework.shiro;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;

/**
 * 使用Redis实现的sessionDao
 * <p>
 * Created by sdevil507 on 2017/8/26.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RedisCacheSessionDao extends EnterpriseCacheSessionDAO {

    private RedisTemplate<Serializable, Session> redisTemplate;

    /**
     * 创建session,并保存[sessionId:session]结构至redis
     *
     * @param session 会话
     * @return 会话ID
     */
    @Override
    protected Serializable doCreate(Session session) {
        Serializable sessionId = super.doCreate(session);
        redisTemplate.opsForValue().set(sessionId, session);
        return sessionId;
    }

    /**
     * 根据sessionId获取session
     *
     * @param sessionId 会话ID
     * @return 会话
     */
    @Override
    protected Session doReadSession(Serializable sessionId) {
        // 先从缓存中获取session，如果没有再去redis中获取
        Session session = super.doReadSession(sessionId);
        if (null == session) {
            session = redisTemplate.opsForValue().get(sessionId);
        }
        return session;
    }

    /**
     * 更新session最后一次访问时间
     *
     * @param session 会话
     */
    @Override
    protected void doUpdate(Session session) {
        super.doUpdate(session);
        redisTemplate.opsForValue().set(session.getId(), session);
    }

    /**
     * 执行session删除
     *
     * @param session
     */
    @Override
    protected void doDelete(Session session) {
        super.doDelete(session);
        redisTemplate.delete(session.getId());
    }
}
