package com.davies.naraka.config.mybatis;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * 自动填充创建,修改时间,如果属于已登录状态,填充创建人,修改人
 * @author davies
 * @date 2022/1/28 10:57 AM
 */
@Slf4j
@Component
public class MyBatisMetaObjectHandler implements MetaObjectHandler {

    @Autowired
    private HttpServletRequest request;

    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createdTime", LocalDateTime::now, LocalDateTime.class);

        if (!Strings.isNullOrEmpty(request.getRemoteUser())) {
            this.strictInsertFill(metaObject, "createdBy", () -> request.getRemoteUser(), String.class);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updatedTime", LocalDateTime::now, LocalDateTime.class);
        if (!Strings.isNullOrEmpty(request.getRemoteUser())) {
            this.strictUpdateFill(metaObject, "updatedBy", () -> request.getRemoteUser(), String.class);
        }
    }
}
