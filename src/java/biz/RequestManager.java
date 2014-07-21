/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biz;

import da.AccountJpaController;
import da.RequestJpaController;
import da.exceptions.RollbackFailureException;
import entity.Account;
import entity.Request;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import javax.transaction.UserTransaction;

/**
 *
 * @author The
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class RequestManager {

    @PersistenceUnit(unitName = "eAdministrationPU")
    private EntityManagerFactory em;
    @Resource
    private UserTransaction utx;

    private RequestJpaController daController;

    public RequestJpaController getDaController() {
        if (daController == null) {
            daController = new RequestJpaController(utx, em);
        }
        return daController;
    }

    public void sendMessage(Account toUser, String content) {
        try {
            getDaController().create(new Request(content, new Date(), Request.STATUS_PENDING, Request.TYPE_MESSAGE, null, toUser));
        } catch (RollbackFailureException ex) {
            Logger.getLogger(RequestManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(RequestManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<Request> getRequests(String username, int type) {
        return getDaController().getRequests(username, type);
    }

    public List<Request> getRequests(int type) {
        return getDaController().getRequests(type);
    }

    public List<Request> getRequestsByStatus(int status, int type) {
        return getDaController().getRequestsByStatus(status, type);
    }

    public void create(Request request) {
        try {
            getDaController().create(request);
        } catch (RollbackFailureException ex) {
            Logger.getLogger(RequestManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(RequestManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void edit(Request request) {
        try {
            getDaController().edit(request);
        } catch (RollbackFailureException ex) {
            Logger.getLogger(RequestManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(RequestManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void delete(int id) {
        try {
            getDaController().destroy(id);
        } catch (RollbackFailureException ex) {
            Logger.getLogger(RequestManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(RequestManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
