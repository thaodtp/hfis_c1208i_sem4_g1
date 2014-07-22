/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import biz.RequestManager;
import biz.ScheduleManager;
import entity.Request;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

/**
 *
 * @author The
 */
@ManagedBean
@RequestScoped
public class NotificationUI {

    @EJB
    private ScheduleManager scheduleManager;
    @EJB
    private RequestManager requestManager;
    @ManagedProperty(value = "#{login}")
    private Login loginBean;

    /**
     * Creates a new instance of NotificationUI
     */
    public NotificationUI() {
    }

    public int getTotalNoti() {
        if (loginBean.getRoleAdmin()) {
            return getUnassignComplaintsCount() + getUnresolvedLabRequestsCount() + getMessages().size();
        } else {
            return 0;
        }
    }

    public int getUnresolvedLabRequestsCount() {
        return scheduleManager.getUnacceptedSchedule().size();
    }

    public int getUnassignComplaintsCount() {
        return requestManager.getUnassignedComplaints().size();
    }

    public List<Request> getMessages() {
        return requestManager.getMessages(loginBean.getUsername());
    }

    public Login getLoginBean() {
        return loginBean;
    }

    public void setLoginBean(Login loginBean) {
        this.loginBean = loginBean;
    }

}
