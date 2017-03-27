package com.lcc.oa.service;

import com.lcc.oa.entity.UserTask;

import java.io.Serializable;
import java.util.List;

/**
 * Created by asus on 2017/3/27.
 */
public interface IUserTaskService {

    public Serializable doAdd(UserTask userTask) throws Exception;

    public void doUpdate(UserTask userTask) throws Exception;

    public void doDelete(UserTask userTask) throws Exception;

    public List<UserTask> toList(String procDefKey) throws Exception;

    public Integer deleteAll() throws Exception;

    public UserTask findById(Integer id) throws Exception;

    public List<UserTask> findByWhere(String procDefKey) throws Exception;

    public List<UserTask> getAll() throws Exception;
}
