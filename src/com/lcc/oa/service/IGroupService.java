package com.lcc.oa.service;

import com.lcc.oa.entity.Group;
import java.util.List;

/**
 * Created by asus on 2017/3/28.
 */
public interface IGroupService {

    public List<Group> getGroupList() throws Exception;

    public List<Group> getGroupListPage() throws Exception;
}
