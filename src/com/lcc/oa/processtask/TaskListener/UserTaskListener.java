package com.lcc.oa.processtask.TaskListener;

import java.util.ArrayList;
import java.util.List;
import com.lcc.oa.entity.UserTask;
import com.lcc.oa.service.IUserTaskService;
import com.lcc.oa.util.Constants;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 动态用户任务分配
 * Created by asus on 2017/3/31.
 */
public class UserTaskListener implements TaskListener {
    private static final long serialVersionUID = 2190559253653576032L;
    private static final Logger logger = Logger.getLogger(UserTaskListener.class);

    @Autowired
    protected RepositoryService repositoryService;

    @Autowired
    private IUserTaskService userTaskService;

    @Override
    public void notify(DelegateTask delegateTask) {
        //com.zml.oa.vacation:8:30012(从task获取进程初始化默认id)
        String processDefinitionId = delegateTask.getProcessDefinitionId();
        System.out.println(
                "processInstId: " + delegateTask.getProcessInstanceId()
                        + " taskDefKey: " + delegateTask.getTaskDefinitionKey()
                        + " id: " + delegateTask.getId()
                        + " name: " + delegateTask.getName());

        System.out.println("" + processDefinitionId
                + " repositoryService: " + repositoryService
                + " userTaskService: " + userTaskService);
        //创建查询
        ProcessDefinition processDefinition = this.repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(processDefinitionId)
                .singleResult();
        //String processDefinitionName = processDefinition.getName();
        //请假流程 com.zml.oa.vacation
        String processDefinitionKey = processDefinition.getKey();
        //directorAudit
        String taskDefinitionKey = delegateTask.getTaskDefinitionKey();
        System.out.println(processDefinitionKey + "===" + taskDefinitionKey);
        try {
            //用户任务表-待完善
            List<UserTask> taskList = this.userTaskService.findByWhere(processDefinitionKey);
            for (UserTask userTask : taskList) {
                String taskKey = userTask.getTaskDefKey();
                String taskType = userTask.getTaskType();
                String ids = userTask.getCandidate_ids();
                if (taskDefinitionKey.equals(taskKey)) {
                    switch (taskType) {
                        case Constants.ASSIGNEE: {
                            //设置代理人；受托人；分配到任务的人
                            delegateTask.setAssignee(ids);
                            logger.info("assignee id: " + ids);
                            break;
                        }

                        case Constants.CANDIDATE_USER: {
                            String[] userIds = ids.split(",");
                            List<String> users = new ArrayList<String>();
                            for (int i = 0; i < userIds.length; i++) {
                                users.add(userIds[i]);
                            }
                            delegateTask.addCandidateUsers(users);
                            logger.info("候选人审批 ids: " + ids);
                            break;
                        }

                        case Constants.CANDIDATE_GROUP: {
                            String[] groupIds = ids.split(",");
                            List<String> groups = new ArrayList<String>();
                            for (int i = 0; i < groupIds.length; i++) {
                                groups.add(groupIds[i]);
                            }
                            delegateTask.addCandidateGroups(groups);
                            logger.info("候选组审批 ids: " + ids);
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
