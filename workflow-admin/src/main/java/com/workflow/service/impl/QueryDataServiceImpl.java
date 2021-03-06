package com.workflow.service.impl;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.workflow.common.enumerate.AgentStatus;
import com.workflow.common.enumerate.NodeTpye;
import com.workflow.common.uuid.SnowflakeIdGenerator;
import com.workflow.exception.ActivityException;
import com.workflow.mapper.ActAgentingMapper;
import com.workflow.mapper.ActDeploymentMapper;
import com.workflow.mapper.ActExecutionMapper;
import com.workflow.mapper.ActExecutionTaskMapper;
import com.workflow.model.*;
import com.workflow.service.QueryDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Create by wl on 2020/11/2
 */
@Service
public class QueryDataServiceImpl implements QueryDataService {

    private static final SnowflakeIdGenerator sfIdGenerator = new SnowflakeIdGenerator();

    private static final  Log logger= LogFactory.get(QueryDataServiceImpl.class);

    @Autowired
    ActDeploymentMapper actDeploymentMapper;

    @Autowired
    ActAgentingMapper actAgentingMapper;

    @Autowired
    ActExecutionTaskMapper actExecutionTaskMapper;

    @Autowired
    ActExecutionMapper actExecutionMapper;

    @Override
    public List<ActDeployment> queryDeplymentList() throws Exception {
        ActDeploymentExample example =new ActDeploymentExample();
        example.createCriteria().getAllCriteria();
        return actDeploymentMapper.selectByExampleWithBLOBs(example);
    }

    @Override
    public void updateDeployment(ActDeployment deployid) throws Exception {
        ActDeployment actDeployment = actDeploymentMapper.selectByPrimaryKey(deployid.getId());
        if (actDeployment==null) {
            logger.error(new ActivityException("根据流程定义id无法查询对应数据！"));
            throw new ActivityException("根据流程定义id无法查询对应数据！");
        }else
            actDeploymentMapper.updateByPrimaryKey(deployid);

    }

    @Override
    public void addDeployment(ActDeployment actDeployment) throws Exception {
        actDeployment.setId(sfIdGenerator.nextId());
        actDeploymentMapper.insertSelective(actDeployment);
    }

    @Override
    public void deleteDeployment(Long deployid) throws Exception {
        ActDeployment actDeployment = actDeploymentMapper.selectByPrimaryKey(deployid);
        if (actDeployment==null) {
            logger.error(new ActivityException("根据流程定义id无法查询对应数据！"));
            throw new ActivityException("根据流程定义id无法查询对应数据！");
        }else
            actDeploymentMapper.deleteByPrimaryKey(deployid);
    }

    @Override
    public List<Map> queryTaskLists(String userid, String flag) throws Exception {
        if (!StringUtils.isEmpty(userid) && !StringUtils.isEmpty(flag)) {
            if(userid.equals("sa")){
                List<Map> actAgentings = actAgentingMapper.queryAgentingAllLists(flag);
                return actAgentings;
            }
            List<Map> actAgentings = actAgentingMapper.queryAgentingLists(userid, flag);
            return actAgentings;
        }
        return null;
    }

    @Override
    public List<Map> queryExectionNodes(String agentingid) {
        List<Map> nodes=new ArrayList<>();
        if (!StringUtils.isEmpty(agentingid)) {
            ActAgenting actAgenting = actAgentingMapper.selectByPrimaryKey(Long.parseLong(agentingid));
            ActExecutionTaskExample actExecutionTaskExample=new ActExecutionTaskExample();
            actExecutionTaskExample.setOrderByClause("id");
            actExecutionTaskExample.createCriteria().andExecutionidEqualTo(actAgenting.getTaskid())
                    .andNodetypeNotEqualTo(NodeTpye.ROUTE.getType());
            List<ActExecutionTask> actExecutionTasks = actExecutionTaskMapper.selectByExample(actExecutionTaskExample);
            ActExecution execution = actExecutionMapper.selectByPrimaryKey(actAgenting.getTaskid());
            putStartNode(nodes,execution);
            for (int i = 0; i < actExecutionTasks.size(); i++) {
                Map<String,Object> m=new HashMap<>();
                List<Map> actlists=new ArrayList<>();
                ActAgentingExample actAgentingExample=new ActAgentingExample();
                actAgentingExample.createCriteria().andTaskidEqualTo(actExecutionTasks.get(i).getExecutionid())
                        .andNownodeidEqualTo(actExecutionTasks.get(i).getDeploymentdetialid());
                List<ActAgenting> actAgentings = actAgentingMapper.selectByExample(actAgentingExample);
                ActExecutionTask tasks=actExecutionTasks.get(i);
                m.put("nodeName",tasks.getNodename());
                m.put("nodeStatus",tasks.getNodestatus());
                m.put("nodeType",tasks.getNodetype());
                m.put("sendTime",tasks.getSendtime());
                for (int j = 0; j <actAgentings.size() ; j++) {
                    ActAgenting agenting=actAgentings.get(j);
                    Map<String,Object> amap=new HashMap<>();
                    amap.put("userid",agenting.getUseid());
                    amap.put("agentingStatus",agenting.getAgentingstatus());
                    amap.put("agentingSign",agenting.getAgentingsign());
                    amap.put("startTime",agenting.getStarttime());
                    amap.put("endTime",agenting.getEndtime());
                    amap.put("suggestStr",agenting.getSuggeststr());
                    actlists.add(amap);
                }
                m.put("nodeDetails",actlists);
                nodes.add(m);
            }
        }
        return nodes;
    }

    //加入发起人信息
    private void putStartNode(List<Map> nodes, ActExecution execution) {
        Map rootMap=new HashMap();
        Map rootUsers=new HashMap();
        List<Map> rootlist=new ArrayList<>();
        rootMap.put("nodeName","发起申请");
        rootMap.put("nodeStatus",1);
        rootMap.put("nodeType",0);
        rootMap.put("sendTime",execution.getStarttime());
        rootUsers.put("userid",execution.getUserid());
        rootUsers.put("startTime",execution.getStarttime());
        rootUsers.put("endTime",execution.getStarttime());
        rootUsers.put("agentingStatus",1);
        rootlist.add(rootUsers);
        rootMap.put("nodeDetails",rootlist);
        nodes.add(rootMap);
    }

    @Override
    public int queryTaskStatus(String agentingid) {
        if (!StringUtils.isEmpty(agentingid)) {
            ActAgenting actAgenting = actAgentingMapper.selectByPrimaryKey(Long.parseLong(agentingid));
            ActExecution execution = actExecutionMapper.selectByPrimaryKey(actAgenting.getTaskid());
            return execution.getStatus();
        }else{
            logger.error(new ActivityException("agentingid为空，请检查!"));
            throw new ActivityException("agentingid为空，请检查!");
        }
    }

    @Override
    public List<ActExecutionTask> queryCanRejectNode(String agentingid) throws Exception {
        ActAgenting actAgenting = actAgentingMapper.selectByPrimaryKey(Long.parseLong(agentingid));
        Long taskid = actAgenting.getTaskid();
        ActExecutionTaskExample actExecutionTaskExample=new ActExecutionTaskExample();
        actExecutionTaskExample.createCriteria().andExecutionidEqualTo(taskid).andNodetypeEqualTo(NodeTpye.APPROVAL.getType());
        List<ActExecutionTask> tasks = actExecutionTaskMapper.selectByExample(actExecutionTaskExample);
        return tasks;
    }

    @Override
    public List<ActExecution> querySponsorTasks(String userid) throws Exception {
        //sa查询所有
        if (userid.equals("sa")) {
            ActExecutionExample actExecutionExample=new ActExecutionExample();
            actExecutionExample.createCriteria();
            List<ActExecution> actExecutions = actExecutionMapper.selectByExample(actExecutionExample);
            return actExecutions;
        }
        ActExecutionExample actExecutionExample=new ActExecutionExample();
        actExecutionExample.createCriteria().andUseridEqualTo(userid);
        List<ActExecution> actExecutions = actExecutionMapper.selectByExample(actExecutionExample);
        return actExecutions;
    }

    @Override
    public List<Map> queryDetailByTaskid(String taskid) throws Exception {
        {
            List<Map> nodes=new ArrayList<>();
//            if (!StringUtils.isEmpty(taskid)) {
//                ActAgenting actAgenting = actAgentingMapper.selectByPrimaryKey(Long.parseLong(agentingid));
                ActExecutionTaskExample actExecutionTaskExample=new ActExecutionTaskExample();
                actExecutionTaskExample.setOrderByClause("id");
                actExecutionTaskExample.createCriteria().andExecutionidEqualTo(Long.parseLong(taskid))
                        .andNodetypeNotEqualTo(NodeTpye.ROUTE.getType());
                List<ActExecutionTask> actExecutionTasks = actExecutionTaskMapper.selectByExample(actExecutionTaskExample);
                ActExecution execution = actExecutionMapper.selectByPrimaryKey(Long.parseLong(taskid));
                putStartNode(nodes,execution);
                for (int i = 0; i < actExecutionTasks.size(); i++) {
                    Map<String,Object> m=new HashMap<>();
                    List<Map> actlists=new ArrayList<>();
                    ActAgentingExample actAgentingExample=new ActAgentingExample();
                    actAgentingExample.createCriteria().andTaskidEqualTo(actExecutionTasks.get(i).getExecutionid())
                            .andNownodeidEqualTo(actExecutionTasks.get(i).getDeploymentdetialid());
                    List<ActAgenting> actAgentings = actAgentingMapper.selectByExample(actAgentingExample);
                    ActExecutionTask tasks=actExecutionTasks.get(i);
                    m.put("nodeName",tasks.getNodename());
                    m.put("nodeStatus",tasks.getNodestatus());
                    m.put("nodeType",tasks.getNodetype());
                    m.put("sendTime",tasks.getSendtime());
                    for (int j = 0; j <actAgentings.size() ; j++) {
                        ActAgenting agenting=actAgentings.get(j);
                        Map<String,Object> amap=new HashMap<>();
                        amap.put("userid",agenting.getUseid());
                        amap.put("agentingStatus",agenting.getAgentingstatus());
                        amap.put("agentingSign",agenting.getAgentingsign());
                        amap.put("startTime",agenting.getStarttime());
                        amap.put("endTime",agenting.getEndtime());
                        amap.put("suggestStr",agenting.getSuggeststr());
                        actlists.add(amap);
                    }
                    m.put("nodeDetails",actlists);
                    nodes.add(m);
                }
//            }
            return nodes;
        }
    }
}
