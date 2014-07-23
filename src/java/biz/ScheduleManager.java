/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biz;

import da.LabScheduleJpaController;
import da.ScheduleSequenceJpaController;
import da.exceptions.RollbackFailureException;
import entity.Department;
import entity.Lab;
import entity.LabSchedule;
import entity.ScheduleSequence;
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
public class ScheduleManager {

    @PersistenceUnit(unitName = "eAdministrationPU")
    private EntityManagerFactory em;

    @Resource
    private UserTransaction utx;

    private ScheduleSequenceJpaController daController;

    public ScheduleSequenceJpaController getDaController() {
        if (daController == null) {
            daController = new ScheduleSequenceJpaController(utx, em);
        }
        return daController;
    }

    public List<ScheduleSequence> getUnacceptedSchedule() {
        return getDaController().getUnacceptedSchedule();
    }

    public void denyLabRequest(ScheduleSequence schedule) {
        try {
            schedule.setStatus(ScheduleSequence.STATUS_DENIED);
            getDaController().edit(schedule);
        } catch (RollbackFailureException ex) {
            Logger.getLogger(LabScheduleManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(LabScheduleManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void acceptLabRequest(ScheduleSequence schedule) {
        try {
            schedule.setStatus(ScheduleSequence.STATUS_ACCEPTED);
            getDaController().edit(schedule);
        } catch (RollbackFailureException ex) {
            Logger.getLogger(LabScheduleManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(LabScheduleManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void requestLabSequence(ScheduleSequence schedule) throws Exception {
        schedule.setStatus(ScheduleSequence.STATUS_PENDING);
        getDaController().create(schedule);
    }

    public void edit(ScheduleSequence schedule) {
        try {
            getDaController().edit(schedule);
        } catch (RollbackFailureException ex) {
            Logger.getLogger(ScheduleManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(ScheduleManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<ScheduleSequence> getAllSchedule() {
        return getDaController().getAllSchedule();
    }
    
    public List<ScheduleSequence> getAllScheduleOfOneDepartment(Department departmentId) {
        return getDaController().getAllScheduleOfOneDepartment(departmentId);
    }
}
