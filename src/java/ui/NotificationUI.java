/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ui;

import biz.RequestManager;
import biz.ScheduleManager;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
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
    /**
     * Creates a new instance of NotificationUI
     */
    public NotificationUI() {
    }
    
}
