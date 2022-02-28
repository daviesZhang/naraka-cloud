package com.davies.naraka.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.davies.naraka.admin.mapper.OperationLogMapper;
import com.davies.naraka.admin.domain.entity.OperationLog;
import com.davies.naraka.admin.service.IOperationLogService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 操作时间  服务实现类
 * </p>
 *
 * @author davies
 * @since 2022-01-24
 */
@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements IOperationLogService {

}
