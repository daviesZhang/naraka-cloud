package com.davies.naraka.system.repository;

import com.davies.naraka.system.domain.entity.SysTenementTree;
import com.davies.naraka.system.domain.entity.SysTenementTreeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface SysTenementTreeRepository extends JpaRepository<SysTenementTree, SysTenementTreeId> {


    List<SysTenementTree> findByIdAncestor(String ancestor);

    List<SysTenementTree> findByIdAncestorAndIdDistance(String ancestor, Integer distance);

    List<SysTenementTree> findByIdDescendant(String descendant);


    long countByIdAncestorAndIdDescendant(String ancestor, String descendant);


    @Modifying
    @Query("delete from SysTenementTree where id.ancestor =:ancestor")
    int deleteByAncestor(@Param("ancestor") String ancestor);

    @Modifying
    @Query("delete from SysTenementTree where id.descendant in (:descendants)")
    int deleteByDescendants(@Param("descendants") Collection<String> descendants);


}
