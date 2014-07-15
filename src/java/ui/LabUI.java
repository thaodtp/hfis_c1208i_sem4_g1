/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import biz.LabManager;
import entity.Lab;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author JUN
 */
@ManagedBean
@ViewScoped
public class LabUI implements Serializable{
    @EJB
    private LabManager labManager;
    private Lab lab;
    private int id;
    private String name;
    private int type;
    private int status;
    private String msg;

    /**
     * Creates a new instance of LabUI
     */
    public LabUI() {
    }

    public List<Lab> getAllLabs() {
        return labManager.getAllLabs();
    }

    public List<Lab> getLabs() {
        return labManager.displayLabs();
    }

    public List<Lab> getServerRoom() {
        return labManager.displayServerRoom();
    }
    public boolean getCheckedValue() {
        if (labManager.getAllLabs().get(0).getStatus()==1){
            return true;
        } else {
            return false;
        }
    }

    public String editLab() {
        try {
            lab.setId(id);
            lab.setName(name);
            lab.setType(type);
            lab.setStatus(status);
            labManager.edit(lab);
            return "/success.xhtml";
        } catch (Exception ex) {
            lab.setStatus(status);
            msg = "Can't update Lab";
            Logger.getLogger(LabUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String deleteLab() {
        try {
            labManager.delete(id);
            return "/success.xhtml?faces-redirect=true";
        } catch (Exception ex) {
            msg = "Can't delete lab";
            Logger.getLogger(LabUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return labManager.getAllLabs().get(0).getStatus();
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
