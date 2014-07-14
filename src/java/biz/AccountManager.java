/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biz;

import da.AccountJpaController;
import da.exceptions.NonexistentEntityException;
import da.exceptions.RollbackFailureException;
import entity.Account;
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
public class AccountManager {

    @PersistenceUnit(unitName = "eAdministrationPU")
    private EntityManagerFactory em;

    @Resource
    private UserTransaction utx;

    private AccountJpaController daController;

    public AccountJpaController getDaController() {
        if (daController == null) {
            daController = new AccountJpaController(utx, em);
        }
        return daController;
    }
    public void create(Account account) {
        try {
            getDaController().create(account);
        } catch (RollbackFailureException ex) {
            Logger.getLogger(AccountManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(AccountManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void edit(Account account) {
        try {
            getDaController().edit(account);
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(AccountManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RollbackFailureException ex) {
            Logger.getLogger(AccountManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(AccountManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void delete(String username) {
        try {
            getDaController().destroy(username);
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(AccountManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RollbackFailureException ex) {
            Logger.getLogger(AccountManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(AccountManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<Account> getAllAccount() {
        return getDaController().findAccountEntities();
    }

    public boolean isAccountExist(String username) {
        return getDaController().findAccount(username) != null;
    }

    public List<Account> getAccountByRole(int role) {
        return getDaController().getAccountByRole(role);
    }
    public Account getAccount(String username){
        return getDaController().getAccount(username);
    }
    public Account login(String username, String password) {
        Account target = getAccount(username);
        if (target != null && target.getPassword().equals(password)) {
            return target;
        } else {
            return null;
        }
    }
    public List<Account> searchAccByName(String keyword) {
        return getDaController().searchAccByName(keyword);
    }
}
