/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package da;

import da.exceptions.IllegalOrphanException;
import da.exceptions.NonexistentEntityException;
import da.exceptions.PreexistingEntityException;
import da.exceptions.RollbackFailureException;
import entity.Account;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entity.Department;
import entity.LabSchedule;
import java.util.ArrayList;
import java.util.List;
import entity.Request;
import entity.ScheduleSequence;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.transaction.UserTransaction;

/**
 *
 * @author The
 */
public class AccountJpaController implements Serializable {

    public AccountJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public Account getAccount(String username) {
        TypedQuery<Account> query = getEntityManager().createQuery("SELECT a FROM Account a WHERE a.username = :username", Account.class);
        query.setParameter("username", username);
        List<Account> result = query.getResultList();
        if (result.isEmpty()) {
            return null;
        } else {
            return result.get(0);
        }
    }

    public List<Account> getAccountByRole(int role) {
        TypedQuery<Account> query = getEntityManager().createQuery("SELECT a FROM Account a WHERE a.role = :role", Account.class);
        query.setParameter("role", role);
        List<Account> result = query.getResultList();
        if (result.isEmpty()) {
            return null;
        } else {
            return result;
        }
    }

    public List<Account> searchAccByName(String keyword) {
        TypedQuery<Account> query = getEntityManager().createQuery("SELECT a FROM Account a WHERE a.name like :name or a.username like :username", Account.class);
        query.setParameter("name", "%" + keyword + "%");
        query.setParameter("username", "%" + keyword + "%");
        return query.getResultList();
    }

    public void create(Account account) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (account.getScheduleSequenceList() == null) {
            account.setScheduleSequenceList(new ArrayList<ScheduleSequence>());
        }
        if (account.getRequestList() == null) {
            account.setRequestList(new ArrayList<Request>());
        }
        if (account.getRequestList1() == null) {
            account.setRequestList1(new ArrayList<Request>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Department departmentId = account.getDepartmentId();
            if (departmentId != null) {
                departmentId = em.getReference(departmentId.getClass(), departmentId.getId());
                account.setDepartmentId(departmentId);
            }
            List<ScheduleSequence> attachedScheduleSequenceList = new ArrayList<ScheduleSequence>();
            for (ScheduleSequence scheduleSequenceListScheduleSequenceToAttach : account.getScheduleSequenceList()) {
                scheduleSequenceListScheduleSequenceToAttach = em.getReference(scheduleSequenceListScheduleSequenceToAttach.getClass(), scheduleSequenceListScheduleSequenceToAttach.getId());
                attachedScheduleSequenceList.add(scheduleSequenceListScheduleSequenceToAttach);
            }
            account.setScheduleSequenceList(attachedScheduleSequenceList);
            List<Request> attachedRequestList = new ArrayList<Request>();
            for (Request requestListRequestToAttach : account.getRequestList()) {
                requestListRequestToAttach = em.getReference(requestListRequestToAttach.getClass(), requestListRequestToAttach.getId());
                attachedRequestList.add(requestListRequestToAttach);
            }
            account.setRequestList(attachedRequestList);
            List<Request> attachedRequestList1 = new ArrayList<Request>();
            for (Request requestList1RequestToAttach : account.getRequestList1()) {
                requestList1RequestToAttach = em.getReference(requestList1RequestToAttach.getClass(), requestList1RequestToAttach.getId());
                attachedRequestList1.add(requestList1RequestToAttach);
            }
            account.setRequestList1(attachedRequestList1);
            em.persist(account);
            if (departmentId != null) {
                departmentId.getAccountList().add(account);
                departmentId = em.merge(departmentId);
            }
            for (ScheduleSequence scheduleSequenceListScheduleSequence : account.getScheduleSequenceList()) {
                Account oldRequestAccountOfScheduleSequenceListScheduleSequence = scheduleSequenceListScheduleSequence.getRequestAccount();
                scheduleSequenceListScheduleSequence.setRequestAccount(account);
                scheduleSequenceListScheduleSequence = em.merge(scheduleSequenceListScheduleSequence);
                if (oldRequestAccountOfScheduleSequenceListScheduleSequence != null) {
                    oldRequestAccountOfScheduleSequenceListScheduleSequence.getScheduleSequenceList().remove(scheduleSequenceListScheduleSequence);
                    oldRequestAccountOfScheduleSequenceListScheduleSequence = em.merge(oldRequestAccountOfScheduleSequenceListScheduleSequence);
                }
            }
            for (Request requestListRequest : account.getRequestList()) {
                Account oldResolveAccountOfRequestListRequest = requestListRequest.getResolveAccount();
                requestListRequest.setResolveAccount(account);
                requestListRequest = em.merge(requestListRequest);
                if (oldResolveAccountOfRequestListRequest != null) {
                    oldResolveAccountOfRequestListRequest.getRequestList().remove(requestListRequest);
                    oldResolveAccountOfRequestListRequest = em.merge(oldResolveAccountOfRequestListRequest);
                }
            }
            for (Request requestList1Request : account.getRequestList1()) {
                Account oldRequestAccountOfRequestList1Request = requestList1Request.getRequestAccount();
                requestList1Request.setRequestAccount(account);
                requestList1Request = em.merge(requestList1Request);
                if (oldRequestAccountOfRequestList1Request != null) {
                    oldRequestAccountOfRequestList1Request.getRequestList1().remove(requestList1Request);
                    oldRequestAccountOfRequestList1Request = em.merge(oldRequestAccountOfRequestList1Request);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findAccount(account.getUsername()) != null) {
                throw new PreexistingEntityException("Account " + account + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Account account) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Account persistentAccount = em.find(Account.class, account.getUsername());
            Department departmentIdOld = persistentAccount.getDepartmentId();
            Department departmentIdNew = account.getDepartmentId();
            List<ScheduleSequence> scheduleSequenceListOld = persistentAccount.getScheduleSequenceList();
            List<ScheduleSequence> scheduleSequenceListNew = account.getScheduleSequenceList();
            List<Request> requestListOld = persistentAccount.getRequestList();
            List<Request> requestListNew = account.getRequestList();
            List<Request> requestList1Old = persistentAccount.getRequestList1();
            List<Request> requestList1New = account.getRequestList1();
            if (departmentIdNew != null) {
                departmentIdNew = em.getReference(departmentIdNew.getClass(), departmentIdNew.getId());
                account.setDepartmentId(departmentIdNew);
            }
            List<ScheduleSequence> attachedScheduleSequenceListNew = new ArrayList<ScheduleSequence>();
            for (ScheduleSequence scheduleSequenceListNewScheduleSequenceToAttach : scheduleSequenceListNew) {
                scheduleSequenceListNewScheduleSequenceToAttach = em.getReference(scheduleSequenceListNewScheduleSequenceToAttach.getClass(), scheduleSequenceListNewScheduleSequenceToAttach.getId());
                attachedScheduleSequenceListNew.add(scheduleSequenceListNewScheduleSequenceToAttach);
            }
            scheduleSequenceListNew = attachedScheduleSequenceListNew;
            account.setScheduleSequenceList(scheduleSequenceListNew);
            List<Request> attachedRequestListNew = new ArrayList<Request>();
            for (Request requestListNewRequestToAttach : requestListNew) {
                requestListNewRequestToAttach = em.getReference(requestListNewRequestToAttach.getClass(), requestListNewRequestToAttach.getId());
                attachedRequestListNew.add(requestListNewRequestToAttach);
            }
            requestListNew = attachedRequestListNew;
            account.setRequestList(requestListNew);
            List<Request> attachedRequestList1New = new ArrayList<Request>();
            for (Request requestList1NewRequestToAttach : requestList1New) {
                requestList1NewRequestToAttach = em.getReference(requestList1NewRequestToAttach.getClass(), requestList1NewRequestToAttach.getId());
                attachedRequestList1New.add(requestList1NewRequestToAttach);
            }
            requestList1New = attachedRequestList1New;
            account.setRequestList1(requestList1New);
            account = em.merge(account);
            if (departmentIdOld != null && !departmentIdOld.equals(departmentIdNew)) {
                departmentIdOld.getAccountList().remove(account);
                departmentIdOld = em.merge(departmentIdOld);
            }
            if (departmentIdNew != null && !departmentIdNew.equals(departmentIdOld)) {
                departmentIdNew.getAccountList().add(account);
                departmentIdNew = em.merge(departmentIdNew);
            }
            for (ScheduleSequence scheduleSequenceListOldScheduleSequence : scheduleSequenceListOld) {
                if (!scheduleSequenceListNew.contains(scheduleSequenceListOldScheduleSequence)) {
                    scheduleSequenceListOldScheduleSequence.setRequestAccount(null);
                    scheduleSequenceListOldScheduleSequence = em.merge(scheduleSequenceListOldScheduleSequence);
                }
            }
            for (ScheduleSequence scheduleSequenceListNewScheduleSequence : scheduleSequenceListNew) {
                if (!scheduleSequenceListOld.contains(scheduleSequenceListNewScheduleSequence)) {
                    Account oldRequestAccountOfScheduleSequenceListNewScheduleSequence = scheduleSequenceListNewScheduleSequence.getRequestAccount();
                    scheduleSequenceListNewScheduleSequence.setRequestAccount(account);
                    scheduleSequenceListNewScheduleSequence = em.merge(scheduleSequenceListNewScheduleSequence);
                    if (oldRequestAccountOfScheduleSequenceListNewScheduleSequence != null && !oldRequestAccountOfScheduleSequenceListNewScheduleSequence.equals(account)) {
                        oldRequestAccountOfScheduleSequenceListNewScheduleSequence.getScheduleSequenceList().remove(scheduleSequenceListNewScheduleSequence);
                        oldRequestAccountOfScheduleSequenceListNewScheduleSequence = em.merge(oldRequestAccountOfScheduleSequenceListNewScheduleSequence);
                    }
                }
            }
            for (Request requestListOldRequest : requestListOld) {
                if (!requestListNew.contains(requestListOldRequest)) {
                    requestListOldRequest.setResolveAccount(null);
                    requestListOldRequest = em.merge(requestListOldRequest);
                }
            }
            for (Request requestListNewRequest : requestListNew) {
                if (!requestListOld.contains(requestListNewRequest)) {
                    Account oldResolveAccountOfRequestListNewRequest = requestListNewRequest.getResolveAccount();
                    requestListNewRequest.setResolveAccount(account);
                    requestListNewRequest = em.merge(requestListNewRequest);
                    if (oldResolveAccountOfRequestListNewRequest != null && !oldResolveAccountOfRequestListNewRequest.equals(account)) {
                        oldResolveAccountOfRequestListNewRequest.getRequestList().remove(requestListNewRequest);
                        oldResolveAccountOfRequestListNewRequest = em.merge(oldResolveAccountOfRequestListNewRequest);
                    }
                }
            }
            for (Request requestList1OldRequest : requestList1Old) {
                if (!requestList1New.contains(requestList1OldRequest)) {
                    requestList1OldRequest.setRequestAccount(null);
                    requestList1OldRequest = em.merge(requestList1OldRequest);
                }
            }
            for (Request requestList1NewRequest : requestList1New) {
                if (!requestList1Old.contains(requestList1NewRequest)) {
                    Account oldRequestAccountOfRequestList1NewRequest = requestList1NewRequest.getRequestAccount();
                    requestList1NewRequest.setRequestAccount(account);
                    requestList1NewRequest = em.merge(requestList1NewRequest);
                    if (oldRequestAccountOfRequestList1NewRequest != null && !oldRequestAccountOfRequestList1NewRequest.equals(account)) {
                        oldRequestAccountOfRequestList1NewRequest.getRequestList1().remove(requestList1NewRequest);
                        oldRequestAccountOfRequestList1NewRequest = em.merge(oldRequestAccountOfRequestList1NewRequest);
                    }
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = account.getUsername();
                if (findAccount(id) == null) {
                    throw new NonexistentEntityException("The account with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Account account;
            try {
                account = em.getReference(Account.class, id);
                account.getUsername();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The account with id " + id + " no longer exists.", enfe);
            }
            Department departmentId = account.getDepartmentId();
            if (departmentId != null) {
                departmentId.getAccountList().remove(account);
                departmentId = em.merge(departmentId);
            }
            List<ScheduleSequence> scheduleSequenceList = account.getScheduleSequenceList();
            for (ScheduleSequence scheduleSequenceListScheduleSequence : scheduleSequenceList) {
                scheduleSequenceListScheduleSequence.setRequestAccount(null);
                scheduleSequenceListScheduleSequence = em.merge(scheduleSequenceListScheduleSequence);
            }
            List<Request> requestList = account.getRequestList();
            for (Request requestListRequest : requestList) {
                requestListRequest.setResolveAccount(null);
                requestListRequest = em.merge(requestListRequest);
            }
            List<Request> requestList1 = account.getRequestList1();
            for (Request requestList1Request : requestList1) {
                requestList1Request.setRequestAccount(null);
                requestList1Request = em.merge(requestList1Request);
            }
            em.remove(account);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Account> findAccountEntities() {
        return findAccountEntities(true, -1, -1);
    }

    public List<Account> findAccountEntities(int maxResults, int firstResult) {
        return findAccountEntities(false, maxResults, firstResult);
    }

    private List<Account> findAccountEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Account.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Account findAccount(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Account.class, id);
        } finally {
            em.close();
        }
    }

    public int getAccountCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Account> rt = cq.from(Account.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

    public Account getAccountByUsername(String username) {
        String queryString = "SELECT a FROM Account a WHERE a.username = :username";
        TypedQuery<Account> query = getEntityManager().createQuery(queryString, Account.class);
        query.setParameter("username", username);
        List<Account> results = query.getResultList();
        if (results.isEmpty()) {
            return null;
        } else {
            return query.getResultList().get(0);
        }
    }
}
