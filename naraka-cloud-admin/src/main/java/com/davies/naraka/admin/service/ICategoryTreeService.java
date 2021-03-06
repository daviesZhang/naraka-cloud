package com.davies.naraka.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.davies.naraka.admin.domain.entity.CategoryTree;
import com.davies.naraka.admin.domain.enums.CategoryType;
import com.davies.naraka.autoconfigure.ClassUtils;
import com.google.common.base.Preconditions;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author davies
 * @since 2022-02-08
 */
public interface ICategoryTreeService extends IService<CategoryTree> {


    /**
     * 根节点
     */
     Integer ROOT = 0;


    /**
     * 是否为根节点
     * @param id
     * @return
     */
   default boolean isRoot(int id){
       return ROOT==id;
   }

    /**
     * 将某个节点移动到另一个节点下，且子节点全部上浮。
     * <code>
     * 例如下图(省略顶级分类)：
     *        1                                     1
     *        |                                   / | \
     *        2                                  3  4  5
     *      / | \         moveNode(2,7)         / \
     *     3  4 5     --------------->         6   7
     *    / \                                 /  / | \
     *   6  7                                8  9  10 2
     *  /  / \
     * 8  9  10
     * </code>
     * 实现步骤如下：
     * 1，将 ancestor 为 2 的所有记录删除，并保存descendant集合 D
     * 2，将 descendant 为 2 的所有记录删除，并保存 ancestor集合 A
     *      上面两步解除了与 节点2 的关系。
     * 3, 找出所有 ancestor 为 集合A 并且 descendant为集合B的记录，将这些记录的距离减 1
     * @param from 被移动分类的node
     * @param to 目标分类的 node
     * @param type 关系类型
     * @throws IllegalArgumentException 如果node或target所表示的分类不存在、或node==target
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    void moveAndRise(Integer from, Integer to, CategoryType type);




    /**
     * 将某个节点移动到另一个节点下，且子节点全部删除。
     * 注意，这个方法不支持将父节点移动到自己的子节点下。因为逻辑上是不成立（或者无意义的）
     * <code>
     * 例如下图(省略顶级分类)：
     *        1                                     1
     *        |                                     |
     *        2                                     2
     *      / | \         moveNode(7,2)          / | \ \
     *     3  4 5     ------------------>      3  4  5 7
     *    / \                                 /
     *   6  7                                6
     *  /  / \                              /
     * 8  9  10                            8
     * </code>
     * 实现步骤如下：
     * 1，将 ancestor 为 7 的所有记录删除
     * 2, 将 7 与 2 建立距离为1的直接关系。
     * @param from 被移动分类的node
     * @param to 目标分类的 node
     * @param type 关系类型
     * @throws IllegalArgumentException 如果node或target所表示的分类不存在、或node==target
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    default void moveAndDeleteChildren(Integer from, Integer to, CategoryType type) {
        if (Objects.equals(from, to)) {
            throw new IllegalArgumentException("不能移动到自己下面!");
        }
        // 检查 to 是不是 from 的子节点
        long count = count(new QueryWrapper<>(new CategoryTree()
                .setAncestor(from)
                .setDescendant(to)
                .setCategoryType(type)
        ));

        Preconditions.checkArgument(count == 0, "使用移动并删除子节点的操作，不能将父节点移动到子节点下");

        //1，将 ancestor 为 from 的所有记录删除
        delete(from, type);

        //2, 将 from (d) 与 to (a) 建立距离为1的直接关系。
        insertNode(from, to, type);
    }




    /**
     * 将某个节点移动到另一个节点下，且子节点全部一起移动过去（移动整个分支树）。
     * 注意，与 {@link #moveAndDeleteChildren(Integer, Integer, CategoryType)}
     * 方法一样，该方法不允许将父节点移动到子节点
     * <code>
     * 例如下图(省略顶级分类)：
     *        1                                    1
     *        |                                    |
     *        2                                    2
     *      / | \         moveNode(7,2)         / | \ \
     *     3  4 5     ------------------>      3  4 5  7
     *    / \                                 /       | \
     *   6  7                                6        9 10
     *  /  / \                              /
     * 8  9  10                            8
     * </code>
     * 实现步骤如下：
     * 1，获取 ancestor为 7 的所有关系集合 A0
     * 2，查出 descendant 为 2 的所有关系 A1
     * 3，删掉 descendant 为 7 以及 集合 A0.map(item -> item::descendant)的所有记录，解除7这棵树和其他节点的关系。
     * 4, 遍历 A1 + 2 并遍历 A0 + 7，逐一创建新的关系，
     * 5，保存关系。
     * @param from 被移动分类的node
     * @param to 目标分类的 node
     * @param type 关系类型


     * @throws IllegalArgumentException 如果node或target所表示的分类不存在、或node==target
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    void moveTree(Integer from, Integer to, CategoryType type);




    /**
     * 如果是节点不是移动到自己的子节点下，则默认选择移动整棵树 {@link #moveTree(Integer, Integer, CategoryType)}
     * 如果节点是移动到自己的子节点下，则默认选择移动并上浮 {@link #moveAndRise(Integer, Integer, CategoryType)}
     *
     * @param from 被移动分类的node
     * @param to 目标分类的 node
     * @param type 关系类型
     * @throws IllegalArgumentException 如果node或target所表示的分类不存在、或node==target
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    default void smartMove(Integer from, Integer to, CategoryType type) {
        // 检查 to 是不是 from 的子节点
        long count = count(new QueryWrapper<>(new CategoryTree()
                .setAncestor(from)
                .setDescendant(to)
                .setCategoryType(type)


        ));
        if(count > 0) {
            moveAndRise(from, to, type);
        } else {
            moveTree(from, to, type);
        }
    }




    /**
     * 查询直接上级ID
     *
     * @param descendant 后代节点
     * @param type 关系类型


     * @return 结果
     */
    default Optional<Integer> getParent(Integer descendant, CategoryType type) {
        return findAncestor(descendant, type, 1).map(CategoryTree::getAncestor);
    }



    /**
     * 查询直接下级Id
     *
     * @param ancestor 祖代ID
     * @param type     关系类型


     * @return 结果
     */
    default List<Integer> getChildren(Integer ancestor, CategoryType type) {
        return findDescendant(ancestor, type, 1)
                .stream()
                .map(CategoryTree::getDescendant)
                .collect(Collectors.toList());
    }



    /**
     * 插入一个新的节点
     * 步骤如下：
     * 1，把ancestor作为 descendant查找出它的所有祖节点关系记录。
     * 2，复制这些记录的 descendant改为 newDescendant 且距离加1
     * 3，添加 ancestor和 newDescendant 距离为1的记录。
     *
     * @param newDescendant 子节点
     * @param ancestor 父节点
     * @param type     节点类型


     * @return 是否成功
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    default boolean insertNode(Integer newDescendant, Integer ancestor, CategoryType type) {
        // 1 查找出ancestor的所有上级关系。
        List<CategoryTree> ancestorParents = list(new QueryWrapper<>(new CategoryTree()
                .setDescendant(ancestor)
                .setCategoryType(type)
        ));
        //2 复制这些关系，将关系的descendant改为 newDescendant 并且将距离 + 1，并批量保存
        List<CategoryTree> relations = new ArrayList<>();
        if (!ancestorParents.isEmpty()) {
            relations = ancestorParents.stream().map(closure -> {
                CategoryTree copy = ClassUtils.copyObject(closure, new CategoryTree());
                return copy.setDistance(closure.getDistance() + 1)
                        .setDescendant(newDescendant);
            }).collect(Collectors.toList());
        }
        // 添加ancestor和newDescendant 的直接关系（距离为1）
        relations.add(new CategoryTree()
                .setAncestor(ancestor)
                .setDescendant(newDescendant)
                .setDistance(1)
                .setCategoryType(type));

        return saveBatch(relations);
    }




    /**
     * 删除祖节点，及其以下所有节点
     * @param ancestor 祖节点
     * @param type 关系类型


     * @return 是否成功
     */
    @Transactional(rollbackFor = RuntimeException.class)
    default boolean delete(Integer ancestor, CategoryType type) {
        return remove(new QueryWrapper<>(new CategoryTree()
                .setAncestor(ancestor)
                .setCategoryType(type)
        ));
    }


    /**
     * 根据子节点来删除所有关系。
     * @param descendants 子节点集合
     * @param type 关系类型
     * @return 是否成功
     */
    default boolean deleteByDescendants(Collection<Integer> descendants, CategoryType type) {
        return remove(new QueryWrapper<>(
                        new CategoryTree()
                                .setCategoryType(type)


                ).lambda().in(CategoryTree::getDescendant, descendants)
        );



    }


    /**
     * 根据相关参数查找 关系
     * @param descendant 子节点
     * @param type 关系类型
     * @param distance 距离


     * @return 结果
     */
    default Optional<CategoryTree> findAncestor(Integer descendant, CategoryType type, Integer distance) {
        return Optional.ofNullable(this.getOne(new QueryWrapper<>(new CategoryTree()
                .setDescendant(descendant)
                .setCategoryType(type)
                .setDistance(distance)

        )));
    }

    /**
     * 根据相关参数查找 关系
     * @param ancestor 祖节点
     * @param type 关系类型
     * @param distance 距离
     * @return 结果
     */
    default List<CategoryTree> findDescendant(Integer ancestor, CategoryType type, Integer distance) {
        return this.list(new QueryWrapper<>(new CategoryTree()
                .setAncestor(ancestor)
                .setCategoryType(type)
                .setDistance(distance))

);
    }
    /**
     * 根据相关参数查找 关系
     * @param descendant 子节点
     * @param type 关系类型


     * @return 结果
     */
    default List<CategoryTree> findAllAncestor(Integer descendant, CategoryType type) {

        return this.list(new QueryWrapper<>(new CategoryTree()
                .setDescendant(descendant)
                .setCategoryType(type)

        ));
    }

    /**
     * 根据相关参数查找 关系
     * @param ancestor 祖节点
     * @param type 关系类型


     * @return 结果
     */
    default List<CategoryTree> findAllDescendant(Integer ancestor, CategoryType type) {
        return this.list(new QueryWrapper<>(new CategoryTree()
                .setAncestor(ancestor)
                .setCategoryType(type)));
    }
}
