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
import entity.ScheduleSequence;
import entity.Lab;
import entity.Account;
import entity.LabSchedule;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.transaction.UserTransaction;

/**
 *
 * @author The
 */
public class LabScheduleJpaController implements Serializable {

    public LabScheduleJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
    public List<LabSchedule> getUnacceptedSchedule(){
        TypedQuery<LabSchedule> query = getEntityManager().createQuery("SELECT l FROM LabSchedule l WHERE l.status=0 AND l.date>:date", LabSchedule.class);
        query.setParameter("date", new Date());
        return query.getResultList();        
    }
    public List<LabSchedule> getAllSchedule(){
        TypedQuery<LabSchedule> query = getEntityManager().createQuery("SELECT l FROM LabSchedule l where l.date>:date", LabSchedule.class);
        query.setParameter("date", new Date());
        return query.getResultList(); 
    }
    public LabSchedule getSchedule(Date date, int slot, Lab lab) {
        TypedQuery<LabSchedule> query = getEntityManager().createQuery("SELECT l FROM LabSchedule l WHERE l.slot = :slot AND l.date=:date AND l.labId=:lab AND (l.sequenceId.status=1 OR l.sequenceId.status=0)", LabSchedule.class);
        query.setParameter("slot", slot);
        query.setParameter("date", date);
        query.setParameter("lab", lab);
        List<LabSchedule> result = query.getResultList();
        if (result.isEmpty()) {
            return null;
        } else {
            return result.get(0);
        }
    }

    public void create(LabSchedule labSchedule) throws PreexistingEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            ScheduleSequence sequenceId = labSchedule.getSequenceId();
            if (sequenceId != null) {
                sequenceId = em.getReference(sequenceId.getClass(), sequenceId.getId());
                labSchedule.setSequenceId(sequenceId);
            }
            Lab labId = labSchedule.getLabId();
            if (labId != null) {
                labId = em.getReference(labId.getClass(), labId.getId());
                labSchedule.setLabId(labId);
            }
            em.persist(labSchedule);
            if (sequenceId != null) {
                sequenceId.getLabScheduleList().add(labSchedule);
                sequenceId = em.merge(sequenceId);
            }
            if (labId != null) {
                labId.getLabScheduleList().add(labSchedule);
                labId = em.merge(labId);
            }
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

    public void edit(LabSchedule labSchedule) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            LabSchedule persistentLabSchedule = em.find(LabSchedule.class, labSchedule.getId());
            ScheduleSequence sequenceIdOld = persistentLabSchedule.getSequenceId();
            ScheduleSequence sequenceIdNew = labSchedule.getSequenceId();
            Lab labIdOld = persistentLabSchedule.getLabId();
            Lab labIdNew = labSchedule.getLabId();
            if (sequenceIdNew != null) {
                sequenceIdNew = em.getReference(sequenceIdNew.getClass(), sequenceIdNew.getId());
                labSchedule.setSequenceId(sequenceIdNew);
            }
            if (labIdNew != null) {
                labIdNew = em.getReference(labIdNew.getClass(), labIdNew.getId());
                labSchedule.setLabId(labIdNew);
            }
            labSchedule = em.merge(labSchedule);
            if (sequenceIdOld != null && !sequenceIdOld.equals(sequenceIdNew)) {
                sequenceIdOld.getLabScheduleList().remove(labSchedule);
                sequenceIdOld = em.merge(sequenceIdOld);
            }
            if (sequenceIdNew != null && !sequenceIdNew.equals(sequenceIdOld)) {
                sequenceIdNew.getLabScheduleList().add(labSchedule);
                sequenceIdNew = em.merge(sequenceIdNew);
            }
            if (labIdOld != null && !labIdOld.equals(labIdNew)) {
                labIdOld.getLabScheduleList().remove(labSchedule);
                labIdOld = em.merge(labIdOld);
            }
            if (labIdNew != null && !labIdNew.equals(labIdOld)) {
                labIdNew.getLabScheduleList().add(labSchedule);
                labIdNew = em.merge(labIdNew);
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
                Integer id = labSchedule.getId();
                if (findLabSchedule(id) == null) {
                    throw new NonexistentEntityException("The labSchedule with id " + id + " no longer exists.");
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
            LabSchedule labSchedule;
            try {
                labSchedule = em.getReference(LabSchedule.class, id);
                labSchedule.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The labSchedule with id " + id + " no longer exists.", enfe);
            }
            ScheduleSequence sequenceId = labSchedule.getSequenceId();
            if (sequenceId != null) {
                sequenceId.getLabScheduleList().remove(labSchedule);
                sequenceId = em.merge(sequenceId);
            }
            Lab labId = labSchedule.getLabId();
            if (labId != null) {
                labId.getLabScheduleList().remove(labSchedule);
                labId = em.merge(labId);
            }
            em.remove(labSchedule);
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

    public List<LabSchedule> findLabScheduleEntities() {
        return findLabScheduleEntities(true, -1, -1);
    }

    public List<LabSchedule> findLabScheduleEntities(int maxResults, int firstResult) {
        return findLabScheduleEntities(false, maxResults, firstResult);
    }

    private List<LabSchedule> findLabScheduleEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(LabSchedule.class));
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

    public LabSchedule findLabSchedule(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(LabSchedule.class, id);
        } finally {
            em.close();
        }
    }

    public int getLabScheduleCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<LabSchedule> rt = cq.from(LabSchedule.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
