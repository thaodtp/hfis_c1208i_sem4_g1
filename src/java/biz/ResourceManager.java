/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biz;

import da.LabJpaController;
import da.ResourceJpaController;
import da.exceptions.RollbackFailureException;
import entity.Resource;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class ResourceManager {

    @PersistenceUnit(unitName = "eAdministrationPU")
    private EntityManagerFactory em;
    @javax.annotation.Resource
    private UserTransaction utx;
    private ResourceJpaController daController;

    public ResourceJpaController getDaController() {
        if (daController == null) {
            daController = new ResourceJpaController(utx, em);
        }
        return daController;
    }

    public void create(Resource resource) {
        try {
            getDaController().create(resource);
        } catch (RollbackFailureException ex) {
            Logger.getLogger(ResourceManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(ResourceManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void delete(int id){
        try {
            getDaController().destroy(id);
        } catch (RollbackFailureException ex) {
            Logger.getLogger(ResourceManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(ResourceManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void edit(Resource resource){
        try {
            getDaController().edit(resource);
        } catch (RollbackFailureException ex) {
            Logger.getLogger(ResourceManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(ResourceManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void edit(int id, String title, String content, int type) {
        try {
            Resource resource = getResourceById(id);
            resource.setTitle(title);
            resource.setContent(content);
            resource.setType(type);
            getDaController().edit(resource);
        } catch (RollbackFailureException ex) {
            Logger.getLogger(ResourceManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(ResourceManager.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    public List<Resource> getAllResource(){
        return getDaController().getAllResources();
    }
    
    public List<Resource> displaySoftware() {
        return getDaController().displaySoftware();
    }
    
    public List<Resource> displayHardware() {
        return getDaController().displayHardware();
    }
    
    public List<Resource> displaySyllabus() {
        return getDaController().displaySyllabus();
    }
    
    public List<Resource> displayELearning() {
        return getDaController().displayELearning();
    }
    
    public List<Resource> searchResource(String keyword){
        return getDaController().searchResource(keyword);
    }
    public List<Resource> getResourceByType(int type){
        return getDaController().getResourceByType(type);
    }
    
    public Resource getResourceById(int id) {
        return getDaController().getResourceById(id);
    }

}
