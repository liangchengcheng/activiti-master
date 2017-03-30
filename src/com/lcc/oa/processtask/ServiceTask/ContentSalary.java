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
 * 记录薪资调整
 * 如果用ServiceTask的activiti:class 属性执行此类，则salaryService和saService 都为空
 * 因为每次执行都会为JavaDelegate创建新的实例，也就是每次都new 一个新的。
 * 这样@Autowired 就不能自动注入，必须让Spring容器管理此类，而不是每次都new一个
 * 这样@Autowired 才会自动注入。所以，用@Component注解，此注解会让ContentSalary类实例化到spring容器中，
 * 达到让Spring容器管理此类的目的，并且bpmn的ServiceTask属性用activiti:delegateExpression="${contentSalary}" 这样
 * 就可以解决调用ContentSalary时 salaryService、saService为空的问题。
 * Created by asus on 2017/3/31.
 */

@Component
public class ContentSalary implements JavaDelegate {

    @Autowired
    private ISalaryService salaryService;

    @Autowired
    private ISalaryAdjustService saService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        //execution代表执行
        Integer businessKey = (Integer) execution.getVariable("businessKey");
        SalaryAdjust salaryAdjust = this.saService.findById(businessKey);

        Salary salary = this.salaryService.findByUserId(salaryAdjust.getUserId().toString());
        //调整金额
        BigDecimal newMoney = salaryAdjust.getAdjustMoney();
        salary.setBaseMoney(newMoney);
        this.salaryService.doUpdate(salary);
    }
}
