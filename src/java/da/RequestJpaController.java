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
import entity.Request;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author The
 */
public class RequestJpaController implements Serializable {

    public RequestJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Request request) throws PreexistingEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Account requestAccount = request.getRequestAccount();
            if (requestAccount != null) {
                requestAccount = em.getReference(requestAccount.getClass(), requestAccount.getUsername());
                request.setRequestAccount(requestAccount);
            }
            em.persist(request);
            if (requestAccount != null) {
                requestAccount.getRequestList().add(request);
                requestAccount = em.merge(requestAccount);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findRequest(request.getId()) != null) {
                throw new PreexistingEntityException("Request " + request + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Request request) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Request persistentRequest = em.find(Request.class, request.getId());
            Account requestAccountOld = persistentRequest.getRequestAccount();
            Account requestAccountNew = request.getRequestAccount();
            if (requestAccountNew != null) {
                requestAccountNew = em.getReference(requestAccountNew.getClass(), requestAccountNew.getUsername());
                request.setRequestAccount(requestAccountNew);
            }
            request = em.merge(request);
            if (requestAccountOld != null && !requestAccountOld.equals(requestAccountNew)) {
                requestAccountOld.getRequestList().remove(request);
                requestAccountOld = em.merge(requestAccountOld);
            }
            if (requestAccountNew != null && !requestAccountNew.equals(requestAccountOld)) {
                requestAccountNew.getRequestList().add(request);
                requestAccountNew = em.merge(requestAccountNew);
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
                Integer id = request.getId();
                if (findRequest(id) == null) {
                    throw new NonexistentEntityException("The request with id " + id + " no longer exists.");
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
            Request request;
            try {
                request = em.getReference(Request.class, id);
                request.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The request with id " + id + " no longer exists.", enfe);
            }
            Account requestAccount = request.getRequestAccount();
            if (requestAccount != null) {
                requestAccount.getRequestList().remove(request);
                requestAccount = em.merge(requestAccount);
            }
            em.remove(request);
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

    public List<Request> findRequestEntities() {
        return findRequestEntities(true, -1, -1);
    }

    public List<Request> findRequestEntities(int maxResults, int firstResult) {
        return findRequestEntities(false, maxResults, firstResult);
    }

    private List<Request> findRequestEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Request.class));
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

    public Request findRequest(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Request.class, id);
        } finally {
            em.close();
        }
    }

    public int getRequestCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Request> rt = cq.from(Request.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
