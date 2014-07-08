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
import entity.Report;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author The
 */
public class ReportJpaController implements Serializable {

    public ReportJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Report report) throws PreexistingEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Account requestAccount = report.getRequestAccount();
            if (requestAccount != null) {
                requestAccount = em.getReference(requestAccount.getClass(), requestAccount.getUsername());
                report.setRequestAccount(requestAccount);
            }
            em.persist(report);
            if (requestAccount != null) {
                requestAccount.getReportList().add(report);
                requestAccount = em.merge(requestAccount);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findReport(report.getId()) != null) {
                throw new PreexistingEntityException("Report " + report + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Report report) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Report persistentReport = em.find(Report.class, report.getId());
            Account requestAccountOld = persistentReport.getRequestAccount();
            Account requestAccountNew = report.getRequestAccount();
            if (requestAccountNew != null) {
                requestAccountNew = em.getReference(requestAccountNew.getClass(), requestAccountNew.getUsername());
                report.setRequestAccount(requestAccountNew);
            }
            report = em.merge(report);
            if (requestAccountOld != null && !requestAccountOld.equals(requestAccountNew)) {
                requestAccountOld.getReportList().remove(report);
                requestAccountOld = em.merge(requestAccountOld);
            }
            if (requestAccountNew != null && !requestAccountNew.equals(requestAccountOld)) {
                requestAccountNew.getReportList().add(report);
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
                Integer id = report.getId();
                if (findReport(id) == null) {
                    throw new NonexistentEntityException("The report with id " + id + " no longer exists.");
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
            Report report;
            try {
                report = em.getReference(Report.class, id);
                report.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The report with id " + id + " no longer exists.", enfe);
            }
            Account requestAccount = report.getRequestAccount();
            if (requestAccount != null) {
                requestAccount.getReportList().remove(report);
                requestAccount = em.merge(requestAccount);
            }
            em.remove(report);
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

    public List<Report> findReportEntities() {
        return findReportEntities(true, -1, -1);
    }

    public List<Report> findReportEntities(int maxResults, int firstResult) {
        return findReportEntities(false, maxResults, firstResult);
    }

    private List<Report> findReportEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Report.class));
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

    public Report findReport(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Report.class, id);
        } finally {
            em.close();
        }
    }

    public int getReportCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Report> rt = cq.from(Report.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
