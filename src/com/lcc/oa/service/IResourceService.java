package com.lcc.oa.service;

import com.lcc.oa.entity.GroupAndResource;
import com.lcc.oa.entity.Resource;

import java.util.List;

/**
 * Created by asus on 2017/3/28.
 */
public interface IResourceService {

    public Resource getPermissions(Integer id) throws Exception;

    public List<Resource> getMenus(List<GroupAndResource> gr) throws Exception;

    public List<Resource> getAllResource() throws Exception;

    public List<Resource> getResourceListPage() throws Exception;

    public List<Resource> getResourceByType() throws Exception;

    public void doAdd(Resource entity) throws Exception;

    public void doUpdate(Resource entity) throws Exception;

    public void doDelete(Resource entity) throws Exception;
}
