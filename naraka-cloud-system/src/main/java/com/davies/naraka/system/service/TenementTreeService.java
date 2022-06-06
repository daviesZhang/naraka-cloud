package com.davies.naraka.system.service;

import com.davies.naraka.system.domain.entity.SysTenementTree;

import java.util.Collection;
import java.util.List;

/**
 * @author davies
 * @date 2022/6/6 10:57
 */
public interface TenementTreeService {


    /**
     * 根节点
     */
    String ROOT = "0";

    /**
     * 将某个节点移动到另一个节点下，且子节点全部一起移动过去（移动整个分支树）。
     * 注意，与 {@link #moveAndDeleteChildren(String, String)}
     * 方法一样，该方法不允许将父节点移动到子节点
     * <code>
     * 例如下图(省略顶级分类)：
     * 1                                    1
     * |                                    |
     * 2                                    2
     * / | \         moveNode(7,2)         / | \ \
     * 3  4 5     ------------------>      3  4 5  7
     * / \                                 /       | \
     * 6  7                                6        9 10
     * /  / \                              /
     * 8  9  10                            8
     * </code>
     * 实现步骤如下：
     * 1，获取 ancestor为 7 的所有关系集合 A0
     * 2，查出 descendant 为 2 的所有关系 A1
     * 3，删掉 descendant 为 7 以及 集合 A0.map(item -> item::descendant)的所有记录，解除7这棵树和其他节点的关系。
     * 4, 遍历 A1 + 2 并遍历 A0 + 7，逐一创建新的关系，
     * 5，保存关系。
     *
     * @param from 被移动分类的node
     * @param to   目标分类的 node
     * @throws IllegalArgumentException 如果node或target所表示的分类不存在、或node==target
     */

    void moveTree(String from, String to);


    /**
     * 将某个节点移动到另一个节点下，且子节点全部删除。
     * 注意，这个方法不支持将父节点移动到自己的子节点下。因为逻辑上是不成立（或者无意义的）
     * <code>
     * 例如下图(省略顶级分类)：
     * 1                                     1
     * |                                     |
     * 2                                     2
     * / | \         moveNode(7,2)          / | \ \
     * 3  4 5     ------------------>      3  4  5 7
     * / \                                 /
     * 6  7                                6
     * /  / \                              /
     * 8  9  10                            8
     * </code>
     * 实现步骤如下：
     * 1，将 ancestor 为 7 的所有记录删除
     * 2, 将 7 与 2 建立距离为1的直接关系。
     *
     * @param from 被移动分类的node
     * @param to   目标分类的 node
     * @throws IllegalArgumentException 如果node或target所表示的分类不存在、或node==target
     */
    void moveAndDeleteChildren(String from, String to);


    List<String> getChildren(String ancestor);

    List<SysTenementTree> findAllDescendant(String ancestor);

    List<SysTenementTree> findAllAncestor(String descendant);


    void insertNode(String newDescendant, String ancestor);

    /**
     * 删除祖节点，及其以下所有节点
     *
     * @param ancestor 祖节点
     * @return 是否成功
     */
    void delete(String ancestor);

    void deleteByDescendants(Collection<String> descendants);
}
