/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ui;

import biz.DepartmentManager;
import entity.Department;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author The
 */
@ManagedBean
@FacesConverter(value = "departmentConverter")
public class DepartmentConverter implements Converter{
    @EJB
    private DepartmentManager manager;
    /**
     * Creates a new instance of DepartmentConverter
     */
    public DepartmentConverter() {
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        return manager.getDepartmentById(Integer.parseInt(value));
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return ((Department)value).getId()+"";
    }
    
}
