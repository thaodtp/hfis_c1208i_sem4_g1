/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import biz.LabManager;
import entity.Lab;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author JUN
 */
@ManagedBean
@FacesConverter(value = "labConverter")
public class LabConverter implements Converter {
    @EJB
    private LabManager labManager; 
    /**
     * Creates a new instance of LabConverter
     */
    public LabConverter() {
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        return labManager.getLabById(Integer.parseInt(value));
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return ((Lab)value).getId()+"";
    }

}
