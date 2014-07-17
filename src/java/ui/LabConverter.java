/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ui;

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
public class LabConverter  implements Converter{

    /**
     * Creates a new instance of LabConverter
     */
    public LabConverter()  {
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
         return value;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if(value.equals(1))return "Unavailable";
        else {
            return "Available";
        }
    }  

}
