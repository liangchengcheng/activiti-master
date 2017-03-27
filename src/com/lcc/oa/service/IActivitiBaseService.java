package com.lcc.oa.service;

/**
 * Created by asus on 2017/3/28.
 */
public interface IActivitiBaseService {

    /**
     * 删除用户和组的关系
     */
    public void deleteAllUser() throws Exception;

    /**
     * 删除用户和组的关系
     */
    public void deleteAllRole() throws Exception;

    /**
     * 删除用户和组的关系
     */
    public void deleteAllMemerShip() throws Exception;
}
