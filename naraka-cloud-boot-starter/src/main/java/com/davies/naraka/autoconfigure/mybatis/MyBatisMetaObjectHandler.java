package com.davies.naraka.autoconfigure.mybatis;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.davies.naraka.autoconfigure.CurrentUserNameSupplier;
import com.davies.naraka.autoconfigure.properties.MyBatisProperties;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;

/**
 * 自动填充创建,修改时间,如果属于已登录状态,填充创建人,修改人
 *
 * @author davies
 * @date 2022/1/28 10:57 AM
 */
@Slf4j
public class MyBatisMetaObjectHandler implements MetaObjectHandler {


    private final CurrentUserNameSupplier currentUserNameSupplier;

    private final MyBatisProperties myBatisProperties;

    public MyBatisMetaObjectHandler(CurrentUserNameSupplier currentUserNameSupplier, MyBatisProperties myBatisProperties) {
        this.currentUserNameSupplier = currentUserNameSupplier;
        this.myBatisProperties = myBatisProperties;
    }

    @Override
    public void insertFill(MetaObject metaObject) {
        if (!Strings.isNullOrEmpty(myBatisProperties.getFillCreatedTime())) {
            this.strictInsertFill(metaObject, myBatisProperties.getFillCreatedTime(), LocalDateTime::now, LocalDateTime.class);
        }
        if (!Strings.isNullOrEmpty(myBatisProperties.getFillCreatedBy())) {
            String username = currentUserNameSupplier.get();
            if (!Strings.isNullOrEmpty(username)) {
                this.strictInsertFill(metaObject, myBatisProperties.getFillCreatedBy(), () -> username, String.class);
            }
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        if (!Strings.isNullOrEmpty(myBatisProperties.getFillUpdatedTime())) {
            this.strictUpdateFill(metaObject, myBatisProperties.getFillUpdatedTime(), LocalDateTime::now, LocalDateTime.class);
        }
        if (!Strings.isNullOrEmpty(myBatisProperties.getFillUpdatedBy())) {
            String username = currentUserNameSupplier.get();
            if (!Strings.isNullOrEmpty(username)) {
                this.strictUpdateFill(metaObject, myBatisProperties.getFillUpdatedBy(), () -> username, String.class);
            }
        }
    }
}
