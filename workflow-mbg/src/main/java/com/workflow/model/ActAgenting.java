package com.workflow.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;

public class ActAgenting implements Serializable {
    private Long id;

    private Long taskid;

    private String useid;

    private Integer agentingstatus;

    private Integer agentingsign;

    private Date starttime;

    private Date endtime;

    private Long nownodeid;

    private Long nodeversion;

    private String suggeststr;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTaskid() {
        return taskid;
    }

    public void setTaskid(Long taskid) {
        this.taskid = taskid;
    }

    public String getUseid() {
        return useid;
    }

    public void setUseid(String useid) {
        this.useid = useid;
    }

    public Integer getAgentingstatus() {
        return agentingstatus;
    }

    public void setAgentingstatus(Integer agentingstatus) {
        this.agentingstatus = agentingstatus;
    }

    public Integer getAgentingsign() {
        return agentingsign;
    }

    public void setAgentingsign(Integer agentingsign) {
        this.agentingsign = agentingsign;
    }

    public Date getStarttime() {
        return starttime;
    }

    public void setStarttime(Date starttime) {
        this.starttime = starttime;
    }

    public Date getEndtime() {
        return endtime;
    }

    public void setEndtime(Date endtime) {
        this.endtime = endtime;
    }

    public Long getNownodeid() {
        return nownodeid;
    }

    public void setNownodeid(Long nownodeid) {
        this.nownodeid = nownodeid;
    }

    public Long getNodeversion() {
        return nodeversion;
    }

    public void setNodeversion(Long nodeversion) {
        this.nodeversion = nodeversion;
    }

    public String getSuggeststr() {
        return suggeststr;
    }

    public void setSuggeststr(String suggeststr) {
        this.suggeststr = suggeststr;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", taskid=").append(taskid);
        sb.append(", useid=").append(useid);
        sb.append(", agentingstatus=").append(agentingstatus);
        sb.append(", agentingsign=").append(agentingsign);
        sb.append(", starttime=").append(starttime);
        sb.append(", endtime=").append(endtime);
        sb.append(", nownodeid=").append(nownodeid);
        sb.append(", nodeversion=").append(nodeversion);
        sb.append(", suggeststr=").append(suggeststr);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}