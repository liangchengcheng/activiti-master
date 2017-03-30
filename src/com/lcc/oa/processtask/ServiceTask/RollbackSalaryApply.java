package com.lcc.oa.processtask.ServiceTask;

import com.lcc.oa.entity.Salary;
import com.lcc.oa.entity.SalaryAdjust;
import com.lcc.oa.service.ISalaryAdjustService;
import com.lcc.oa.service.ISalaryService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 回滚薪资的调整
 * Created by asus on 2017/3/31.
 */
@Component
public class RollbackSalaryApply implements JavaDelegate {

    @Autowired
    private ISalaryService salaryService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        //获取薪资调整记录
        SalaryAdjust salaryAdjust = (SalaryAdjust) execution.getVariable("entity");

        BigDecimal baseMoney = (BigDecimal) execution.getVariable("baseMoney");
        Salary salary = this.salaryService.findByUserId(salaryAdjust.getUserId().toString());
        salary.setBaseMoney(baseMoney);
        this.salaryService.doUpdate(salary);
    }
}
