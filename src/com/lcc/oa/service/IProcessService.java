package com.lcc.oa.service;

import com.lcc.oa.entity.*;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.ui.Model;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Created by asus on 2017/3/28.
 */
public interface IProcessService {

    /**
     * 启动薪资调整
     */
    public String startSalaryAdjust(SalaryAdjust salary) throws Exception;

    /**
     * 启动请假流程
     */
    public String startVacation(Vacation vacation) throws Exception;

    /**
     * 启动报销流程
     */
    public String startExpense(ExpenseAccount expense) throws Exception;

    /**
     * 查询代办任务
     */
    public List<BaseVO> findTodoTask(User user, Model model) throws Exception;

    /**
     * 签收任务
     */
    public void doClaim(User user, String taskId) throws Exception;

    /**
     * 完成任务
     */
    public void complete(String taskId, String content, String userid, Map<String, Object> variables) throws Exception;

    /**
     * 获取评论
     */
    public List<CommentVO> getComments(String processInstanceId) throws Exception;

    /**
     * 显示流程图,带流程跟踪
     */
    public InputStream getDiagram(String processInstanceId) throws Exception;

    /**
     * 显示图片-通过流程ID，不带流程跟踪(没有乱码问题)
     */
    public InputStream getDiagramByProInstanceId_noTrace(String resourceType, String processInstanceId) throws Exception;

    /**
     * 显示图片-通过部署ID，不带流程跟踪(没有乱码啊问题)
     */
    public InputStream getDiagramByProDefinitionId_noTrace(String resourceType, String processDefinitionId) throws Exception;

    /**
     * 读取已结束中的流程-admin查看
     */
    public List<BaseVO> findFinishedProcessInstances(Model model) throws Exception;

    /**
     * 各个审批人员查看自己完成的任务
     */
    public List<BaseVO> findFinishedTaskInstances(User user, Model model) throws Exception;

    /**
     * 查看正在运行的请假流程
     */
    public List<BaseVO> listRuningVacation(User user) throws Exception;

    /**
     * 查看正在运行的报销流程
     */
    public List<BaseVO> listRuningExpense(User user) throws Exception;

    /**
     * 查看正在运行的薪资跳转流程
     */
    public List<BaseVO> listRuningSalaryAdjust(User user) throws Exception;

    /**
     * 管理运行中流程
     */
    public List<ProcessInstance> listRuningProcess(Model model) throws Exception;

    /**
     * 激活流程实例
     */
    public void activateProcessInstance(String processInstanceId) throws Exception;

    /**
     * 挂起流程实例
     */
    public void suspendProcessInstance(String processInstanceId) throws Exception;
}
