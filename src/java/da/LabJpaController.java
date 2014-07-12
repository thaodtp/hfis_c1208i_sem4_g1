/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package da;

import da.exceptions.NonexistentEntityException;
import da.exceptions.PreexistingEntityException;
import da.exceptions.RollbackFailureException;
import entity.Lab;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entity.LabSchedule;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.transaction.UserTransaction;

/**
 *
 * @author The
 */
public class LabJpaController implements Serializable {

    public LabJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
    public List<Lab> getAllLabs(){
        TypedQuery<Lab> query = getEntityManager().createQuery("SELECT l FROM Lab l", Lab.class);
        return query.getResultList();  
    }
    public List<Lab> displayLabs(){
        TypedQuery<Lab> query = getEntityManager().createQuery("SELECT l FROM Lab l WHERE l.type = :type", Lab.class);
        query.setParameter("type",1);
        return  query.getResultList();  
    }
    public List<Lab> displayServerRoom(){
        TypedQuery<Lab> query = getEntityManager().createQuery("SELECT l FROM Lab l WHERE l.type = :type", Lab.class);
        query.setParameter("type", 2);
        return  query.getResultList();  
    }
    public void create(Lab lab) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (lab.getLabScheduleList() == null) {
            lab.setLabScheduleList(new ArrayList<LabSchedule>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            List<LabSchedule> attachedLabScheduleList = new ArrayList<LabSchedule>();
            for (LabSchedule labScheduleListLabScheduleToAttach : lab.getLabScheduleList()) {
                labScheduleListLabScheduleToAttach = em.getReference(labScheduleListLabScheduleToAttach.getClass(), labScheduleListLabScheduleToAttach.getId());
                attachedLabScheduleList.add(labScheduleListLabScheduleToAttach);
            }
            lab.setLabScheduleList(attachedLabScheduleList);
            em.persist(lab);
            for (LabSchedule labScheduleListLabSchedule : lab.getLabScheduleList()) {
                Lab oldLabIdOfLabScheduleListLabSchedule = labScheduleListLabSchedule.getLabId();
                labScheduleListLabSchedule.setLabId(lab);
                labScheduleListLabSchedule = em.merge(labScheduleListLabSchedule);
                if (oldLabIdOfLabScheduleListLabSchedule != null) {
                    oldLabIdOfLabScheduleListLabSchedule.getLabScheduleList().remove(labScheduleListLabSchedule);
                    oldLabIdOfLabScheduleListLabSchedule = em.merge(oldLabIdOfLabScheduleListLabSchedule);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findLab(lab.getId()) != null) {
                throw new PreexistingEntityException("Lab " + lab + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Lab lab) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Lab persistentLab = em.find(Lab.class, lab.getId());
            List<LabSchedule> labScheduleListOld = persistentLab.getLabScheduleList();
            List<LabSchedule> labScheduleListNew = lab.getLabScheduleList();
            List<LabSchedule> attachedLabScheduleListNew = new ArrayList<LabSchedule>();
            for (LabSchedule labScheduleListNewLabScheduleToAttach : labScheduleListNew) {
                labScheduleListNewLabScheduleToAttach = em.getReference(labScheduleListNewLabScheduleToAttach.getClass(), labScheduleListNewLabScheduleToAttach.getId());
                attachedLabScheduleListNew.add(labScheduleListNewLabScheduleToAttach);
            }
            labScheduleListNew = attachedLabScheduleListNew;
            lab.setLabScheduleList(labScheduleListNew);
            lab = em.merge(lab);
            for (LabSchedule labScheduleListOldLabSchedule : labScheduleListOld) {
                if (!labScheduleListNew.contains(labScheduleListOldLabSchedule)) {
                    labScheduleListOldLabSchedule.setLabId(null);
                    labScheduleListOldLabSchedule = em.merge(labScheduleListOldLabSchedule);
                }
            }
            for (LabSchedule labScheduleListNewLabSchedule : labScheduleListNew) {
                if (!labScheduleListOld.contains(labScheduleListNewLabSchedule)) {
                    Lab oldLabIdOfLabScheduleListNewLabSchedule = labScheduleListNewLabSchedule.getLabId();
                    labScheduleListNewLabSchedule.setLabId(lab);
                    labScheduleListNewLabSchedule = em.merge(labScheduleListNewLabSchedule);
                    if (oldLabIdOfLabScheduleListNewLabSchedule != null && !oldLabIdOfLabScheduleListNewLabSchedule.equals(lab)) {
                        oldLabIdOfLabScheduleListNewLabSchedule.getLabScheduleList().remove(labScheduleListNewLabSchedule);
                        oldLabIdOfLabScheduleListNewLabSchedule = em.merge(oldLabIdOfLabScheduleListNewLabSchedule);
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
                Integer id = lab.getId();
                if (findLab(id) == null) {
                    throw new NonexistentEntityException("The lab with id " + id + " no longer exists.");
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
            Lab lab;
            try {
                lab = em.getReference(Lab.class, id);
                lab.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The lab with id " + id + " no longer exists.", enfe);
            }
            List<LabSchedule> labScheduleList = lab.getLabScheduleList();
            for (LabSchedule labScheduleListLabSchedule : labScheduleList) {
                labScheduleListLabSchedule.setLabId(null);
                labScheduleListLabSchedule = em.merge(labScheduleListLabSchedule);
            }
            em.remove(lab);
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

    public List<Lab> findLabEntities() {
        return findLabEntities(true, -1, -1);
    }

    public List<Lab> findLabEntities(int maxResults, int firstResult) {
        return findLabEntities(false, maxResults, firstResult);
    }

    private List<Lab> findLabEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Lab.class));
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

    public Lab findLab(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Lab.class, id);
        } finally {
            em.close();
        }
    }

    public int getLabCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Lab> rt = cq.from(Lab.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
