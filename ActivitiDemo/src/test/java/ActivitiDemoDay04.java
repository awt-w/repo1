import com.activiti.demo.pojo.Evection;
import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * create 2020-12-08 10:13
 */
public class ActivitiDemoDay04 {
    private ProcessEngine processEngine;

    @Before
    public void getProcessEngine() {
        processEngine = ProcessEngines.getDefaultProcessEngine();
    }

    /**
     * 交接组任务
     */
    @Test
    public void handoverTask(){
        String assignee="王五";
        String candidate="lisi";
        String processDefinitionKey="evection-group";
        TaskService taskService = processEngine.getTaskService();
        List<Task> taskList = taskService.createTaskQuery()
                .processDefinitionKey(processDefinitionKey)
                .taskAssignee(assignee)
                .list();
        if (taskList.size()>0){
            Task task=taskList.get(0);
            System.out.println("流程实例ID：" + task.getProcessInstanceId());
            System.out.println("任务ID：" + task.getId());
            System.out.println("任务负责人：" + task.getAssignee());
            System.out.println("任务名称：" + task.getName());

//            归还任务。原理：将当前任务的负责人设置为另一位【候选人】
            taskService.setAssignee(task.getId(),candidate);
            System.out.println("任务：" + task.getId() + "  已被  原负责人：" + assignee + "  交接给："+candidate);
        }else{
            System.out.println("没有找到" + assignee + "的待归还任务");
        }
    }

    /**
     * 归还组任务
     */
    @Test
    public void returnTheTask(){
        String assignee="王五";
        String processDefinitionKey="evection-group";
        TaskService taskService = processEngine.getTaskService();
        List<Task> taskList = taskService.createTaskQuery()
                .processDefinitionKey(processDefinitionKey)
                .taskAssignee(assignee)
                .list();
        if (taskList.size()>0){
            Task task=taskList.get(0);
            System.out.println("流程实例ID：" + task.getProcessInstanceId());
            System.out.println("任务ID：" + task.getId());
            System.out.println("任务负责人：" + task.getAssignee());
            System.out.println("任务名称：" + task.getName());

//            归还任务。原理：将当前任务的负责人置空
            taskService.setAssignee(task.getId(),null);
            System.out.println("任务：" + task.getId() + "已被  负责人：" + assignee + "  归还");
        }else{
            System.out.println("没有找到" + assignee + "的待归还任务");
        }
    }

    /**
     * 拾取组任务
     */
    @Test
    public void claimTask(){
        String candidate="王五";
        String processDefinitionKey="evection-group";
        TaskService taskService = processEngine.getTaskService();
        List<Task> taskList = taskService.createTaskQuery()
                .processDefinitionKey(processDefinitionKey)
                .taskCandidateUser(candidate)
                .list();
        if (taskList.size()>0){
            Task task=taskList.get(0);
            System.out.println("流程实例ID：" + task.getProcessInstanceId());
            System.out.println("任务ID：" + task.getId());
            System.out.println("任务负责人：" + task.getAssignee());
            System.out.println("任务名称：" + task.getName());

//            拾取任务:  claim
            taskService.claim(task.getId(),candidate);
            System.out.println("任务：" + task.getId() + "已被  用户：" + candidate + "  拾取");
        }else{
            System.out.println("没有找到" + candidate + "的待办任务");
        }
    }

    /**
     * 根据某一个候选人查询任务,此时候选人并不能完成任务，需要先【拾取任务】
     * 1.主要代码：taskCandidateOrAssigned(candidate)，
     * 此方法是可以同时处理【候选人】或【任务负责人】的查询，
     * 如只需要查询候选的，使用taskCandidateUser()
     *
     */
    @Test
    public void queryTaskByCandidate() {
        String candidate = "lisi";
        String processDefinitionKey = "evection-group";
        TaskService taskService = processEngine.getTaskService();
        List<Task> taskList = taskService.createTaskQuery()
                .taskCandidateOrAssigned(candidate)
                .processDefinitionKey(processDefinitionKey)
                .list();
        for (Task task : taskList) {
            System.out.println("流程实例ID：" + task.getProcessInstanceId());
            System.out.println("任务ID：" + task.getId());
            System.out.println("任务负责人：" + task.getAssignee());
            System.out.println("任务名称：" + task.getName());
        }
        if (taskList.size() > 0) {
            taskService.complete(taskList.get(0).getId());
            System.out.println("完成任务" + taskList.get(0).getName());
        }
    }

    /**
     * 启动组任务
     */
    @Test
    public void startGroup() {
        RuntimeService runtimeService = processEngine.getRuntimeService();
        Map<String, Object> variable = new HashMap<>();
        variable.put("staff", "A员工");
//        多个候选人，用逗号隔开
        variable.put("managers","lisi,王五");
        ProcessInstance processInstance = runtimeService
                .startProcessInstanceByKey("evection-group", variable);
    }

    /**
     * 部署组任务
     */
    @Test
    public void deploymentGroup() {
        RepositoryService repositoryService = processEngine.getRepositoryService();
        Deployment deploy = repositoryService.createDeployment()
                .addClasspathResource("BPMN\\evection-group.bpmn")
                .name("组任务出差申请")
                .deploy();
        System.out.println("流程部署KEY：" + deploy.getKey());
        System.out.println("流程部署ID：" + deploy.getId());
        System.out.println("流程部署名称" + deploy.getName());
    }

    /**
     * 部署测试变量的bpmn
     */
    @Test
    public void deploymentEvectionVariable() {
//        1.获取流程service
        RepositoryService repositoryService = processEngine.getRepositoryService();
//        2.部署
        Deployment deploy = repositoryService.createDeployment().addClasspathResource("BPMN/evection-variable.bpmn").name("带变量的出差申请").deploy();
        System.out.println("流程部署的ID：" + deploy.getId());
        System.out.println("流程部署的名称：" + deploy.getName());
    }

    /**
     * 完成小于三天的任务
     */
    @Test
    public void completeTask01() {
        String taskAssignee = "D财务";
//        1.获取任务service
        TaskService taskService = processEngine.getTaskService();
//        2.使用流程部署Key与负责人名称查询待完成的流程实例ID
        List<Task> taskList = taskService.createTaskQuery()
                .taskAssignee(taskAssignee)
                .processDefinitionKey("evection-variable")
                .list();
//        3.判断是否真的存在待完成任务
        for (Task task : taskList) {
            System.out.println("流程实例ID：" + task.getProcessInstanceId());
            System.out.println("任务ID：" + task.getId());
            System.out.println("任务负责人：" + task.getAssignee());
            System.out.println("任务名称：" + task.getName());
        }
        if (taskList.size() > 0) {
//        4.完成
            taskService.complete(taskList.get(0).getId());
            System.out.println(taskAssignee + "已完成" + taskList.get(0).getName());
        } else {
            System.out.println(taskAssignee + "不存在待完成任务");
        }

    }

    /**
     * 开启变量bpmn任务
     */
    @Test
    public void startEvectionVariable() {
//        1.获取runtimeservice
        RuntimeService runtimeService = processEngine.getRuntimeService();

//        2.设置变量
        Map<String, Object> variable = new HashMap<>();
//              2.1设置实体变量（请假天数）
        Evection evection = new Evection();
        evection.setDays(2.5D);
        variable.put("evection", evection);
//              2.2设置任务负责人名称
        variable.put("staff", "A员工");
        variable.put("deManager", "B部门经理");
        variable.put("geManager", "C总经理");
        variable.put("finance", "D财务");

//        3.通过key来启动流程,并传入变量
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("evection-variable", variable);
//        4.打印流程实例ID
        System.out.println("流程实例ID：" + processInstance.getId());
    }

    @Test
    public void deploymentEvectionListener() {
        RepositoryService repositoryService = processEngine.getRepositoryService();
        Deployment deploy = repositoryService.createDeployment().addClasspathResource("BPMN/evection-listener.bpmn").deploy();
        System.out.println("流程部署Key：" + deploy.getKey());
    }

    @Test
    public void startEvcetionListener() {
        RuntimeService runtimeService = processEngine.getRuntimeService();
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("evection-listener");
        System.out.println("流程定义ID：" + processInstance.getProcessDefinitionId());
        System.out.println("流程实例ID：" + processInstance.getId());
        System.out.println("当前活动ID：" + processInstance.getActivityId());
    }

    @Test
    public void delete() {
        RepositoryService repositoryService = processEngine.getRepositoryService();

        repositoryService.deleteDeployment("22501", true);
        System.out.println("del 22501 ok");
    }
}
