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
import entity.Message;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author The
 */
public class MessageJpaController implements Serializable {

    public MessageJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Message message) throws PreexistingEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Account receiveAccount = message.getReceiveAccount();
            if (receiveAccount != null) {
                receiveAccount = em.getReference(receiveAccount.getClass(), receiveAccount.getUsername());
                message.setReceiveAccount(receiveAccount);
            }
            Account sendAccount = message.getSendAccount();
            if (sendAccount != null) {
                sendAccount = em.getReference(sendAccount.getClass(), sendAccount.getUsername());
                message.setSendAccount(sendAccount);
            }
            em.persist(message);
            if (receiveAccount != null) {
                receiveAccount.getMessageList().add(message);
                receiveAccount = em.merge(receiveAccount);
            }
            if (sendAccount != null) {
                sendAccount.getMessageList().add(message);
                sendAccount = em.merge(sendAccount);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findMessage(message.getId()) != null) {
                throw new PreexistingEntityException("Message " + message + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Message message) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Message persistentMessage = em.find(Message.class, message.getId());
            Account receiveAccountOld = persistentMessage.getReceiveAccount();
            Account receiveAccountNew = message.getReceiveAccount();
            Account sendAccountOld = persistentMessage.getSendAccount();
            Account sendAccountNew = message.getSendAccount();
            if (receiveAccountNew != null) {
                receiveAccountNew = em.getReference(receiveAccountNew.getClass(), receiveAccountNew.getUsername());
                message.setReceiveAccount(receiveAccountNew);
            }
            if (sendAccountNew != null) {
                sendAccountNew = em.getReference(sendAccountNew.getClass(), sendAccountNew.getUsername());
                message.setSendAccount(sendAccountNew);
            }
            message = em.merge(message);
            if (receiveAccountOld != null && !receiveAccountOld.equals(receiveAccountNew)) {
                receiveAccountOld.getMessageList().remove(message);
                receiveAccountOld = em.merge(receiveAccountOld);
            }
            if (receiveAccountNew != null && !receiveAccountNew.equals(receiveAccountOld)) {
                receiveAccountNew.getMessageList().add(message);
                receiveAccountNew = em.merge(receiveAccountNew);
            }
            if (sendAccountOld != null && !sendAccountOld.equals(sendAccountNew)) {
                sendAccountOld.getMessageList().remove(message);
                sendAccountOld = em.merge(sendAccountOld);
            }
            if (sendAccountNew != null && !sendAccountNew.equals(sendAccountOld)) {
                sendAccountNew.getMessageList().add(message);
                sendAccountNew = em.merge(sendAccountNew);
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
                Integer id = message.getId();
                if (findMessage(id) == null) {
                    throw new NonexistentEntityException("The message with id " + id + " no longer exists.");
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
            Message message;
            try {
                message = em.getReference(Message.class, id);
                message.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The message with id " + id + " no longer exists.", enfe);
            }
            Account receiveAccount = message.getReceiveAccount();
            if (receiveAccount != null) {
                receiveAccount.getMessageList().remove(message);
                receiveAccount = em.merge(receiveAccount);
            }
            Account sendAccount = message.getSendAccount();
            if (sendAccount != null) {
                sendAccount.getMessageList().remove(message);
                sendAccount = em.merge(sendAccount);
            }
            em.remove(message);
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

    public List<Message> findMessageEntities() {
        return findMessageEntities(true, -1, -1);
    }

    public List<Message> findMessageEntities(int maxResults, int firstResult) {
        return findMessageEntities(false, maxResults, firstResult);
    }

    private List<Message> findMessageEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Message.class));
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

    public Message findMessage(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Message.class, id);
        } finally {
            em.close();
        }
    }

    public int getMessageCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Message> rt = cq.from(Message.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
