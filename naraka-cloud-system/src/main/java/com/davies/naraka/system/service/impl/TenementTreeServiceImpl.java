package com.davies.naraka.system.service.impl;

import com.davies.naraka.autoconfigure.ClassUtils;
import com.davies.naraka.system.domain.entity.SysTenementTree;
import com.davies.naraka.system.domain.entity.SysTenementTreeId;
import com.davies.naraka.system.repository.SysTenementTreeRepository;
import com.davies.naraka.system.service.TenementTreeService;
import com.google.common.base.Preconditions;
import jdk.internal.joptsimple.internal.Strings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author davies
 * @date 2022/6/6 10:58
 */
@Service
public class TenementTreeServiceImpl implements TenementTreeService {

    private final SysTenementTreeRepository treeRepository;

    public TenementTreeServiceImpl(SysTenementTreeRepository treeRepository) {
        this.treeRepository = treeRepository;
    }


    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void moveTree(String from, String to) {
        if (Objects.equals(from, to)) {
            throw new IllegalArgumentException("不能移动到自己下面!");
        }
        // 检查 to 是不是 from 的子节点
        long count = treeRepository.countByIdAncestorAndIdDescendant(from, to);

        Preconditions.checkArgument(count == 0, "使用移动并删除子节点的操作，不能将父节点移动到子节点下");
        // 1，获取 ancestor为 from 的所有关系集合 (被移动的树关系)
        List<SysTenementTree> fromDescendants = findAllDescendant(from);


        // 2，把from这棵树的所有节点关系都删除，解除from这棵树和其他节点的关系。 （删掉旧关系）
        List<String> descendants = fromDescendants.stream()
                .map(SysTenementTree::getId).map(SysTenementTreeId::getDescendant)
                .collect(Collectors.toList());
        descendants.add(from);
        deleteByDescendants(descendants);

        // 3, 通过方法快速建立 from 和 to的关系。
        insertNode(from, to);

        // 4.查出 descendant 为 from 的所有关系 ,用于把from的子节点挂上去建立关系。
        List<SysTenementTree> fromNewAncestors = findAllAncestor(from);

        // 5. 建立关系
        // 5.1 加入from的旧关系
        List<SysTenementTree> relations = new ArrayList<>(fromDescendants);

        // 5.2 将from新的 ancestor 和 from旧的 descendant 建立关系
        for (SysTenementTree fromNewAncestor : fromNewAncestors) {
            for (SysTenementTree fromDescendant : fromDescendants) {

                SysTenementTreeId copy = ClassUtils.copyObject(fromNewAncestor.getId(), new SysTenementTreeId());
                copy.setDistance(fromNewAncestor.getId().getDistance() + fromDescendant.getId().getDistance())
                        .setDescendant(fromDescendant.getId().getDescendant());
                relations.add(new SysTenementTree(copy));
            }
        }
        treeRepository.saveAll(relations);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void moveAndDeleteChildren(String from, String to) {
        if (Objects.equals(from, to)) {
            throw new IllegalArgumentException("不能移动到自己下面!");
        }
        // 检查 to 是不是 from 的子节点
        long count = treeRepository.countByIdAncestorAndIdDescendant(from, to);

        Preconditions.checkArgument(count == 0, "使用移动并删除子节点的操作，不能将父节点移动到子节点下");
        //1，将 ancestor 为 from 的所有记录删除
        delete(from);
        //2, 将 from (d) 与 to (a) 建立距离为1的直接关系。
        insertNode(from, to);
    }


    /**
     * 查询直接下级Id
     *
     * @param ancestor 祖代ID
     * @return 结果
     */
    @Override
    public List<String> getChildren(String ancestor) {

        return treeRepository.findByIdAncestorAndIdDistance(ancestor, 1)
                .stream().map(SysTenementTree::getId).map(SysTenementTreeId::getDescendant)
                .collect(Collectors.toList());


    }


    @Override
    public List<SysTenementTree> findAllDescendant(String ancestor) {
        return this.treeRepository.findByIdAncestor(ancestor);
    }


    /**
     * 根据相关参数查找 关系
     *
     * @param descendant 子节点
     * @return 结果
     */
    @Override
    public List<SysTenementTree> findAllAncestor(String descendant) {
        return treeRepository.findByIdDescendant(descendant);

    }

    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void insertNode(String newDescendant, String ancestor) {

        if (Strings.isNullOrEmpty(ancestor)) {
            ancestor = ROOT;
        }

        // 1 查找出ancestor的所有上级关系。
        List<SysTenementTree> ancestorParents = treeRepository.findByIdDescendant(ancestor);
        //2 复制这些关系，将关系的descendant改为 newDescendant 并且将距离 + 1，并批量保存
        List<SysTenementTree> relations = new ArrayList<>();
        if (!ancestorParents.isEmpty()) {
            relations = ancestorParents.stream().map(closure -> {
                SysTenementTreeId copy = ClassUtils.copyObject(closure.getId(), new SysTenementTreeId());
                copy.setDistance(closure.getId().getDistance() + 1)
                        .setDescendant(newDescendant);
                return new SysTenementTree(copy);
            }).collect(Collectors.toList());
        }
        // 添加ancestor和newDescendant 的直接关系（距离为1）
        relations.add(new SysTenementTree(new SysTenementTreeId()
                .setAncestor(ancestor)
                .setDescendant(newDescendant)
                .setDistance(1)));

        treeRepository.saveAll(relations);

    }

    /**
     * 删除祖节点，及其以下所有节点
     *
     * @param ancestor 祖节点
     * @return 是否成功
     */
    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public void delete(String ancestor) {
        treeRepository.deleteByAncestor(ancestor);
    }

    @Override
    public void deleteByDescendants(Collection<String> descendants) {
        treeRepository.deleteByDescendants(descendants);

    }
}
