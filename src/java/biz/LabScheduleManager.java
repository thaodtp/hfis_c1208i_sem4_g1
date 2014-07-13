/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biz;

import da.AccountJpaController;
import da.LabScheduleJpaController;
import da.exceptions.RollbackFailureException;
import entity.Lab;
import entity.LabSchedule;
import java.util.Date;
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
 * @author The
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class LabScheduleManager {

    @PersistenceUnit(unitName = "eAdministrationPU")
    private EntityManagerFactory em;

    @Resource
    private UserTransaction utx;

    private LabScheduleJpaController daController;

    public LabScheduleJpaController getDaController() {
        if (daController == null) {
            daController = new LabScheduleJpaController(utx, em);
        }
        return daController;
    }
    public List<LabSchedule> getUnacceptedSchedule(){
        return getDaController().getUnacceptedSchedule();
    }
    public void acceptLabRequest(LabSchedule schedule){
        try {
            schedule.setStatus(LabSchedule.STATUS_ACCEPTED);
            getDaController().edit(schedule);
        } catch (RollbackFailureException ex) {
            Logger.getLogger(LabScheduleManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(LabScheduleManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void requestLab(LabSchedule schedule){
        try {
            schedule.setStatus(0);
            getDaController().create(schedule);
        } catch (RollbackFailureException ex) {
            Logger.getLogger(LabScheduleManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(LabScheduleManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public LabSchedule getSchedule(Date date, int slot, Lab lab) {
        return getDaController().getSchedule(date, slot, lab);
    }
}
