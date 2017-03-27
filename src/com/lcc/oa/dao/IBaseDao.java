package com.lcc.oa.dao;

import java.io.Serializable;
import java.util.List;

public interface IBaseDao<T> {
    /**
     *  保存实体
     */
    public Serializable add(T bean) throws Exception;

    /**
     *  保存或者更新实体
     */
    public void saveOrUpdate(T bean) throws Exception;


    /**
     * 删除
     */
    public void delete(T bean) throws Exception;

    /**
     * 更新实体
     */
    public void update(T bean) throws Exception;

    /**
     * 执行HQL
     */
    public List<T> createQuery(final String hql) throws Exception;

    /**
     * 根据ID获取实体
     */
    public T getBean(final Class<T> obj,final Serializable id) throws Exception;

    /**
     * 分页-无条件
     */
    public List<T> findByPage(final String hql, int firstResult, int maxResult) throws Exception;

    /**
     * 返回唯一一条数据
     */
    public T unique(final String hql) throws Exception;
}

