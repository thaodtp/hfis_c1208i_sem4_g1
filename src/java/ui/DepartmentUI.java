/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ui;

import biz.DepartmentManager;
import entity.Department;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.view.ViewScoped;

/**
 *
 * @author Thao
 */
@ManagedBean
@ViewScoped
public class DepartmentUI {
    
    @EJB
    private DepartmentManager departmentManager;

    private List<Department> departments;
    private String name;
    private int id;
    private String msg;
    public List<Department> getDepartments(){
       return departmentManager.getDepartments();
    }
    
    public String deleteDepartment(){
        try {
            departmentManager.delete(id);
            return "/success.xhtml?faces-redirect=true";
        } catch (Exception ex) {
            msg = "Can't delete department";
            Logger.getLogger(DepartmentUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
    public void setDepartments(List<Department> departments) {
        this.departments = departments;
    }
    
    public List<String> getDepNames(){
        return departmentManager.getDepNames();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
    
    

   
    
    
            
    
    
    
    
}
