/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import biz.AccountManager;
import biz.DepartmentManager;
import biz.LabManager;
import biz.LabScheduleManager;
import biz.RequestManager;
import biz.ScheduleManager;
import entity.Account;
import entity.Department;
import entity.Lab;
import entity.LabSchedule;
import entity.ScheduleSequence;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
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
    private AccountManager accountManager;
    @EJB
    private DepartmentManager departmentManager;
    @EJB
    private RequestManager requestManager;
    @EJB
    private ScheduleManager scheduleManager;
    @EJB
    private LabScheduleManager labScheduleManager;
    @EJB
    private LabManager labManager;
    @ManagedProperty(value = "#{login}")
    private Login loginBean;
    private String date;
    private List<Integer> slots;
    private ScheduleSequence curSchedule;
    private LabSchedule currentLabSchedule;
    private Lab lab;
    private int slot;
    private String detail;
    private List<LabSchedule> addPreparation;
    private Map<String, List<Lab>> freeLabPreparation;
    private String msg;
    private Department departmentId;
    private int id;

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

    public List<ScheduleSequence> getUnacceptedSchedule() {
        return scheduleManager.getUnacceptedSchedule();
    }

    public List<ScheduleSequence> getAllSchedule() {
        return scheduleManager.getAllSchedule();
    }
    
    public List<ScheduleSequence> getAllScheduleOfOneDepartment() {
        return scheduleManager.getAllScheduleOfOneDepartment(departmentId);
    }

    public void denyLabRequest() {
        scheduleManager.denyLabRequest(curSchedule);
        curSchedule = null;
    }

    public void acceptLabRequest() {
        for (LabSchedule schedule : curSchedule.getLabScheduleList()) {
            if (schedule.getDate().before(new Date())) {
                schedule.setLabId(null);
            }
        }
        scheduleManager.acceptLabRequest(curSchedule);
        requestManager.sendMessage(curSchedule.getRequestAccount(), "Your lab request has been approved");
        curSchedule = null;
    }

    public List<Lab> getFreeLab(Date date, int slot) {
        List<Lab> allLabs = labManager.displayLabs();
        List<LabSchedule> schedules = labScheduleManager.getScheduleByDS(date, slot);
        for (LabSchedule ls : schedules) {
            if (allLabs.contains(ls.getLabId())) {
                allLabs.remove(ls.getLabId());
            }
        }
        return allLabs;
    }

    public void checkLab() {
        addPreparation = new LinkedList();
        freeLabPreparation = new HashMap<>();
        String[] dates = date.split(",");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 2);
        for (String d : dates) {
            try {
                LabSchedule ls = new LabSchedule();
                Date dd = new SimpleDateFormat("dd/MM/yyyy").parse(d);
                List<Lab> freeLabs = getFreeLab(dd, slot);
//                if (freeLabs.isEmpty()) {
//                    ls.setLabId(null);
//                } else {
//                    ls.setLabId(freeLabs.get(0));
//                }
                if (cal.getTime().after(dd)) {
                    ls.setSlot(-1);
                } else {
                    ls.setSlot(slot);
                }
                ls.setDate(dd);
                addPreparation.add(ls);
                freeLabPreparation.put(d, freeLabs);
//                ls.setSequenceId(ss);
//                labScheduleManager.requestLab(ls);
            } catch (ParseException ex) {
            }
        }
        if (addPreparation.isEmpty()) {
            msg = "You haven't choose any day to check";
            addPreparation = null;
            freeLabPreparation = null;
        }
    }

    public void requestLab() {
        try {
            ScheduleSequence ss = new ScheduleSequence();
            ss.setDetail(detail);
            ss.setRequestAccount(loginBean.getAccount());
            scheduleManager.requestLabSequence(ss);
            for (LabSchedule ls : addPreparation) {
                if (ls.getLabId() != null && ls.getSlot() != -1) {
                    ls.setSequenceId(ss);
                    labScheduleManager.requestLab(ls);
                }
            }
            msg = "Your request've been sent";
            detail = "";
            slot = 0;
            lab = null;
            addPreparation = null;
            freeLabPreparation = null;
        } catch (Exception ex) {
            msg = "System error";
        }
    }

    public String getMessage() {
        String t = msg;
        msg = "";
        return t;
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
            currentLabSchedule = labScheduleManager.getSchedule(new SimpleDateFormat("dd/MM/yyyy").parse(date), slot, lab);
            return currentLabSchedule;
        } catch (ParseException ex) {
            Logger.getLogger(LabScheduleUI.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public ScheduleSequence getCurSchedule() {
        return curSchedule;
    }

    public void setCurSchedule(ScheduleSequence curSchedule) {
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

    public LabSchedule getCurrentLabSchedule() {
        return currentLabSchedule;
    }

    public void setCurrentLabSchedule(LabSchedule currentLabSchedule) {
        this.currentLabSchedule = currentLabSchedule;
    }

    public List<LabSchedule> getAddPreparation() {
        return addPreparation;
    }

    public void setAddPreparation(List<LabSchedule> addPreparation) {
        this.addPreparation = addPreparation;
    }

    public List<Lab> getFreeLabPreparation(String date) {
        return freeLabPreparation.get(date);
    }

    public Department getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
    //    this.departmentId = departmentId;
        Account target = accountManager.getAccountByUsername("bnguyen");
        if (target != null) {
            this.departmentId = target.getDepartmentId();
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        
    }
    
    
    

}
