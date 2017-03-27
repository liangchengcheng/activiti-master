package com.lcc.oa.service.impl;

/**
 * Created by asus on 2017/3/28.
 */
import java.util.List;

import com.lcc.oa.entity.GroupAndResource;
import com.lcc.oa.service.IBaseService;
import com.lcc.oa.service.IGroupAndResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GroupAndResourceServiceImpl extends BaseServiceImpl<GroupAndResource>
        implements IGroupAndResourceService {

    @Autowired
    private IBaseService<GroupAndResource> baseService;

    @Override
    public List<GroupAndResource> getResource(Integer groupId) throws Exception {
        List<GroupAndResource> list = this.baseService.findByWhere("GroupAndResource", new String[]{"groupId"}, new String[]{groupId.toString()});
        return list;
    }

    @Override
    public void doAdd(GroupAndResource gar) throws Exception {
        this.baseService.add(gar);
    }

    @Override
    public void doDelete(GroupAndResource gar) throws Exception {
        this.baseService.delete(gar);
    }

}
