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
import entity.Department;
import entity.Request;
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
public class DepartmentJpaController implements Serializable {

    public DepartmentJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public Department getDepartment(String name) {
        TypedQuery<Department> query = getEntityManager().createQuery("SELECT d FROM Department d WHERE d.name = :name", Department.class);
        List<Department> deps = query.getResultList();
        if (deps.isEmpty()) {
            return null;
        } else {
            return deps.get(0);
        }
    }

    public void create(Department department) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (department.getAccountList() == null) {
            department.setAccountList(new ArrayList<Account>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            List<Account> attachedAccountList = new ArrayList<Account>();
            for (Account accountListAccountToAttach : department.getAccountList()) {
                accountListAccountToAttach = em.getReference(accountListAccountToAttach.getClass(), accountListAccountToAttach.getUsername());
                attachedAccountList.add(accountListAccountToAttach);
            }
            department.setAccountList(attachedAccountList);
            em.persist(department);
            for (Account accountListAccount : department.getAccountList()) {
                Department oldDepartmentIdOfAccountListAccount = accountListAccount.getDepartmentId();
                accountListAccount.setDepartmentId(department);
                accountListAccount = em.merge(accountListAccount);
                if (oldDepartmentIdOfAccountListAccount != null) {
                    oldDepartmentIdOfAccountListAccount.getAccountList().remove(accountListAccount);
                    oldDepartmentIdOfAccountListAccount = em.merge(oldDepartmentIdOfAccountListAccount);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findDepartment(department.getId()) != null) {
                throw new PreexistingEntityException("Department " + department + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Department department) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Department persistentDepartment = em.find(Department.class, department.getId());
            List<Account> accountListOld = persistentDepartment.getAccountList();
            List<Account> accountListNew = department.getAccountList();
            List<Account> attachedAccountListNew = new ArrayList<Account>();
            for (Account accountListNewAccountToAttach : accountListNew) {
                accountListNewAccountToAttach = em.getReference(accountListNewAccountToAttach.getClass(), accountListNewAccountToAttach.getUsername());
                attachedAccountListNew.add(accountListNewAccountToAttach);
            }
            accountListNew = attachedAccountListNew;
            department.setAccountList(accountListNew);
            department = em.merge(department);
            for (Account accountListOldAccount : accountListOld) {
                if (!accountListNew.contains(accountListOldAccount)) {
                    accountListOldAccount.setDepartmentId(null);
                    accountListOldAccount = em.merge(accountListOldAccount);
                }
            }
            for (Account accountListNewAccount : accountListNew) {
                if (!accountListOld.contains(accountListNewAccount)) {
                    Department oldDepartmentIdOfAccountListNewAccount = accountListNewAccount.getDepartmentId();
                    accountListNewAccount.setDepartmentId(department);
                    accountListNewAccount = em.merge(accountListNewAccount);
                    if (oldDepartmentIdOfAccountListNewAccount != null && !oldDepartmentIdOfAccountListNewAccount.equals(department)) {
                        oldDepartmentIdOfAccountListNewAccount.getAccountList().remove(accountListNewAccount);
                        oldDepartmentIdOfAccountListNewAccount = em.merge(oldDepartmentIdOfAccountListNewAccount);
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
                Integer id = department.getId();
                if (findDepartment(id) == null) {
                    throw new NonexistentEntityException("The department with id " + id + " no longer exists.");
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
            Department department;
            try {
                department = em.getReference(Department.class, id);
                department.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The department with id " + id + " no longer exists.", enfe);
            }
            List<Account> accountList = department.getAccountList();
            for (Account accountListAccount : accountList) {
                accountListAccount.setDepartmentId(null);
                accountListAccount = em.merge(accountListAccount);
            }
            em.remove(department);
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

    public List<Department> findDepartmentEntities() {
        return findDepartmentEntities(true, -1, -1);
    }

    public List<Department> findDepartmentEntities(int maxResults, int firstResult) {
        return findDepartmentEntities(false, maxResults, firstResult);
    }

    private List<Department> findDepartmentEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Department.class));
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

    public Department findDepartment(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Department.class, id);
        } finally {
            em.close();
        }
    }

    public int getDepartmentCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Department> rt = cq.from(Department.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

    public List<Department> getDepartmentById(int departmentId) {
        String queryString = "SELECT d FROM Department d WHERE d.id = :id";
        TypedQuery<Department> query = getEntityManager().createQuery(queryString, Department.class);
        query.setParameter("id", departmentId);  
        return query.getResultList();
    }

}
