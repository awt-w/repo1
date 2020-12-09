import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Description:
 * date: 2020/12/7 11:19
 *
 * @author Lele
 */
public class ActivitiDemoDay03 {
    private ProcessEngine processEngine;
    @Before
    public void getProcessEngine(){
        processEngine= ProcessEngines.getDefaultProcessEngine();
    }

    /**
     * 启动任务
     */
    @Test
    public void startTask(){
//        1.获取运行时Service
        RuntimeService runtimeService = processEngine.getRuntimeService();
//        2.创建一个与uel表达式对应值的Map
        Map<String,Object> assigneeMap=new HashMap<>();
        assigneeMap.put("assignee0","A请假");
        assigneeMap.put("assignee1","B经理");
        assigneeMap.put("assignee2","C财务");
//        3.带上Map，启动流程
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("evection-uel", assigneeMap);
    }

    /**
     * 部署evection-uel
     */
    @Test
    public void deployment(){
//        1.获取repositoryService
        RepositoryService repositoryService = processEngine.getRepositoryService();
//        2.部署
        Deployment deploy = repositoryService.createDeployment().name("出差申请-uel")
                .addClasspathResource("BPMN/evection-uel.bpmn")
                .deploy();
        System.out.println("部署ID+" + deploy.getId());
    }
}
