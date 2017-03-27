package com.lcc.oa.service;

import com.lcc.oa.entity.Vacation;

import java.io.Serializable;
import java.util.List;

/**
 * Created by asus on 2017/3/27.
 */
public interface IVacationService {

    public Serializable doAdd(Vacation vacation) throws Exception;

    public void doUpdate(Vacation vacation) throws Exception;

    public void doDelete(Vacation vacation) throws Exception;

    public List<Vacation> toList(Integer userId) throws Exception;

    public Vacation findById(Integer id) throws Exception;

    public List<Vacation> findByStatus(Integer userId, String status) throws Exception;

}
