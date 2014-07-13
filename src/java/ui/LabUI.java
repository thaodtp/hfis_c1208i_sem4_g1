/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import biz.LabManager;
import entity.Lab;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.view.ViewScoped;

/**
 *
 * @author The
 */
@ManagedBean
@ViewScoped
public class LabUI {

    @EJB
    private LabManager labManager;

    /**
     * Creates a new instance of LabUI
     */
    public LabUI() {
    }

    public List<Lab> getLabs() {
        return labManager.getLabs();
    }
}
