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
import entity.Lab;
import entity.LabSchedule;
import entity.ScheduleSequence;
import java.util.ArrayList;
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
public class ScheduleSequenceJpaController implements Serializable {

    public ScheduleSequenceJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public List<ScheduleSequence> getUnacceptedSchedule() {
        TypedQuery<ScheduleSequence> query = getEntityManager().createQuery("SELECT l FROM ScheduleSequence l WHERE l.status=0", ScheduleSequence.class);
        return query.getResultList();
    }

    public List<ScheduleSequence> getAllSchedule() {
        TypedQuery<ScheduleSequence> query = getEntityManager().createQuery("SELECT l FROM ScheduleSequence l", ScheduleSequence.class);
        return query.getResultList();
    }

    public void create(ScheduleSequence scheduleSequence) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (scheduleSequence.getLabScheduleList() == null) {
            scheduleSequence.setLabScheduleList(new ArrayList<LabSchedule>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Account requestAccount = scheduleSequence.getRequestAccount();
            if (requestAccount != null) {
                requestAccount = em.getReference(requestAccount.getClass(), requestAccount.getUsername());
                scheduleSequence.setRequestAccount(requestAccount);
            }
            List<LabSchedule> attachedLabScheduleList = new ArrayList<LabSchedule>();
            for (LabSchedule labScheduleListLabScheduleToAttach : scheduleSequence.getLabScheduleList()) {
                labScheduleListLabScheduleToAttach = em.getReference(labScheduleListLabScheduleToAttach.getClass(), labScheduleListLabScheduleToAttach.getId());
                attachedLabScheduleList.add(labScheduleListLabScheduleToAttach);
            }
            scheduleSequence.setLabScheduleList(attachedLabScheduleList);
            em.persist(scheduleSequence);
            if (requestAccount != null) {
                requestAccount.getScheduleSequenceList().add(scheduleSequence);
                requestAccount = em.merge(requestAccount);
            }
            for (LabSchedule labScheduleListLabSchedule : scheduleSequence.getLabScheduleList()) {
                ScheduleSequence oldSequenceIdOfLabScheduleListLabSchedule = labScheduleListLabSchedule.getSequenceId();
                labScheduleListLabSchedule.setSequenceId(scheduleSequence);
                labScheduleListLabSchedule = em.merge(labScheduleListLabSchedule);
                if (oldSequenceIdOfLabScheduleListLabSchedule != null) {
                    oldSequenceIdOfLabScheduleListLabSchedule.getLabScheduleList().remove(labScheduleListLabSchedule);
                    oldSequenceIdOfLabScheduleListLabSchedule = em.merge(oldSequenceIdOfLabScheduleListLabSchedule);
                }
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

    public void edit(ScheduleSequence scheduleSequence) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            ScheduleSequence persistentScheduleSequence = em.find(ScheduleSequence.class, scheduleSequence.getId());
            Account requestAccountOld = persistentScheduleSequence.getRequestAccount();
            Account requestAccountNew = scheduleSequence.getRequestAccount();
            List<LabSchedule> labScheduleListOld = persistentScheduleSequence.getLabScheduleList();
            List<LabSchedule> labScheduleListNew = scheduleSequence.getLabScheduleList();
            if (requestAccountNew != null) {
                requestAccountNew = em.getReference(requestAccountNew.getClass(), requestAccountNew.getUsername());
                scheduleSequence.setRequestAccount(requestAccountNew);
            }
            List<LabSchedule> attachedLabScheduleListNew = new ArrayList<LabSchedule>();
            for (LabSchedule labScheduleListNewLabScheduleToAttach : labScheduleListNew) {
                labScheduleListNewLabScheduleToAttach = em.getReference(labScheduleListNewLabScheduleToAttach.getClass(), labScheduleListNewLabScheduleToAttach.getId());
                attachedLabScheduleListNew.add(labScheduleListNewLabScheduleToAttach);
            }
            labScheduleListNew = attachedLabScheduleListNew;
            scheduleSequence.setLabScheduleList(labScheduleListNew);
            scheduleSequence = em.merge(scheduleSequence);
            if (requestAccountOld != null && !requestAccountOld.equals(requestAccountNew)) {
                requestAccountOld.getScheduleSequenceList().remove(scheduleSequence);
                requestAccountOld = em.merge(requestAccountOld);
            }
            if (requestAccountNew != null && !requestAccountNew.equals(requestAccountOld)) {
                requestAccountNew.getScheduleSequenceList().add(scheduleSequence);
                requestAccountNew = em.merge(requestAccountNew);
            }
            for (LabSchedule labScheduleListOldLabSchedule : labScheduleListOld) {
                if (!labScheduleListNew.contains(labScheduleListOldLabSchedule)) {
                    labScheduleListOldLabSchedule.setSequenceId(null);
                    labScheduleListOldLabSchedule = em.merge(labScheduleListOldLabSchedule);
                }
            }
            for (LabSchedule labScheduleListNewLabSchedule : labScheduleListNew) {
                if (!labScheduleListOld.contains(labScheduleListNewLabSchedule)) {
                    ScheduleSequence oldSequenceIdOfLabScheduleListNewLabSchedule = labScheduleListNewLabSchedule.getSequenceId();
                    labScheduleListNewLabSchedule.setSequenceId(scheduleSequence);
                    labScheduleListNewLabSchedule = em.merge(labScheduleListNewLabSchedule);
                    if (oldSequenceIdOfLabScheduleListNewLabSchedule != null && !oldSequenceIdOfLabScheduleListNewLabSchedule.equals(scheduleSequence)) {
                        oldSequenceIdOfLabScheduleListNewLabSchedule.getLabScheduleList().remove(labScheduleListNewLabSchedule);
                        oldSequenceIdOfLabScheduleListNewLabSchedule = em.merge(oldSequenceIdOfLabScheduleListNewLabSchedule);
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
                Integer id = scheduleSequence.getId();
                if (findScheduleSequence(id) == null) {
                    throw new NonexistentEntityException("The scheduleSequence with id " + id + " no longer exists.");
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
            ScheduleSequence scheduleSequence;
            try {
                scheduleSequence = em.getReference(ScheduleSequence.class, id);
                scheduleSequence.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The scheduleSequence with id " + id + " no longer exists.", enfe);
            }
            Account requestAccount = scheduleSequence.getRequestAccount();
            if (requestAccount != null) {
                requestAccount.getScheduleSequenceList().remove(scheduleSequence);
                requestAccount = em.merge(requestAccount);
            }
            List<LabSchedule> labScheduleList = scheduleSequence.getLabScheduleList();
            for (LabSchedule labScheduleListLabSchedule : labScheduleList) {
                labScheduleListLabSchedule.setSequenceId(null);
                labScheduleListLabSchedule = em.merge(labScheduleListLabSchedule);
            }
            em.remove(scheduleSequence);
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

    public List<ScheduleSequence> findScheduleSequenceEntities() {
        return findScheduleSequenceEntities(true, -1, -1);
    }

    public List<ScheduleSequence> findScheduleSequenceEntities(int maxResults, int firstResult) {
        return findScheduleSequenceEntities(false, maxResults, firstResult);
    }

    private List<ScheduleSequence> findScheduleSequenceEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(ScheduleSequence.class));
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

    public ScheduleSequence findScheduleSequence(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(ScheduleSequence.class, id);
        } finally {
            em.close();
        }
    }

    public int getScheduleSequenceCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<ScheduleSequence> rt = cq.from(ScheduleSequence.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
