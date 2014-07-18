/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import biz.LabManager;
import entity.Lab;
import entity.LabSchedule;
import entity.Request;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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
public class LabUI implements Serializable {

    @EJB
    private LabManager labManager;
    private Lab lab;
    private List<Lab> labs;
    private int id;
    private String name;
    private int type;
    private int status;
    private String msg;
    private Map<String, Integer> typeS = new LinkedHashMap<String, Integer>();

    /**
     * Creates a new instance of LabUI
     */
    public LabUI() {
        typeS.put("Lab Room", 1);
        typeS.put("Server Room", 2);
    }

    public Map<String, Integer> getLabUsageStatistic() {
        Map<String, Integer> result = new TreeMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("\"dd/MM/yyyy\"");
        List<LabSchedule> schedules = new LinkedList<>();
        for(Lab l:labManager.displayLabs()){
            schedules.addAll(l.getLabScheduleList());
        }
        for (Iterator<LabSchedule> it = schedules.iterator(); it.hasNext();) {
            LabSchedule req = it.next();
            String date = sdf.format(req.getDate());
            if (result.containsKey(date)) {
                result.put(date, result.get(date) + 1);
            } else {
                if (result.size() > 10) {
                    break;
                } else {
                    result.put(date, 1);
                }
            }
        }
        return result;
    }
    
    public Map<String, Integer> getTypeS() {
        return typeS;
    }

    public List<Lab> getAllLabs() {
        return labManager.getAllLabs();
    }

    public List<Lab> getLabs() {
        return labManager.displayLabs();
    }

    public List<Lab> getRoomByType() {
        return labManager.getRoomByType(type);
    }

    public List<Lab> getServerRoom() {
        return labManager.displayServerRoom();
    }

    public String create() {
        try {
            labs = labManager.getAllLabs();
            if (name.equals("")) {
                msg = " Please enter lab's name!!!";
                return null;
            }
            for (Lab lab : labs) {
                if (lab.getName().toLowerCase().equals(name.toLowerCase())) {
                    msg = " Lab is exist";
                    return "";
                }
            }
            labManager.create(new Lab(name, type, 0));
            return "/admin/success.xhtml";
        } catch (Exception ex) {
            msg = "Can't add this room";
            Logger.getLogger(LabUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
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
            return "/admin/success.xhtml?faces-redirect=true";
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
