/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ui;

import biz.AccountManager;
import entity.Account;
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
@FacesConverter(value = "accountConverter")
public class AccountConverter implements Converter{

    @EJB
    private AccountManager manager;
    /**
     * Creates a new instance of AccountConverter
     */
    public AccountConverter() {
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        return manager.getAccount(value);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return ((Account)value).getUsername();
    }
    
}
