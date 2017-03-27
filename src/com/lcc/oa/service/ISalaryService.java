package com.lcc.oa.service;

import com.lcc.oa.entity.Salary;

import java.io.Serializable;

/**
 * Created by asus on 2017/3/28.
 */
public interface ISalaryService {

    public Serializable doAdd(Salary salary) throws Exception;

    public void doUpdate(Salary salary) throws Exception;

    public Salary findByUserId(String userId) throws Exception;

    public Salary findById(Integer id) throws Exception;

}
