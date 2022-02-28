package com.davies.naraka.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Preconditions;
import com.davies.naraka.admin.common.ClassUtils;
import com.davies.naraka.admin.domain.enums.CategoryType;
import com.davies.naraka.admin.mapper.CategoryTreeMapper;
import com.davies.naraka.admin.domain.entity.CategoryTree;
import com.davies.naraka.admin.service.ICategoryTreeService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author davies
 * @since 2022-02-08
 */
@Service
public class CategoryTreeServiceImpl extends ServiceImpl<CategoryTreeMapper, CategoryTree> implements ICategoryTreeService {
    @Override
    public void moveAndRise(Integer from, Integer to, CategoryType type) {
        if (Objects.equals(from, to)) {

            throw new IllegalArgumentException("不能移动到自己下面!");
        }

        //1 获取 from 节点的所有祖节点。
        List<Integer> ancestors = deleteAndGetAllAncestor(from, type);
        Preconditions.checkArgument(!ancestors.isEmpty(), "不能移动根节点");

        //2 获取 from 节点的所有子(孙)节点
        List<Integer> descendants = deleteAndGetAllDescendant(from, type);

        //3 修改所有 ancestor 为 ancestors集合， descendant为descendants集合的记录，并将距离减1
        baseMapper.subDistance(ancestors, descendants, type, 1);

        //4 将from作为 descendant 转移到 to 下，且距离为1
        this.insertNode(from, to, type);
    }


    @Override
    public void moveTree(Integer from, Integer to, CategoryType type) {
        if (Objects.equals(from, to)) {
            throw new IllegalArgumentException("不能移动到自己下面!");
        }
        // 检查 to 是不是 from 的子节点
        long count = count(new QueryWrapper<>(new CategoryTree()
                .setDescendant(to)
                .setAncestor(from)
                .setCategoryType(type)
        ));
        Preconditions.checkArgument(count == 0, "使用移动并删除子节点的操作，不能将父节点移动到子节点下");
        // 1，获取 ancestor为 from 的所有关系集合 (被移动的树关系)
        List<CategoryTree> fromDescendants = findAllDescendant(from, type);


        // 2，把from这棵树的所有节点关系都删除，解除from这棵树和其他节点的关系。 （删掉旧关系）
        List<Integer> descendants = fromDescendants.stream().map(CategoryTree::getDescendant).collect(Collectors.toList());
        descendants.add(from);
        deleteByDescendants(descendants, type);

        // 3, 通过方法快速建立 from 和 to的关系。
        insertNode(from, to, type);

        // 4.查出 descendant 为 from 的所有关系 ,用于把from的子节点挂上去建立关系。
        List<CategoryTree> fromNewAncestors = findAllAncestor(from, type);

        // 5. 建立关系
        // 5.1 加入from的旧关系
        List<CategoryTree> relations = new ArrayList<>(fromDescendants);

        // 5.2 将from新的 ancestor 和 from旧的 descendant 建立关系
        for (CategoryTree fromNewAncestor : fromNewAncestors) {
            for (CategoryTree fromDescendant : fromDescendants) {

                CategoryTree copy = ClassUtils.copyObject(fromNewAncestor, new CategoryTree());
                copy.setDistance(fromNewAncestor.getDistance() + fromDescendant.getDescendant())
                        .setDescendant(fromDescendant.getDescendant());
                relations.add(copy);
            }
        }

        saveBatch(relations);
    }

    private List<Integer> deleteAndGetAllAncestor(Integer descendant, CategoryType type) {
        CategoryTree condition = new CategoryTree();
        condition.setDescendant(descendant);
        condition.setCategoryType(type);

        List<Integer> ancestors = list(new QueryWrapper<>(condition))
                .stream().map(CategoryTree::getAncestor).collect(Collectors.toList());
        if (!ancestors.isEmpty()) {
            remove(new QueryWrapper<>(condition));
        }
        return ancestors;
    }

    private List<Integer> deleteAndGetAllDescendant(Integer ancestor, CategoryType type) {
        List<Integer> descendants = findAllDescendant(ancestor, type)
                .stream().map(CategoryTree::getDescendant).collect(Collectors.toList());
        delete(ancestor, type);
        return descendants;
    }
}
