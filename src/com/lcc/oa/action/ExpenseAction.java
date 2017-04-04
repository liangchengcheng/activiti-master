package com.lcc.oa.action;

import com.lcc.oa.entity.BaseVO;
import com.lcc.oa.entity.CommentVO;
import com.lcc.oa.entity.ExpenseAccount;
import com.lcc.oa.entity.User;
import com.lcc.oa.service.IExpenseService;
import com.lcc.oa.service.IProcessService;
import com.lcc.oa.service.IUserService;
import com.lcc.oa.util.UserUtil;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.collections.map.HashedMap;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 报销的控制器
 * Created by liangchengcheng on 2017/4/4.
 */
@Controller
@RequestMapping("/expenseAction")
public class ExpenseAction {
    private static final Logger logger = Logger.getLogger(ExpenseAction.class);

    @Autowired
    protected IExpenseService expenseService;

    @Autowired
    protected RuntimeService runtimeService;

    @Autowired
    protected TaskService taskService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IProcessService processService;

    /**
     * 是否含有某种权限
     */
    @RequiresPermissions("user:expense:toAdd")
    @RequestMapping(value = "toAdd",method = RequestMethod.GET)
    public ModelAndView toAdd(Model model){
        if (!model.containsAttribute("expense")){
            model.addAttribute("expense",new ExpenseAccount());
        }
        return new ModelAndView("expense/add_expense").addObject(model);
    }

    /**
     * 详情界面
     */
    @RequiresPermissions("user:expense:details")
    @RequestMapping(value = "details/{id}",method =  RequestMethod.GET)
    public String details(@PathVariable("id") Integer id,Model model)throws Exception{
        ExpenseAccount expense = this.expenseService.findById(id);
        model.addAttribute("expense",expense);
        return "/expense/details_expense";
    }

    public String toAdd(@ModelAttribute("expense") @Valid ExpenseAccount expense, BindingResult results,
                        RedirectAttributes redirectAttributes, HttpSession session, Model model)throws Exception{
        User user = UserUtil.getUserFromSession(session);
        if (results.hasErrors()){
            model.addAttribute("expense",expense);
            return "expense/add_expense";
        }

        //用户没登录的话就不能操作,
        if (user == null || user.getId() == null){
            model.addAttribute("msg","登录超时,请重新登录");
            return "login";
        }

        //初始化一个报销记录并且设置相关的属性,并且启动报销流程
        expense.setApplyDate(new Date());
        expense.setUserId(user.getId());
        expense.setUser_name(user.getName());
        expense.setTitle(user.getName()+" 的报销申请");
        expense.setBusinessType(BaseVO.EXPENSE);
        expense.setStatus(BaseVO.PENDING);
        this.expenseService.doAdd(expense);
        String businessKey = expense.getId().toString();
        expense.setBusinessKey(businessKey);

        try{
            String processInstanceId = this.processService.startExpense(expense);
            redirectAttributes.addFlashAttribute("message", "流程已启动，流程ID：" + processInstanceId);
            logger.info("processInstanceId: "+processInstanceId);
        }catch (ActivitiException e) {
            if (e.getMessage().indexOf("no processes deployed with key") != -1) {
                logger.warn("没有部署流程!", e);
                redirectAttributes.addFlashAttribute("error", "没有部署流程，请在[工作流]->[流程管理]页面点击<重新部署流程>");
            } else {
                logger.error("启动报销流程失败：", e);
                redirectAttributes.addFlashAttribute("error", "系统内部错误！");
            }
        } catch (Exception e) {
            logger.error("启动报销流程失败：", e);
            redirectAttributes.addFlashAttribute("error", "系统内部错误！");
        }
        return "redirect:/expenseAction/toAdd";
    }

    /**
     * 审批报销流程  //*代表 财务， 如果业务编号，也可以代表其他角色
     */
    @RequiresPermissions("user:expense:toApproval")
    @RequestMapping("/toApproval/{taskId}")
    public String toApproval(@PathVariable("taskId") String taskId, Model model)throws Exception{
        Task task = this.taskService.createTaskQuery().taskId(taskId).singleResult();
        //根据任务查询流程实例
        String processInstanceId = task.getProcessInstanceId();
        ProcessInstance pi = this.runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        ExpenseAccount expense = (ExpenseAccount) this.runtimeService.getVariable(pi.getId(), "entity");
        expense.setTask(task);
        List<CommentVO> commentList = this.processService.getComments(processInstanceId);
        model.addAttribute("commentList", commentList);
        model.addAttribute("expense", expense);
        return "expense/audit_expense";
    }

    /**
     * 完成任务
     */
    @RequiresPermissions("user:expense:complate")
    @RequestMapping("/complate/{taskId}")
    public String complate(
            @RequestParam("expenseId") Integer expenseId,
            @PathVariable("taskId") String taskId,
            RedirectAttributes redirectAttributes,
            HttpSession session) throws Exception{
                User user = UserUtil.getUserFromSession(session);
        ExpenseAccount expense = this.expenseService.findById(expenseId);
        Map<String, Object> variables = new HashMap<String, Object>();
        expense.setStatus(BaseVO.APPROVAL_SUCCESS);
        this.expenseService.doUpdate(expense);
        //完成任务
        this.processService.complete(taskId,null,user.getId().toString(),variables);
        redirectAttributes.addFlashAttribute("message", "任务办理完成！");
        return "redirect:/processAction/todoTaskList_page";
    }





}
