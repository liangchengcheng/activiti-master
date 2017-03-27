package com.lcc.oa.service;

import com.lcc.oa.entity.ExpenseAccount;
import java.io.Serializable;
import java.util.List;

/**
 * Created by asus on 2017/3/28.
 */
public interface IExpenseService {

    public Serializable doAdd(ExpenseAccount bean) throws Exception;

    public void doUpdate(ExpenseAccount bean) throws Exception;

    public void doDelete(ExpenseAccount bean) throws Exception;

    public List<ExpenseAccount> toList(Integer userId) throws Exception;

    public ExpenseAccount findById(Integer id) throws Exception;

    public List<ExpenseAccount> findByStatus(Integer userId, String status) throws Exception;
}
