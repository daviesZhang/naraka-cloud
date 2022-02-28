package com.davies.naraka.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.davies.naraka.admin.mapper.LoginLogMapper;
import com.davies.naraka.admin.domain.entity.LoginLog;
import com.davies.naraka.admin.service.ILoginLogService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 管理员登陆日子  服务实现类
 * </p>
 *
 * @author davies
 * @since 2022-01-24
 */
@Service
public class LoginLogServiceImpl extends ServiceImpl<LoginLogMapper, LoginLog> implements ILoginLogService {

}
