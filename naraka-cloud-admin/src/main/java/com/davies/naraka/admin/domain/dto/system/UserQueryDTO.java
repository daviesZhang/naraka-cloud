package com.davies.naraka.admin.domain.dto.system;

import com.davies.naraka.admin.domain.enums.UserStatus;
import com.davies.naraka.admin.domain.enums.UserType;
import com.davies.naraka.cloud.common.annotation.ColumnName;
import com.davies.naraka.cloud.common.annotation.Crypto;
import com.davies.naraka.cloud.common.annotation.QueryFilter;
import com.davies.naraka.cloud.common.domain.QueryField;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

import static com.davies.naraka.cloud.common.enums.QueryFilterType.*;

/**
 *
 *
 {"query":{
 "username": {
 "type": "LIKE",
 "filter": "e"
 },
 "status": {
 "type": "CONTAINS",
 "filter": [
 1,
 0
 ]
 },
 "email": "sychopian@foxmail.com",
 "phone": {
 "type": "STARTS_WITH",
 "filter": "18566242532"
 },
 "passwordExpireTime": {
 "type": "LESSTHANEQUAL",
 "filter": "2055-09-01 11:11:11"
 },
 "createdTime": [
 {
 "type": "GREATERTHANEQUAL",
 "filter": "2021-09-01 11:11:11"
 },
 {
 "type": "LESSTHANEQUAL",
 "filter": "2022-01-28 11:11:11"
 }
 ]
 },
 "current": "1",
 "size": "10"
 }
 * @author davies
 * @date 2022/1/29 9:32 PM
 */
@Data
public class UserQueryDTO {


    @QueryFilter(types = {
            EQUALS,
            LIKE})
    @ColumnName(name = "u.username")
    private QueryField<String> username;


    @QueryFilter(types = {CONTAINS})
    private QueryField<List<UserStatus>> status;


    @QueryFilter(types = {CONTAINS})
    private QueryField<List<UserType>> type;



    @Crypto
    @QueryFilter(types = {STARTS_WITH,EQUALS})
    private QueryField<String> email;

    @Crypto
    @QueryFilter(types = {STARTS_WITH,EQUALS})
    private QueryField<String> phone;

    @QueryFilter(types = {
            ORDER_ASC,
            ORDER_DESC,
            LESSTHAN,
            EQUALS,
            LESSTHANEQUAL,
            GREATERTHANE,
            GREATERTHANEQUAL})
    @ColumnName(name = "u.created_time")
    private List<QueryField<LocalDateTime>> createdTime;

    @QueryFilter(types = {
            ORDER_ASC,
            ORDER_DESC,
            LESSTHAN,
            EQUALS,
            LESSTHANEQUAL,
            GREATERTHANE,
            GREATERTHANEQUAL})
    private List<QueryField<LocalDateTime>> passwordExpireTime;


    @QueryFilter(types = {
            EQUALS,
            LIKE})
    @ColumnName(name = "r.code")
    private QueryField<String> role;
}
