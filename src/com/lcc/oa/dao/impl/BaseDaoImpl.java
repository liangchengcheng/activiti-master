package com.lcc.oa.dao.impl;

import com.lcc.oa.dao.IBaseDao;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.Serializable;
import java.util.List;

/**
 * Created by asus on 2017/3/27.
 */
public class BaseDaoImpl<T> implements IBaseDao<T> {

    @Autowired
    @Qualifier("sessionFactory")
    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public Serializable add(T bean) throws Exception {
        return getSession().save(bean);
    }

    @Override
    public void saveOrUpdate(T bean) throws Exception {
        this.getSession().saveOrUpdate(bean);
    }

    @Override
    public void delete(T bean) throws Exception {
        this.getSession().saveOrUpdate(bean);
    }

    @Override
    public void update(T bean) throws Exception {
        this.getSession().update(bean);
    }

    @Override
    public List<T> createQuery(String hql) throws Exception {
        List<T> list = this.getSession().createQuery(hql).list();
        return list;
    }

    @Override
    public T getBean(Class<T> obj, Serializable id) throws Exception {
        return (T) getSession().get(obj.getClass(),id);
    }

    @Override
    public List<T> findByPage(String hql, int firstResult, int maxResult) throws Exception {
        Session session = sessionFactory.getCurrentSession();
        Query query =session.createQuery(hql);
        query.setFirstResult(firstResult);
        query.setMaxResults(maxResult);
        return query.list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public T unique(String hql) throws Exception {
        Query query = getSession().createSQLQuery(hql);
        return (T) query.uniqueResult();
    }
}
