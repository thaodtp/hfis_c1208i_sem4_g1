/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package da;

import da.exceptions.NonexistentEntityException;
import da.exceptions.PreexistingEntityException;
import da.exceptions.RollbackFailureException;
import entity.Resource;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.UserTransaction;

/**
 *
 * @author The
 */
public class ResourceJpaController implements Serializable {

    public ResourceJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
    
    public List<Resource> getAllResources(){
         TypedQuery<Resource> query = getEntityManager().createQuery("SELECT r FROM Resource r", Resource.class);
        return query.getResultList();
    }
    public List<Resource> searchResource(String keyword){
        TypedQuery<Resource> query = getEntityManager().createQuery("SELECT r FROM Resource r WHERE  r.title like :title or r.content like :content", Resource.class);     
        query.setParameter("title", "%"+ keyword + "%");     
        query.setParameter("content", "%"+ keyword + "%");
        return query.getResultList();
    }
    public List<Resource> getResourceByType(int type){
        TypedQuery<Resource> query = getEntityManager().createQuery("SELECT r FROM Resource r WHERE r.type = :type", Resource.class);
        query.setParameter("type", type);
        return query.getResultList();
    }
    public void create(Resource resource) throws PreexistingEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            em.persist(resource);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findResource(resource.getId()) != null) {
                throw new PreexistingEntityException("Resource " + resource + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Resource resource) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            resource = em.merge(resource);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = resource.getId();
                if (findResource(id) == null) {
                    throw new NonexistentEntityException("The resource with id " + id + " no longer exists.");
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
            Resource resource;
            try {
                resource = em.getReference(Resource.class, id);
                resource.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The resource with id " + id + " no longer exists.", enfe);
            }
            em.remove(resource);
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

    public List<Resource> findResourceEntities() {
        return findResourceEntities(true, -1, -1);
    }

    public List<Resource> findResourceEntities(int maxResults, int firstResult) {
        return findResourceEntities(false, maxResults, firstResult);
    }

    private List<Resource> findResourceEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Resource.class));
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

    public Resource findResource(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Resource.class, id);
        } finally {
            em.close();
        }
    }

    public int getResourceCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Resource> rt = cq.from(Resource.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
