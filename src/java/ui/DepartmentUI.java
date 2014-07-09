/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ui;

import biz.DepartmentManager;
import entity.Department;

import java.util.List;
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
    
    public List<Department> getDepartments(){
       return departmentManager.getAllDepartment();
    }
    

    public void setDepartments(List<Department> departments) {
        this.departments = departments;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    

   
    
    
            
    
    
    
    
}
