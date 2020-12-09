package com.activiti.demo.listener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

/**
 * Description:
 * date: 2020/12/7 11:49
 *
 * @author Lele
 */
public class EvectionListener implements TaskListener {

    @Override
    public void notify(DelegateTask delegateTask) {
//        1.判断任务的名称
//        2.判断时间的名称
        System.out.println("当前事件" + delegateTask.getEventName());
        if ("创建出差申请".equals(delegateTask.getName())
                && "create".equals(delegateTask.getEventName())) {
            delegateTask.setAssignee("listener请假");
        }
    }
}
