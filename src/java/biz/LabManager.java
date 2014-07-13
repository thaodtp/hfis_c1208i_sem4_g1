/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biz;

import da.AccountJpaController;
import da.LabJpaController;
import entity.Lab;
import java.util.List;
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

    public List<Lab> getLabs() {
        return getDaController().findLabEntities();
    }
}
