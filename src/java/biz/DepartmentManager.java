/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package biz;

import da.DepartmentJpaController;
import da.RequestJpaController;
import da.exceptions.RollbackFailureException;
import entity.Department;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

/**
 *
 * @author The
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class DepartmentManager {
    @PersistenceContext(unitName = "eAdministrationPU")
    private EntityManagerFactory em;
    @Resource
    private UserTransaction utx;

    private DepartmentJpaController daController;

    public DepartmentJpaController getDaController() {
        if (daController == null) {
            daController = new DepartmentJpaController(utx, em);
        }
        return daController;
    }
    
    public void create(Department department){
        try {
            getDaController().create(department);
        } catch (RollbackFailureException ex) {
            Logger.getLogger(DepartmentManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(DepartmentManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void delete(int id){
        try {
            getDaController().destroy(id);
        } catch (RollbackFailureException ex) {
            Logger.getLogger(DepartmentManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(DepartmentManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void edit(Department department){
        try {
            getDaController().edit(department);
        } catch (RollbackFailureException ex) {
            Logger.getLogger(DepartmentManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(DepartmentManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void getDepartment(String name){}

    public List<Department> getDepartmentById(int departmentId) {      
        return getDaController().getDepartmentById(departmentId);       
    }
    
    public String getAllName() {
        return getDaController().getAllName();
    }
}
