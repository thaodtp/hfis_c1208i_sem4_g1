/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biz;

import da.LabJpaController;
import da.exceptions.RollbackFailureException;
import entity.Lab;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.transaction.UserTransaction;

/**
 *
 * @author JUN
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class LabManager {

    @PersistenceUnit(unitName = "eAdministrationPU")
    private EntityManagerFactory em;
    @Resource
    private UserTransaction utx;
    private LabJpaController daController;

    public LabJpaController getDaController() {
        if (daController == null) {
            daController = new LabJpaController(utx, em);
        }
        return daController;
    }

    public void create(Lab lab) {
        try {
            getDaController().create(lab);
        } catch (RollbackFailureException ex) {
            Logger.getLogger(LabManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(LabManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void delete(int id) {
        try {
            getDaController().destroy(id);
        } catch (RollbackFailureException ex) {
            Logger.getLogger(LabManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(LabManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void edit(Lab lab) {
        try {
            getDaController().edit(lab);
        } catch (RollbackFailureException ex) {
            Logger.getLogger(DepartmentManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(DepartmentManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void edit(int id, String name, int type, int status) {
        try {
            Lab lab = getLabById(id);
            lab.setName(name);
            lab.setType(type);
            lab.setStatus(status);
            getDaController().edit(lab);
        } catch (RollbackFailureException ex) {
            Logger.getLogger(LabManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(LabManager.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public Lab getLabById(int id) {
        return getDaController().getLabById(id);
    }

    public List<Lab> getAllLabs() {
        return getDaController().getAllLabs();
    }

    public List<Lab> displayLabs() {
        return getDaController().displayLabs();
    }

    public List<Lab> displayServerRoom() {
        return getDaController().displayServerRoom();
    }

    public List<Lab> getRoomByType(int type) {
        return getDaController().getRoomByType(type);
    }
}
