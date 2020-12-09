import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.List;

/**
 * Description:
 * date: 2020/12/6 11:36
 *
 * @author Lele
 */
public class ActivitiDemoDay02 {
    private ProcessEngine processEngine;

    @Before
    public void getProcessEngine() {
        processEngine = ProcessEngines.getDefaultProcessEngine();
    }

    /**
     * 挂起或激活某一个流程实例
     */
    @Test
    public void suspendProcessByProcessInstanceID(){
//        1.获取运行时的Service：RunTimeService
        RuntimeService runtimeService = processEngine.getRuntimeService();
//        2.通过流程实例ID，获取流程实例对象
        String processInstanceId="15001";
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
//        3.判断流程实例是否是挂起状态
        boolean suspended = processInstance.isSuspended();
//        4.yes，修改为激活状态
        if (suspended){
            runtimeService.activateProcessInstanceById(processInstanceId);
            System.out.println("流程实例+" + processInstanceId + "+状态被修改为【激活】状态");
        }else {
//        5.no，修改为挂起状态
            runtimeService.suspendProcessInstanceById(processInstanceId);
            System.out.println("流程实例+" + processInstanceId + "+状态被修改为【挂起】状态");
        }
    }

    /**
     * 挂起或激活【所有的】流程实例
     */
    @Test
    public void suspendAllProcessInstance() {
//        1.获取流程部署相关的Service：RepositoryService
        RepositoryService repositoryService = processEngine.getRepositoryService();
//        2.获取流程定义的对象
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("EvectionProcess")
                .orderByProcessDefinitionVersion().desc()
                .singleResult();
//        3.判断当前流程定义的对象是否是挂起状态
        boolean processDefinitionSuspended = processDefinition.isSuspended();

//        4.获取流程定义的id，因为修改状态需要指定一个确定的流程定义
        String processDefinitionId = processDefinition.getId();
//        5.如果是挂起状态，修改为激活状态
        if (processDefinitionSuspended) {
            repositoryService.activateProcessDefinitionById(processDefinitionId, true, null);
            System.out.println("将状态修改为了激活");
        } else {
//        6.如果是激活状态，修改为挂起状态
            repositoryService.suspendProcessDefinitionById(processDefinitionId, true, null);
            System.out.println("将状态修改为了挂起");
        }
    }

    /**
     * 添加业务Key到Activiti中
     * 如有一张请假申请表，表中记录详细的请假信息。需要将此表的记录整合到流程中
     * 关键字：BusinessKey
     */
    @Test
    public void addBusinessKey() {
//        1.获取任务Service
        RuntimeService runtimeService = processEngine.getRuntimeService();
//        2.启动流程实例
//        2.1第一个参数为流程定义的Key
//        2.2第二个参数为BusinessKey
        ProcessInstance instance = runtimeService.startProcessInstanceByKey("EvectionProcess", "1001");
    }

    /**
     * Description:查询历史活动操作信息
     *
     * @date: 2020/12/6 21:22
     */
    @Test
    public void historyQuery() {
//        1.获取流程历史Service：HistoryService
        HistoryService historyService = processEngine.getHistoryService();
//        2.根据流程实例ID进行查询
//        2.1要查询的是act_hi_actinst表，此表对应的是【活动实例表】=》【activity instance】
//        2.2根据2.1，要创建 活动实例 查询
//        2.3根据流程实例开始时间进行升序排序
        List<HistoricActivityInstance> historicActivityInstanceList = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId("2501")
                .orderByHistoricActivityInstanceStartTime().asc()
                .list();
//        3.打印信息
        for (HistoricActivityInstance hi : historicActivityInstanceList) {
            System.out.println(hi.getActivityId());
            System.out.println(hi.getActivityName());
            System.out.println(hi.getProcessDefinitionId());
            System.out.println(hi.getProcessInstanceId());
            System.out.println(hi.getStartTime());
            System.out.println(hi.getEndTime());
            System.out.println("<--↓↓↓-->");
        }
    }

    /**
     * 下载bpmn文件与png文件
     */
    @Test
    public void downloadResource() throws IOException {
//        1.获取流程定义相关的Service：RepositoryService
        RepositoryService repositoryService = processEngine.getRepositoryService();
//        2.查询流程部署ID
        ProcessDefinition result = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("EvectionProcess")
                .singleResult();
        String deploymentId = result.getDeploymentId();
//        3.读取资源信息
//        3.1读取bpmn资源
        String bpmnName = result.getResourceName();
        InputStream bpmnIs = repositoryService.getResourceAsStream(deploymentId, bpmnName);
//        3.2读取png资源
        String pngName = result.getDiagramResourceName();
        InputStream pngIs = repositoryService.getResourceAsStream(deploymentId, pngName);
//        4.构造输出流
        FileOutputStream bpmnFos = new FileOutputStream(new File("C:\\LEN\\evection.bpmn"));
        FileOutputStream pngFos = new FileOutputStream(new File("C:\\LEN\\evection.png"));
//        5.输入流与输出流的转换
        IOUtils.copy(bpmnIs, bpmnFos);
        IOUtils.copy(pngIs, pngFos);
//        6.关闭流
        bpmnIs.close();
        bpmnFos.close();
        pngIs.close();
        pngFos.close();
    }

    /**
     * 删除流程部署（有未完成的流程，）
     * 原理：开启级联删除
     */
    @Test
    public void deleteProcessDeployment02() {
        RepositoryService repositoryService = processEngine.getRepositoryService();
        String deploymentId = "";
        repositoryService.deleteDeployment(deploymentId, true);
    }

    /**
     * 删除流程部署
     */
    @Test
    public void deleteProcessDeployment() {
//        1.获取流程定义Service：RepositoryService
        RepositoryService repositoryService = processEngine.getRepositoryService();
//        2.通过流程部署id来删除流程部署信息
        String deploymentId = "1";
        repositoryService.deleteDeployment(deploymentId);

    }

    /**
     * 查询流程定义的信息
     */
    @Test
    public void queryProcessDefinition() {
//        1.获取流程定义相关的Service： RepositoryService
        RepositoryService repositoryService = processEngine.getRepositoryService();
//        2.创建一个ProcessDefinitionQuery对象
        ProcessDefinitionQuery query = repositoryService.createProcessDefinitionQuery();
//        3.查询流程定义的信息
//        3.1参数为流程定义的Key
//        3.2根据version进行排序
//        3.3倒叙，最新的版本排在前面
        List<ProcessDefinition> processDefinitionList = query.processDefinitionKey("EvectionProcess")
                .orderByProcessDefinitionVersion()
                .desc()
                .list();
//        4.打印信息
        for (ProcessDefinition definition : processDefinitionList) {
            System.out.println("流程定义id：" + definition.getId());
            System.out.println("流程定义名称：" + definition.getName());
            System.out.println("流程定义的Key：" + definition.getKey());
            System.out.println("流程定义版本：" + definition.getVersion());
        }
    }
}
