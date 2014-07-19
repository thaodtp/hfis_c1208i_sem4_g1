/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ui;

import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author JUN
 */
@ManagedBean
@FacesConverter("dateTimeConverter")
public class DateTimeConverter extends javax.faces.convert.DateTimeConverter{

    /**
     * Creates a new instance of DateTimeConverter
     */
    public DateTimeConverter() {
        setPattern("MM/dd/yyyy");
    }
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value != null && value.length() != getPattern().length()) {
            throw new ConverterException("Invalid format");
        }
        return super.getAsObject(context, component, value);
    }
}
