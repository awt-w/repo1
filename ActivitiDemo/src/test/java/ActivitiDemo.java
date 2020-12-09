import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipInputStream;

/**
 * Description:
 * date: 2020/12/5 22:56
 *
 * @author Lele
 */
public class ActivitiDemo {
    private ProcessEngine processEngine;

    @Before
    public void InstanceProcessEngine() {
        processEngine = ProcessEngines.getDefaultProcessEngine();
    }

    /**
     * 通过ZIP压缩包来部署流程
     */
    @Test
    public void deployProcessByZIP() {
//        1.获取流程部署Service：RepositoryService
        RepositoryService repositoryService = processEngine.getRepositoryService();
//        2.流程部署
//        3.读取资源包文件，构造InputStream
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("BPMN/evection.zip");
//        4.用InputStream构造ZIPInputStream
        ZipInputStream zipInputStream=new ZipInputStream(inputStream);
//        5.使用ZIPInputStream进行流程部署
        repositoryService.createDeployment().addZipInputStream(zipInputStream).deploy();
    }

    /**
     * 完成个人任务
     */
    @Test
    public void completTask() {
//        1.获取任务的Service：  TaskService
        TaskService taskService = processEngine.getTaskService();
//        2.查询当前人员【待完成】的任务ID：
//        2.1流程定义ID
//        2.2当前操作任务的人员的名称
        Task task = taskService.createTaskQuery()
                .processDefinitionKey("EvectionProcess")
                .taskAssignee("rose")
                .singleResult();
        System.out.println("流程实例ID：" + task.getProcessInstanceId());
        System.out.println("任务ID：" + task.getId());
        System.out.println("任务负责人：" + task.getAssignee());
        System.out.println("任务名称：" + task.getName());
//        3.完成任务，参数为任务的ID
        taskService.complete(task.getId());
    }

    /**
     * 查询个人待执行的任务
     */
    @Test
    public void testPersonalTaskList() {
//        1.获取任务流程的Service： TaskService
        TaskService taskService = processEngine.getTaskService();
//        2.根据流程【定义】的Key，和当前任务的负责人，来查询
        List<Task> taskList = taskService.createTaskQuery()
                .processDefinitionKey("EvectionProcess")
                .taskAssignee("Jerry")
                .list();
//        3.打印信息
        for (Task task : taskList) {
            System.out.println("流程实例ID：" + task.getProcessInstanceId());
            System.out.println("任务ID：" + task.getId());
            System.out.println("任务负责人：" + task.getAssignee());
            System.out.println("任务名称：" + task.getName());
        }
    }

    /**
     * 测试一个流程实例的启动
     */
    @Test
    public void testStartProcess() {
//        1.获取启动流程的Service： RunTimeService
        RuntimeService runtimeService = processEngine.getRuntimeService();
//        2.根据流程【定义】的ID，来启动流程
        ProcessInstance instance = runtimeService.startProcessInstanceByKey("EvectionProcess");
//        3.打印信息
        System.out.println("流程定义ID：" + instance.getProcessDefinitionId());
        System.out.println("流程实例ID：" + instance.getId());
        System.out.println("当前活动ID：" + instance.getActivityId());
    }

    /**
     * 测试流程部署
     */
    @Test
    public void testDeployment() {
//        1.获取RepositoryService
        RepositoryService repositoryService = processEngine.getRepositoryService();
//        2.使用该Service进行流程的部署
//        2.1给该流程定义一个名称
//        2.2将资源文件：bpmn、png加载到部署中
        Deployment deploy = repositoryService.createDeployment()
                .name("出差审批流程")
                .addClasspathResource("BPMN/Evection.bpmn")
                .addClasspathResource("BPMN/Evection.png")
                .deploy();
//          3.打印信息
        System.out.println("流程部署的ID：" + deploy.getId());
        System.out.println("流程部署的名称：" + deploy.getName());

    }
}
