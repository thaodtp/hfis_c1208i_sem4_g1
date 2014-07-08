/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package da;

import da.exceptions.NonexistentEntityException;
import da.exceptions.PreexistingEntityException;
import da.exceptions.RollbackFailureException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entity.Account;
import entity.Complaint;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author The
 */
public class ComplaintJpaController implements Serializable {

    public ComplaintJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Complaint complaint) throws PreexistingEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Account resolveAccount = complaint.getResolveAccount();
            if (resolveAccount != null) {
                resolveAccount = em.getReference(resolveAccount.getClass(), resolveAccount.getUsername());
                complaint.setResolveAccount(resolveAccount);
            }
            Account complainAccount = complaint.getComplainAccount();
            if (complainAccount != null) {
                complainAccount = em.getReference(complainAccount.getClass(), complainAccount.getUsername());
                complaint.setComplainAccount(complainAccount);
            }
            em.persist(complaint);
            if (resolveAccount != null) {
                resolveAccount.getComplaintList().add(complaint);
                resolveAccount = em.merge(resolveAccount);
            }
            if (complainAccount != null) {
                complainAccount.getComplaintList().add(complaint);
                complainAccount = em.merge(complainAccount);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findComplaint(complaint.getId()) != null) {
                throw new PreexistingEntityException("Complaint " + complaint + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Complaint complaint) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Complaint persistentComplaint = em.find(Complaint.class, complaint.getId());
            Account resolveAccountOld = persistentComplaint.getResolveAccount();
            Account resolveAccountNew = complaint.getResolveAccount();
            Account complainAccountOld = persistentComplaint.getComplainAccount();
            Account complainAccountNew = complaint.getComplainAccount();
            if (resolveAccountNew != null) {
                resolveAccountNew = em.getReference(resolveAccountNew.getClass(), resolveAccountNew.getUsername());
                complaint.setResolveAccount(resolveAccountNew);
            }
            if (complainAccountNew != null) {
                complainAccountNew = em.getReference(complainAccountNew.getClass(), complainAccountNew.getUsername());
                complaint.setComplainAccount(complainAccountNew);
            }
            complaint = em.merge(complaint);
            if (resolveAccountOld != null && !resolveAccountOld.equals(resolveAccountNew)) {
                resolveAccountOld.getComplaintList().remove(complaint);
                resolveAccountOld = em.merge(resolveAccountOld);
            }
            if (resolveAccountNew != null && !resolveAccountNew.equals(resolveAccountOld)) {
                resolveAccountNew.getComplaintList().add(complaint);
                resolveAccountNew = em.merge(resolveAccountNew);
            }
            if (complainAccountOld != null && !complainAccountOld.equals(complainAccountNew)) {
                complainAccountOld.getComplaintList().remove(complaint);
                complainAccountOld = em.merge(complainAccountOld);
            }
            if (complainAccountNew != null && !complainAccountNew.equals(complainAccountOld)) {
                complainAccountNew.getComplaintList().add(complaint);
                complainAccountNew = em.merge(complainAccountNew);
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
                Integer id = complaint.getId();
                if (findComplaint(id) == null) {
                    throw new NonexistentEntityException("The complaint with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Complaint complaint;
            try {
                complaint = em.getReference(Complaint.class, id);
                complaint.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The complaint with id " + id + " no longer exists.", enfe);
            }
            Account resolveAccount = complaint.getResolveAccount();
            if (resolveAccount != null) {
                resolveAccount.getComplaintList().remove(complaint);
                resolveAccount = em.merge(resolveAccount);
            }
            Account complainAccount = complaint.getComplainAccount();
            if (complainAccount != null) {
                complainAccount.getComplaintList().remove(complaint);
                complainAccount = em.merge(complainAccount);
            }
            em.remove(complaint);
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

    public List<Complaint> findComplaintEntities() {
        return findComplaintEntities(true, -1, -1);
    }

    public List<Complaint> findComplaintEntities(int maxResults, int firstResult) {
        return findComplaintEntities(false, maxResults, firstResult);
    }

    private List<Complaint> findComplaintEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Complaint.class));
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

    public Complaint findComplaint(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Complaint.class, id);
        } finally {
            em.close();
        }
    }

    public int getComplaintCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Complaint> rt = cq.from(Complaint.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
