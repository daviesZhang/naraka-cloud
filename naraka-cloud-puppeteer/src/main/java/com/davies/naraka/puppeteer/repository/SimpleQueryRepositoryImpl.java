package com.davies.naraka.puppeteer.repository;

import com.davies.naraka.puppeteer.QueryParamsProvider;
import com.davies.naraka.puppeteer.SimpleQueryHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.function.Function;

/**
 * @author davies
 * @date 2022/3/28 19:00
 */
@Service
public class SimpleQueryRepositoryImpl implements SimpleQueryRepository {


    private final SimpleQueryHelper queryHelper;

    public SimpleQueryRepositoryImpl(EntityManager entityManager,
                                     @Autowired(required = false) SimpleQueryHelper queryHelper) {
        this.queryHelper = queryHelper == null ? new SimpleQueryHelper(entityManager) : queryHelper;
    }

    @Override
    public <T, V> Page<V> page(Object queryParams, String from, Pageable pageable, Class<T> tClass, Function<T, V> convert) {
        return this.queryHelper.getPage(from, QueryParamsProvider.query(queryParams, pageable), tClass, convert);
    }

    @Override
    public <T, V> List<V> list(Object queryParams, String from, Sort sort, Class<T> tClass, Function<T, V> convert) {
        return this.queryHelper.getList(from, QueryParamsProvider.query(queryParams, sort), tClass, convert);
    }
}
