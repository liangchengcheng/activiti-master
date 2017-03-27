package com.lcc.oa.service;

import com.lcc.oa.entity.GroupAndResource;

import java.util.List;

/**
 * Created by asus on 2017/3/28.
 */
public interface IGroupAndResourceService {
    public List<GroupAndResource> getResource(Integer groupId) throws Exception;

    public void doAdd(GroupAndResource gar) throws Exception;

    public void doDelete(GroupAndResource gar) throws Exception;
}
