/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package biz;

import da.AccountJpaController;
import da.RequestJpaController;
import entity.Request;
import java.util.List;
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
public class RequestManager {
        @PersistenceContext(unitName = "eAdministrationPU")
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
    
    public List<Request> getRequests(String username){
        return getDaController().getRequests(username);
    }
}
