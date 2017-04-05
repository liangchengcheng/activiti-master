package com.lcc.oa.action;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lcc.oa.entity.GroupAndResource;
import com.lcc.oa.entity.Resource;
import com.lcc.oa.entity.UserTask;
import com.lcc.oa.pagination.Pagination;
import com.lcc.oa.pagination.PaginationThreadUtils;
import com.lcc.oa.service.IGroupAndResourceService;
import com.lcc.oa.service.IResourceService;
import com.lcc.oa.service.IUserTaskService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.delegate.ActivityBehavior;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 权限管理
 */
@Controller
@RequiresPermissions("admin:*")
@RequestMapping("/permissionAction")
public class PermissionAction {

    @Autowired
    protected RepositoryService repositoryService;

    @Autowired
    protected IUserTaskService userTaskService;

    @Autowired
    protected IGroupAndResourceService garService;

    @Autowired
    protected IResourceService resourceService;

    @RequestMapping(value = "/loadBpmn_page")
    public String loadBpmnInfo(Model model) {
        ProcessDefinitionQuery proDefQuery = repositoryService.createProcessDefinitionQuery().orderByDeploymentId().desc();
        Integer totalSum = proDefQuery.list().size();
        int[] pageParams = PaginationThreadUtils.setPage(totalSum);
        Pagination pagination = PaginationThreadUtils.get();
        List<ProcessDefinition> processDefinitionList = proDefQuery.listPage(pageParams[0], pageParams[1]);
        model.addAttribute("proDefList", processDefinitionList);
        model.addAttribute("page", pagination.getPageStr());
        return "permission/list_bpmn";
    }

    @RequestMapping(value = "/setAuthor")
    public String setAuthor(@RequestParam("id") String processDefinitionId) {
        ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity)
                repositoryService.getProcessDefinition(processDefinitionId);
        //获得当前任务的所有节点
        List<ActivityImpl> activitiList = processDefinition.getActivities();
        for (ActivityImpl activity : activitiList) {

        }
        return null;
    }

    @RequestMapping(value = "/listUserTask")
    @ResponseBody
    public List<UserTask> listUserTask(@RequestParam("processKey") String processKey) throws Exception {
        List<UserTask> list = this.userTaskService.findByWhere(processKey);
        return list;
    }

    /**
     * 删除 userTask表中数据，重新初始化节点信息。
     */
    @RequestMapping(value = "/initialization")
    public void initialization(HttpServletResponse response) throws Exception {
        PrintWriter out = response.getWriter();
        try {
            this.userTaskService.deleteAll();
            ProcessDefinitionQuery proDefQuery = repositoryService.createProcessDefinitionQuery().orderByDeploymentId().desc();
            List<ProcessDefinition> processDefinitionList = proDefQuery.list();
            for (ProcessDefinition processDefinition : processDefinitionList) {
                //读取节点信息保存到userTask表
                setSingleActivitiInfo(processDefinition);
            }
            out.print("success");
        } catch (Exception e) {
            out.print("fail");
            throw e;
        }
        //RedirectAttributes redirectAttributes
        //redirectAttributes.addFlashAttribute("message", "初始化成功！");
        //return "redirect:/permissionAction/loadBpmn_page";
    }

    /**
     * 初始化单个bpmn文件到usertask表
     */
    @RequestMapping(value = "/loadSingleBpmn")
    public String loadSingleBpmn(@RequestParam("processDefinitionId") String processDefinitionId,
                                 RedirectAttributes redirectAttributes) throws Exception {
        ProcessDefinition processDefinition = repositoryService.getProcessDefinition(processDefinitionId);
        //读取节点信息保存到userTask表
        setSingleActivitiInfo(processDefinition);
        redirectAttributes.addFlashAttribute("message", "加载成功！");
        return "redirect:/permissionAction/loadBpmn_page";
    }

    /**
     * 设置单独的任务
     */
    private void setSingleActivitiInfo(ProcessDefinition processDefinition) throws Exception {
        String proDefKey = processDefinition.getKey();
        List<UserTask> list = this.userTaskService.findByWhere(proDefKey);
        //默认entity
        ProcessDefinitionEntity processDef = (ProcessDefinitionEntity)
                repositoryService.getProcessDefinition(processDefinition.getId());
        //获得当前任务的所有节点
        List<ActivityImpl> activitiList = processDef.getActivities();
        for (ActivityImpl activity : activitiList) {
            //获取Behavior
            ActivityBehavior activityBehavior = activity.getActivityBehavior();
            boolean isFound = false;
            //是否为用户任务
            if (activityBehavior instanceof UserTaskActivityBehavior) {
                UserTaskActivityBehavior userTaskActivityBehavior = (UserTaskActivityBehavior) activityBehavior;
                TaskDefinition taskDefinition = userTaskActivityBehavior.getTaskDefinition();
                //任务所属角色
                String taskDefKey = taskDefinition.getKey();
                Expression taskName = taskDefinition.getNameExpression();

                //判断表中是否存在此节点
                if (list.size() != 0) {
                    for (UserTask userTask : list) {
                        if (taskDefKey.equals(userTask.getTaskDefKey())) {
                            userTask.setProcDefKey(processDefinition.getKey());
                            userTask.setProcDefName(processDefinition.getName());
                            userTask.setTaskDefKey(taskDefKey);
                            userTask.setTaskName(taskName.toString());
                            this.userTaskService.doUpdate(userTask);
                            isFound = true;
                            break;
                        }
                    }
                }

                if (!isFound) {
                    UserTask userTask = new UserTask();
                    userTask.setProcDefKey(processDefinition.getKey());
                    userTask.setProcDefName(processDefinition.getName());
                    userTask.setTaskDefKey(taskDefKey);
                    userTask.setTaskName(taskName.toString());
                    this.userTaskService.doAdd(userTask);
                }
            }
        }
    }

    @RequestMapping(value = "/setPermission")
    public String setPermission(@RequestParam("processKey") String processKey, HttpServletRequest request,
                                RedirectAttributes redirectAttribute) throws Exception {
        List<UserTask> list = this.userTaskService.findByWhere(processKey);
        for (UserTask userTask : list) {
            String taskDefKey = userTask.getTaskDefKey();
            String ids = request.getParameter(taskDefKey + "_id");
            String names = request.getParameter(taskDefKey + "_name");
            String taskType = request.getParameter(taskDefKey + "_taskType");

            userTask.setTaskType(taskType);
            userTask.setCandidate_name(names);
            userTask.setCandidate_ids(ids);
            this.userTaskService.doUpdate(userTask);
        }
        redirectAttribute.addFlashAttribute("message", "设置审批人员成功！");
        return "redirect:/permissionAction/loadBpmn_page";
    }

    @RequestMapping(value = "/listPermission_page")
    public String listPermission(@RequestParam("groupId") Integer groupId, Model model) throws Exception {
        List<Resource> resList = this.resourceService.getResourceListPage();
        Pagination pagination = PaginationThreadUtils.get();

        List<GroupAndResource> garList = this.garService.getResource(groupId);
        Map<Integer, Integer> garMap = new HashMap<Integer, Integer>();
        for (GroupAndResource gar : garList) {
            garMap.put(gar.getResourceId(), gar.getId());
        }
        model.addAttribute("resList", resList);
        model.addAttribute("garMap", garMap);
        model.addAttribute("groupId", groupId);
        model.addAttribute("page", pagination.getPageStr());
        return "permission/list_permission";
    }

    @RequestMapping(value = "/addPermission")
    public void addPermission(@RequestParam("resourceId") Integer resourceId,
                              @RequestParam("groupId") Integer groupId,
                              HttpServletResponse response) throws Exception {
        PrintWriter print = response.getWriter();
        try {
            GroupAndResource gar = new GroupAndResource();
            gar.setGroupId(groupId);
            gar.setResourceId(resourceId);
            this.garService.doAdd(gar);
            print.print("success");
        } catch (Exception e) {
            print.print("fail");
            throw e;
        }
    }

    /**
     * 删除Permission
     */
    @RequestMapping(value = "/delPermission")
    public void delPermission(@RequestParam("id") Integer id, HttpServletResponse response) throws Exception {
        PrintWriter print = response.getWriter();
        try {
            GroupAndResource gar = new GroupAndResource();
            gar.setId(id);
            this.garService.doDelete(gar);
            print.print("success");
        } catch (Exception e) {
            print.print("fail");
            throw e;
        }
    }

}
