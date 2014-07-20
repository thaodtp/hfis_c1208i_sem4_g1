/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import biz.LabManager;
import biz.LabScheduleManager;
import entity.Account;
import entity.Lab;
import entity.LabSchedule;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.ValueChangeEvent;

/**
 *
 * @author The
 */
@ManagedBean
@ViewScoped
public class LabScheduleUI implements Serializable {

    @EJB
    private LabScheduleManager scheduleManager;
    @ManagedProperty(value="#{login}")
    private Login loginBean;
    private Account acc;
    private String date;
    private List<Integer> slots;
    private LabSchedule curSchedule;
    private Lab lab;
    private int slot;
    private String detail;
    /**
     * Creates a new instance of LabScheduleUI
     */
    public LabScheduleUI() {
        this.slots = new ArrayList<>();
        slots.add(1);
        slots.add(2);
        slots.add(3);
        slots.add(4);
        slots.add(5);
        this.date = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
    }

   
    public boolean isValidDay() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 3);
        try {
            return new SimpleDateFormat("dd/MM/yyyy").parse(this.date).after(cal.getTime());
        } catch (ParseException ex) {
            Logger.getLogger(LabScheduleUI.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public List<LabSchedule> getUnacceptedSchedule() {
        return scheduleManager.getUnacceptedSchedule();
    }
    public List<LabSchedule> getAllSchedule(){
        return scheduleManager.getAllSchedule();
    }
    public void denyLabRequest() {
        scheduleManager.denyLabRequest(curSchedule);
        curSchedule = null;
    }

    public void acceptLabRequest() {
        scheduleManager.acceptLabRequest(curSchedule);
        curSchedule = null;
    }

    public void requestLab() {
        LabSchedule ls = new LabSchedule();
        ls.setLabId(lab);
        try {
            ls.setDate(new SimpleDateFormat("dd/MM/yyyy").parse(date));
        } catch (ParseException ex) {
            Logger.getLogger(LabScheduleUI.class.getName()).log(Level.SEVERE, null, ex);
            ls.setDate(new Date());
        }
        ls.setSlot(slot);
        ls.setDetail(detail);
        ls.setRequestAccount(loginBean.getAccount());
        scheduleManager.requestLab(ls);
        lab = null;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Lab getLab() {
        return lab;
    }

    public void setLab(Lab lab) {
        this.lab = lab;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }
    
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public LabSchedule getSchedule(Lab lab, int slot) {
        try {
            curSchedule = scheduleManager.getSchedule(new SimpleDateFormat("dd/MM/yyyy").parse(date), slot, lab);
            return curSchedule;
        } catch (ParseException ex) {
            Logger.getLogger(LabScheduleUI.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public LabSchedule getCurSchedule() {
        return curSchedule;
    }

    public void setCurSchedule(LabSchedule curSchedule) {
        this.curSchedule = curSchedule;
    }


    public List<Integer> getSlots() {
        return slots;
    }

    public Login getLoginBean() {
        return loginBean;
    }

    public void setLoginBean(Login loginBean) {
        this.loginBean = loginBean;
    }

    public Account getAcc() {
        return acc;
    }

    public void setAcc(Account acc) {
        this.acc = acc;
    }
    
}
