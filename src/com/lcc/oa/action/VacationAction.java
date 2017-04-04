package com.lcc.oa.action;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import com.lcc.oa.entity.BaseVO;
import com.lcc.oa.entity.CommentVO;
import com.lcc.oa.entity.User;
import com.lcc.oa.entity.Vacation;
import com.lcc.oa.pagination.Pagination;
import com.lcc.oa.pagination.PaginationThreadUtils;
import com.lcc.oa.service.IProcessService;
import com.lcc.oa.service.IUserService;
import com.lcc.oa.service.IVacationService;
import com.lcc.oa.util.UserUtil;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.log4j.Logger;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 请假控制类,没有用动态任务分配
 */

@Controller
@RequestMapping("/vacationAction")
public class VacationAction {
    private static final Logger logger = Logger.getLogger(VacationAction.class);

    @Autowired
    private IVacationService vacationService;

    @Autowired
    protected RuntimeService runtimeService;

    @Autowired
    protected IdentityService identityService;

    @Autowired
    protected TaskService taskService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IProcessService processService;

    /**
     * 查询某人的所有请假申请
     */
    @RequiresPermissions("user:vacation:list")
    @RequestMapping("/toList_page")
    public String toList(HttpSession session, Model model) throws Exception{
        User user = UserUtil.getUserFromSession(session);
        List<Vacation> list = this.vacationService.toList(user.getId());
        //		for(Vacation v : list){
        //			if(BaseVO.APPROVAL_SUCCESS.equals(v.getStatus())){
        //				Vacation vacation = (Vacation)this.historyService.createHistoricVariableInstanceQuery()
        //					.processInstanceId(v.getProcessInstanceId()).variableName("entity");
        //
        //			}
        //		}
        Pagination pagination = PaginationThreadUtils.get();
        model.addAttribute("page", pagination.getPageStr());
        model.addAttribute("vacationList", list);
        return "vacation/list_vacation";
    }

    /**
     * 跳转添加页面
     */
    @RequiresPermissions("user:vacation:toAdd")
    @RequestMapping(value = "/toAdd", method = RequestMethod.GET)
    public ModelAndView toAdd(Model model){
        if(!model.containsAttribute("vacation")) {
            model.addAttribute("vacation", new Vacation());
        }
        return new ModelAndView("vacation/add_vacation").addObject(model);
    }

    /**
     * 详细信息
     */
    @RequiresPermissions("user:vacation:details")
    @RequestMapping(value="/details/{id}", method = RequestMethod.GET)
    public String details(@PathVariable("id") Integer id, Model model) throws Exception{
        Vacation vacation = this.vacationService.findById(id);
        model.addAttribute("vacation", vacation);
        return "/vacation/details_vacation";
    }

    /**
     * 添加并启动请假流程
     */
    @RequiresPermissions("user:vacation:doAdd")
    @RequestMapping(value = "/doAdd", method = RequestMethod.POST)
    public String doAdd(@ModelAttribute("vacation") @Valid Vacation vacation,BindingResult results,
            RedirectAttributes redirectAttributes,
            HttpSession session,
            Model model) throws Exception{
        User user = UserUtil.getUserFromSession(session);
        if(results.hasErrors()){
            model.addAttribute("vacation", vacation);
            return "vacation/add_vacation";
        }

        // 用户未登录不能操作，实际应用使用权限框架实现，例如Spring Security、Shiro等
        if (user == null || user.getId() == null) {
            model.addAttribute("msg", "登录超时，请重新登录!");
            return "login";
        }
        vacation.setUserId(user.getId());
        vacation.setUser_name(user.getName());
        vacation.setTitle(user.getName()+" 的请假申请");
        //业务类型：请假申请
        vacation.setBusinessType(BaseVO.VACATION);
        //审批中
        vacation.setStatus(BaseVO.PENDING);
        vacation.setApplyDate(new Date());
        this.vacationService.doAdd(vacation);
        String businessKey = vacation.getId().toString();
        vacation.setBusinessKey(businessKey);
        try {
            String processInstanceId = this.processService.startVacation(vacation);
            redirectAttributes.addFlashAttribute("message", "流程已启动，流程ID：" + processInstanceId);
            logger.info("processInstanceId: "+processInstanceId);
        } catch (ActivitiException e) {
            if (e.getMessage().indexOf("no processes deployed with key") != -1) {
                logger.warn("没有部署流程!", e);
                redirectAttributes.addFlashAttribute("error", "没有部署流程，请在[工作流]->[流程管理]页面点击<重新部署流程>-待完成");
            } else {
                logger.error("启动请假流程失败：", e);
                redirectAttributes.addFlashAttribute("error", "系统内部错误！");
            }
        } catch (Exception e) {
            logger.error("启动请假流程失败：", e);
            redirectAttributes.addFlashAttribute("error", "系统内部错误！");
        }
        return "redirect:/vacationAction/toAdd";
    }

    /**
     * 审批请假流程
     */
    //*代表 经理、总监、人力
    @RequiresPermissions("user:vacation:toApproval")
    @RequestMapping("/toApproval/{taskId}")
    public String toApproval(@PathVariable("taskId") String taskId, Model model) throws NumberFormatException, Exception{
        Task task = this.taskService.createTaskQuery().taskId(taskId).singleResult();
        // 根据任务查询流程实例
        String processInstanceId = task.getProcessInstanceId();
        ProcessInstance pi = this.runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        Vacation vacation = (Vacation) this.runtimeService.getVariable(pi.getId(), "entity");
        vacation.setTask(task);
        vacation.setProcessInstanceId(processInstanceId);
        List<CommentVO> commentList = this.processService.getComments(processInstanceId);
        String taskDefinitionKey = task.getTaskDefinitionKey();
        logger.info("taskDefinitionKey: "+taskDefinitionKey);
        String result = null;
        if("modifyApply".equals(taskDefinitionKey)){
            result = "vacation/modify_vacation";
        }else{
            result = "vacation/audit_vacation";
        }
        model.addAttribute("vacation", vacation);
        model.addAttribute("commentList", commentList);
        return result;
    }

    /**
     * 完成任务
     */
    //数据库中权限字符串为user:*:complate， 通配符*匹配到vacation所以有权限操作
    @RequiresPermissions("user:vacation:complate")
    @RequestMapping("/complate/{taskId}")
    public String complate(
            @RequestParam("vacationId") Integer vacationId,
            @RequestParam("content") String content,
            @RequestParam("completeFlag") Boolean completeFlag,
            @PathVariable("taskId") String taskId,
            RedirectAttributes redirectAttributes,
            HttpSession session) throws Exception{
        User user = UserUtil.getUserFromSession(session);
        String groupType = user.getGroup().getType();
        Vacation vacation = this.vacationService.findById(vacationId);
        Vacation baseVacation = (Vacation) this.runtimeService.getVariable(vacation.getProcessInstanceId(), "entity");
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("isPass", completeFlag);
        if(completeFlag){
            //由userTask自动分配审批权限
            //variables.put("auditGroup", "hr");
            if("hr".equals(groupType)){
                vacation.setStatus(BaseVO.APPROVAL_SUCCESS);
            }
        }else{
            baseVacation.setTitle(baseVacation.getUser_name()+" 的请假申请失败,需修改后重新提交！");
            vacation.setStatus(BaseVO.APPROVAL_FAILED);
            variables.put("entity", baseVacation);
        }
        this.vacationService.doUpdate(vacation);
        // 完成任务
        this.processService.complete(taskId, content, user.getId().toString(), variables);

        redirectAttributes.addFlashAttribute("message", "任务办理完成！");
        return "redirect:/processAction/todoTaskList_page";
    }

    /**
     * 调整请假申请
     */
    @RequiresPermissions("user:vacation:modify")
    @RequestMapping(value = "/modifyVacation/{taskId}", method = RequestMethod.POST)
    public String modifyVacation(
            @ModelAttribute("vacation") @Valid Vacation vacation,
            BindingResult results,
            @PathVariable("taskId") String taskId,
            @RequestParam("processInstanceId") String processInstanceId,
            @RequestParam("reApply") Boolean reApply,
            RedirectAttributes redirectAttributes,
            HttpSession session,
            Model model) throws Exception {
        if(results.hasErrors()){
            model.addAttribute("vacation", vacation);
            return "vacation/modify_vacation";
        }

        User user = UserUtil.getUserFromSession(session);
        Map<String, Object> variables = new HashMap<String, Object>();
        vacation.setUserId(user.getId());
        vacation.setUser_name(user.getName());
        vacation.setBusinessType(BaseVO.VACATION);
        vacation.setApplyDate(new Date());
        vacation.setBusinessKey(vacation.getId().toString());
        vacation.setProcessInstanceId(processInstanceId);
        if(reApply){
            //修改请假申请
            vacation.setTitle(user.getName()+" 的请假申请！");
            vacation.setStatus(BaseVO.PENDING);
            //由userTask自动分配审批权限
            //	        if(vacation.getDays() <= 3){
            //            	variables.put("auditGroup", "manager");
            //            }else{
            //            	variables.put("auditGroup", "director");
            //            }
            redirectAttributes.addFlashAttribute("message", "任务办理完成，请假申请已重新提交！");
        }else{
            vacation.setTitle(user.getName()+" 的请假申请已取消！");
            vacation.setStatus(BaseVO.APPROVAL_FAILED);
            redirectAttributes.addFlashAttribute("message", "任务办理完成，已经取消您的请假申请！");
        }
        this.vacationService.doUpdate(vacation);
        variables.put("entity", vacation);
        variables.put("reApply", reApply);
        this.processService.complete(taskId, null, user.getId().toString(), variables);

        return "redirect:/processAction/todoTaskList_page";
    }
}

