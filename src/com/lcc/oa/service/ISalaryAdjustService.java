package com.lcc.oa.service;

import com.lcc.oa.entity.SalaryAdjust;

import java.io.Serializable;
import java.util.List;

/**
 * Created by asus on 2017/3/28.
 */
public interface ISalaryAdjustService {

    public Serializable doAdd(SalaryAdjust bean) throws Exception;

    public void doUpdate(SalaryAdjust bean) throws Exception;

    public void doDelete(SalaryAdjust bean) throws Exception;

    public List<SalaryAdjust> toList(Integer userId) throws Exception;

    public SalaryAdjust findByUserId(Integer userId) throws Exception;

    public SalaryAdjust findById(Integer id) throws Exception;

    public List<SalaryAdjust> findByStatus(Integer userId, String status) throws Exception;
}
