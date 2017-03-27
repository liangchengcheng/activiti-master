package com.lcc.oa.service.impl;

import java.util.List;

import com.lcc.oa.entity.Group;
import com.lcc.oa.service.IBaseService;
import com.lcc.oa.service.IGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GroupServiceImpl implements IGroupService {

    @Autowired
    private IBaseService<Group> baseService;

    @Override
    public List<Group> getGroupList() throws Exception{
        List<Group> list = this.baseService.getAllList("Group");
        return list;
    }

    @Override
    public List<Group> getGroupListPage() throws Exception {
        List<Group> list = this.baseService.findByPage("Group", new String[]{}, new String[]{});
        return list;
    }

}