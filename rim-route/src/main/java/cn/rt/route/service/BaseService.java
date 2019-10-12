package cn.rt.route.service;

import java.util.List;

/**
 * @author ruanting
 * @date 2019/10/12
 */
public interface BaseService<T> {

    int insert(T entity);

    int delete(T entity);

    int deleteByKey(Object key);

    int updateByKey(T entity);

    int updateByKeySelective(T entity);

    T selectByKey(Object key);

    T selectOne(T entity);

    List<T> select(T entity);

}
