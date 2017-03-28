package com.lcc.oa.service.impl;

import com.lcc.oa.dao.IJdbcDao;
import com.lcc.oa.entity.UserTask;
import com.lcc.oa.service.IBaseService;
import com.lcc.oa.service.IUserTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.Serializable;
import java.util.List;

@Service
public class UserTaskServiceImpl implements IUserTaskService {

    @Autowired
    private IBaseService<UserTask> baseService;

    @Autowired
    protected IJdbcDao jdbcDao;

    @Override
    public Serializable doAdd(UserTask userTask) throws Exception {
        return baseService.add(userTask);
    }

    @Override
    public void doUpdate(UserTask userTask) throws Exception {
        this.baseService.update(userTask);
    }

    @Override
    public void doDelete(UserTask userTask) throws Exception {
        this.baseService.delete(userTask);
    }

    @Override
    public List<UserTask> toList(String procDefKey) throws Exception {
        List<UserTask> list = this.baseService.findByPage("UserTask", new String[]{"procDefKey"}, new String[]{procDefKey});
        return list;
    }

    @Override
    public UserTask findById(Integer id) throws Exception {
        return this.baseService.getBean(UserTask.class, id);
    }

    @Override
    public Integer deleteAll() throws Exception {
        String sql = "delete from T_USER_TASK";
        return this.jdbcDao.delete(sql, null);
    }

    @Override
    public List<UserTask> findByWhere(String procDefKey) throws Exception {
        return this.baseService.findByWhere("UserTask", new String[]{"procDefKey"}, new String[]{procDefKey});
    }

    @Override
    public List<UserTask> getAll() throws Exception {
        return this.baseService.getAllList("UserTask");
    }

}
