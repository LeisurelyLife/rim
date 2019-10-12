package cn.rt.route.service.impl;

import cn.rt.route.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author ruanting
 * @date 2019/10/12
 */
public abstract class BaseServiceImpl<T> implements BaseService<T> {

    @Autowired
    protected Mapper<T> mapper;

    @Override
    public int insert(T entity) {
        return mapper.insert(entity);
    }

    @Override
    public int delete(T entity) {
        return mapper.delete(entity);
    }

    @Override
    public int deleteByKey(Object key) {
        return mapper.deleteByPrimaryKey(key);
    }

    @Override
    public int updateByKey(T entity) {
        return mapper.updateByPrimaryKey(entity);
    }

    @Override
    public int updateByKeySelective(T entity) {
        return mapper.updateByPrimaryKeySelective(entity);
    }

    @Override
    public T selectByKey(Object key) {
        return mapper.selectByPrimaryKey(key);
    }

    @Override
    public T selectOne(T entity) {
        return mapper.selectOne(entity);
    }

    @Override
    public List<T> select(T entity) {
        return mapper.select(entity);
    }
}
