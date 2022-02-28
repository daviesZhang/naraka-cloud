package com.davies.naraka.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.davies.naraka.admin.mapper.GroupMapper;
import com.davies.naraka.admin.domain.entity.Group;
import com.davies.naraka.admin.service.IGroupService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author davies
 * @since 2022-01-24
 */
@Service
public class GroupServiceImpl extends ServiceImpl<GroupMapper, Group> implements IGroupService {

}
