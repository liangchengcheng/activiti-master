package com.lcc.oa.service.impl;

import com.lcc.oa.entity.User;
import com.lcc.oa.service.IActivitiBaseService;
import com.lcc.oa.service.IGroupService;
import com.lcc.oa.service.IUserService;
import com.lcc.oa.util.BeanUtils;
import org.springframework.stereotype.Service;
import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.UserQuery;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.Serializable;
import java.util.List;

/**
 * Created by asus on 2017/3/28.
 */
@Service
public class UserServiceImpl extends BaseServiceImpl<User> implements IUserService{

    private static final Logger logger = Logger.getLogger(UserServiceImpl.class);

    @Autowired
    protected PasswordHelper passwordHelper;

    @Autowired
    protected IdentityService identityService;

    @Autowired
    protected IActivitiBaseService activitiBaseService;

    @Autowired
    protected IGroupService groupService;

    @Override
    public List<User> getUserList_page() throws Exception {
        List<User> list = findByPage("User", new String[]{}, new String[]{});
        return list;
    }

    /**
     * 通过user_name 获取用户
     */
    @Override
    public User getUserByName(String user_name) throws Exception {
        User user = getUnique("User",new String[]{"name"},new String[]{user_name});
        if (BeanUtils.isBlank(user)){
            return null;
        }
        return user;
    }

    /**
     * 根据用户的id获取
     */
    @Override
    public User getUserById(Integer id) throws Exception {
        return getUnique("User",new String[]{"id"},new String[]{id.toString()});
    }

    /**
     * 通过 group获取一组用户
     */
    @Override
    public List<User> getUserByGroupId(String groupId) throws Exception {
        List<User> list = findByPage("User",new String[]{"group"},new String[]{groupId});
        return list;
    }

    /**
     * 更新用户
     */
    @Override
    public void doUpdate(User user) throws Exception {
        passwordHelper.encryptPassword(user);
        update(user);
    }

    @Override
    public Serializable doAdd(User user, String groupId, boolean synToActiviti) throws Exception {
        return null;
    }

    @Override
    public void doDelete(User user, boolean synToActiviti) throws Exception {

    }

    @Override
    public void synAllUserAndRoleToActiviti() throws Exception {

    }

    @Override
    public void deleteAllActivitiIdentifyData() throws Exception {

    }
}
