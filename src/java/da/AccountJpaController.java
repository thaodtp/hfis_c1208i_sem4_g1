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
import entity.Message;
import java.util.ArrayList;
import java.util.List;
import entity.Complaint;
import entity.Request;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
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

    public void create(Account account) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (account.getMessageList() == null) {
            account.setMessageList(new ArrayList<Message>());
        }
        if (account.getMessageList1() == null) {
            account.setMessageList1(new ArrayList<Message>());
        }
        if (account.getComplaintList() == null) {
            account.setComplaintList(new ArrayList<Complaint>());
        }
        if (account.getComplaintList1() == null) {
            account.setComplaintList1(new ArrayList<Complaint>());
        }
        if (account.getRequestList() == null) {
            account.setRequestList(new ArrayList<Request>());
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
            List<Message> attachedMessageList = new ArrayList<Message>();
            for (Message messageListMessageToAttach : account.getMessageList()) {
                messageListMessageToAttach = em.getReference(messageListMessageToAttach.getClass(), messageListMessageToAttach.getId());
                attachedMessageList.add(messageListMessageToAttach);
            }
            account.setMessageList(attachedMessageList);
            List<Message> attachedMessageList1 = new ArrayList<Message>();
            for (Message messageList1MessageToAttach : account.getMessageList1()) {
                messageList1MessageToAttach = em.getReference(messageList1MessageToAttach.getClass(), messageList1MessageToAttach.getId());
                attachedMessageList1.add(messageList1MessageToAttach);
            }
            account.setMessageList1(attachedMessageList1);
            List<Complaint> attachedComplaintList = new ArrayList<Complaint>();
            for (Complaint complaintListComplaintToAttach : account.getComplaintList()) {
                complaintListComplaintToAttach = em.getReference(complaintListComplaintToAttach.getClass(), complaintListComplaintToAttach.getId());
                attachedComplaintList.add(complaintListComplaintToAttach);
            }
            account.setComplaintList(attachedComplaintList);
            List<Complaint> attachedComplaintList1 = new ArrayList<Complaint>();
            for (Complaint complaintList1ComplaintToAttach : account.getComplaintList1()) {
                complaintList1ComplaintToAttach = em.getReference(complaintList1ComplaintToAttach.getClass(), complaintList1ComplaintToAttach.getId());
                attachedComplaintList1.add(complaintList1ComplaintToAttach);
            }
            account.setComplaintList1(attachedComplaintList1);
            List<Request> attachedRequestList = new ArrayList<Request>();
            for (Request requestListRequestToAttach : account.getRequestList()) {
                requestListRequestToAttach = em.getReference(requestListRequestToAttach.getClass(), requestListRequestToAttach.getId());
                attachedRequestList.add(requestListRequestToAttach);
            }
            account.setRequestList(attachedRequestList);
            em.persist(account);
            if (departmentId != null) {
                departmentId.getAccountList().add(account);
                departmentId = em.merge(departmentId);
            }
            for (Message messageListMessage : account.getMessageList()) {
                Account oldReceiveAccountOfMessageListMessage = messageListMessage.getReceiveAccount();
                messageListMessage.setReceiveAccount(account);
                messageListMessage = em.merge(messageListMessage);
                if (oldReceiveAccountOfMessageListMessage != null) {
                    oldReceiveAccountOfMessageListMessage.getMessageList().remove(messageListMessage);
                    oldReceiveAccountOfMessageListMessage = em.merge(oldReceiveAccountOfMessageListMessage);
                }
            }
            for (Message messageList1Message : account.getMessageList1()) {
                Account oldSendAccountOfMessageList1Message = messageList1Message.getSendAccount();
                messageList1Message.setSendAccount(account);
                messageList1Message = em.merge(messageList1Message);
                if (oldSendAccountOfMessageList1Message != null) {
                    oldSendAccountOfMessageList1Message.getMessageList1().remove(messageList1Message);
                    oldSendAccountOfMessageList1Message = em.merge(oldSendAccountOfMessageList1Message);
                }
            }
            for (Complaint complaintListComplaint : account.getComplaintList()) {
                Account oldResolveAccountOfComplaintListComplaint = complaintListComplaint.getResolveAccount();
                complaintListComplaint.setResolveAccount(account);
                complaintListComplaint = em.merge(complaintListComplaint);
                if (oldResolveAccountOfComplaintListComplaint != null) {
                    oldResolveAccountOfComplaintListComplaint.getComplaintList().remove(complaintListComplaint);
                    oldResolveAccountOfComplaintListComplaint = em.merge(oldResolveAccountOfComplaintListComplaint);
                }
            }
            for (Complaint complaintList1Complaint : account.getComplaintList1()) {
                Account oldComplainAccountOfComplaintList1Complaint = complaintList1Complaint.getComplainAccount();
                complaintList1Complaint.setComplainAccount(account);
                complaintList1Complaint = em.merge(complaintList1Complaint);
                if (oldComplainAccountOfComplaintList1Complaint != null) {
                    oldComplainAccountOfComplaintList1Complaint.getComplaintList1().remove(complaintList1Complaint);
                    oldComplainAccountOfComplaintList1Complaint = em.merge(oldComplainAccountOfComplaintList1Complaint);
                }
            }
            for (Request requestListRequest : account.getRequestList()) {
                Account oldRequestAccountOfRequestListRequest = requestListRequest.getRequestAccount();
                requestListRequest.setRequestAccount(account);
                requestListRequest = em.merge(requestListRequest);
                if (oldRequestAccountOfRequestListRequest != null) {
                    oldRequestAccountOfRequestListRequest.getRequestList().remove(requestListRequest);
                    oldRequestAccountOfRequestListRequest = em.merge(oldRequestAccountOfRequestListRequest);
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

    public void edit(Account account) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Account persistentAccount = em.find(Account.class, account.getUsername());
            Department departmentIdOld = persistentAccount.getDepartmentId();
            Department departmentIdNew = account.getDepartmentId();
            List<Message> messageListOld = persistentAccount.getMessageList();
            List<Message> messageListNew = account.getMessageList();
            List<Message> messageList1Old = persistentAccount.getMessageList1();
            List<Message> messageList1New = account.getMessageList1();
            List<Complaint> complaintListOld = persistentAccount.getComplaintList();
            List<Complaint> complaintListNew = account.getComplaintList();
            List<Complaint> complaintList1Old = persistentAccount.getComplaintList1();
            List<Complaint> complaintList1New = account.getComplaintList1();
            List<Request> requestListOld = persistentAccount.getRequestList();
            List<Request> requestListNew = account.getRequestList();
            List<String> illegalOrphanMessages = null;
            for (Message messageListOldMessage : messageListOld) {
                if (!messageListNew.contains(messageListOldMessage)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Message " + messageListOldMessage + " since its receiveAccount field is not nullable.");
                }
            }
            for (Message messageList1OldMessage : messageList1Old) {
                if (!messageList1New.contains(messageList1OldMessage)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Message " + messageList1OldMessage + " since its sendAccount field is not nullable.");
                }
            }
            for (Complaint complaintListOldComplaint : complaintListOld) {
                if (!complaintListNew.contains(complaintListOldComplaint)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Complaint " + complaintListOldComplaint + " since its resolveAccount field is not nullable.");
                }
            }
            for (Complaint complaintList1OldComplaint : complaintList1Old) {
                if (!complaintList1New.contains(complaintList1OldComplaint)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Complaint " + complaintList1OldComplaint + " since its complainAccount field is not nullable.");
                }
            }
            for (Request requestListOldRequest : requestListOld) {
                if (!requestListNew.contains(requestListOldRequest)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Request " + requestListOldRequest + " since its requestAccount field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (departmentIdNew != null) {
                departmentIdNew = em.getReference(departmentIdNew.getClass(), departmentIdNew.getId());
                account.setDepartmentId(departmentIdNew);
            }
            List<Message> attachedMessageListNew = new ArrayList<Message>();
            for (Message messageListNewMessageToAttach : messageListNew) {
                messageListNewMessageToAttach = em.getReference(messageListNewMessageToAttach.getClass(), messageListNewMessageToAttach.getId());
                attachedMessageListNew.add(messageListNewMessageToAttach);
            }
            messageListNew = attachedMessageListNew;
            account.setMessageList(messageListNew);
            List<Message> attachedMessageList1New = new ArrayList<Message>();
            for (Message messageList1NewMessageToAttach : messageList1New) {
                messageList1NewMessageToAttach = em.getReference(messageList1NewMessageToAttach.getClass(), messageList1NewMessageToAttach.getId());
                attachedMessageList1New.add(messageList1NewMessageToAttach);
            }
            messageList1New = attachedMessageList1New;
            account.setMessageList1(messageList1New);
            List<Complaint> attachedComplaintListNew = new ArrayList<Complaint>();
            for (Complaint complaintListNewComplaintToAttach : complaintListNew) {
                complaintListNewComplaintToAttach = em.getReference(complaintListNewComplaintToAttach.getClass(), complaintListNewComplaintToAttach.getId());
                attachedComplaintListNew.add(complaintListNewComplaintToAttach);
            }
            complaintListNew = attachedComplaintListNew;
            account.setComplaintList(complaintListNew);
            List<Complaint> attachedComplaintList1New = new ArrayList<Complaint>();
            for (Complaint complaintList1NewComplaintToAttach : complaintList1New) {
                complaintList1NewComplaintToAttach = em.getReference(complaintList1NewComplaintToAttach.getClass(), complaintList1NewComplaintToAttach.getId());
                attachedComplaintList1New.add(complaintList1NewComplaintToAttach);
            }
            complaintList1New = attachedComplaintList1New;
            account.setComplaintList1(complaintList1New);
            List<Request> attachedRequestListNew = new ArrayList<Request>();
            for (Request requestListNewRequestToAttach : requestListNew) {
                requestListNewRequestToAttach = em.getReference(requestListNewRequestToAttach.getClass(), requestListNewRequestToAttach.getId());
                attachedRequestListNew.add(requestListNewRequestToAttach);
            }
            requestListNew = attachedRequestListNew;
            account.setRequestList(requestListNew);
            account = em.merge(account);
            if (departmentIdOld != null && !departmentIdOld.equals(departmentIdNew)) {
                departmentIdOld.getAccountList().remove(account);
                departmentIdOld = em.merge(departmentIdOld);
            }
            if (departmentIdNew != null && !departmentIdNew.equals(departmentIdOld)) {
                departmentIdNew.getAccountList().add(account);
                departmentIdNew = em.merge(departmentIdNew);
            }
            for (Message messageListNewMessage : messageListNew) {
                if (!messageListOld.contains(messageListNewMessage)) {
                    Account oldReceiveAccountOfMessageListNewMessage = messageListNewMessage.getReceiveAccount();
                    messageListNewMessage.setReceiveAccount(account);
                    messageListNewMessage = em.merge(messageListNewMessage);
                    if (oldReceiveAccountOfMessageListNewMessage != null && !oldReceiveAccountOfMessageListNewMessage.equals(account)) {
                        oldReceiveAccountOfMessageListNewMessage.getMessageList().remove(messageListNewMessage);
                        oldReceiveAccountOfMessageListNewMessage = em.merge(oldReceiveAccountOfMessageListNewMessage);
                    }
                }
            }
            for (Message messageList1NewMessage : messageList1New) {
                if (!messageList1Old.contains(messageList1NewMessage)) {
                    Account oldSendAccountOfMessageList1NewMessage = messageList1NewMessage.getSendAccount();
                    messageList1NewMessage.setSendAccount(account);
                    messageList1NewMessage = em.merge(messageList1NewMessage);
                    if (oldSendAccountOfMessageList1NewMessage != null && !oldSendAccountOfMessageList1NewMessage.equals(account)) {
                        oldSendAccountOfMessageList1NewMessage.getMessageList1().remove(messageList1NewMessage);
                        oldSendAccountOfMessageList1NewMessage = em.merge(oldSendAccountOfMessageList1NewMessage);
                    }
                }
            }
            for (Complaint complaintListNewComplaint : complaintListNew) {
                if (!complaintListOld.contains(complaintListNewComplaint)) {
                    Account oldResolveAccountOfComplaintListNewComplaint = complaintListNewComplaint.getResolveAccount();
                    complaintListNewComplaint.setResolveAccount(account);
                    complaintListNewComplaint = em.merge(complaintListNewComplaint);
                    if (oldResolveAccountOfComplaintListNewComplaint != null && !oldResolveAccountOfComplaintListNewComplaint.equals(account)) {
                        oldResolveAccountOfComplaintListNewComplaint.getComplaintList().remove(complaintListNewComplaint);
                        oldResolveAccountOfComplaintListNewComplaint = em.merge(oldResolveAccountOfComplaintListNewComplaint);
                    }
                }
            }
            for (Complaint complaintList1NewComplaint : complaintList1New) {
                if (!complaintList1Old.contains(complaintList1NewComplaint)) {
                    Account oldComplainAccountOfComplaintList1NewComplaint = complaintList1NewComplaint.getComplainAccount();
                    complaintList1NewComplaint.setComplainAccount(account);
                    complaintList1NewComplaint = em.merge(complaintList1NewComplaint);
                    if (oldComplainAccountOfComplaintList1NewComplaint != null && !oldComplainAccountOfComplaintList1NewComplaint.equals(account)) {
                        oldComplainAccountOfComplaintList1NewComplaint.getComplaintList1().remove(complaintList1NewComplaint);
                        oldComplainAccountOfComplaintList1NewComplaint = em.merge(oldComplainAccountOfComplaintList1NewComplaint);
                    }
                }
            }
            for (Request requestListNewRequest : requestListNew) {
                if (!requestListOld.contains(requestListNewRequest)) {
                    Account oldRequestAccountOfRequestListNewRequest = requestListNewRequest.getRequestAccount();
                    requestListNewRequest.setRequestAccount(account);
                    requestListNewRequest = em.merge(requestListNewRequest);
                    if (oldRequestAccountOfRequestListNewRequest != null && !oldRequestAccountOfRequestListNewRequest.equals(account)) {
                        oldRequestAccountOfRequestListNewRequest.getRequestList().remove(requestListNewRequest);
                        oldRequestAccountOfRequestListNewRequest = em.merge(oldRequestAccountOfRequestListNewRequest);
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

    public void destroy(String id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
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
            List<String> illegalOrphanMessages = null;
            List<Message> messageListOrphanCheck = account.getMessageList();
            for (Message messageListOrphanCheckMessage : messageListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Account (" + account + ") cannot be destroyed since the Message " + messageListOrphanCheckMessage + " in its messageList field has a non-nullable receiveAccount field.");
            }
            List<Message> messageList1OrphanCheck = account.getMessageList1();
            for (Message messageList1OrphanCheckMessage : messageList1OrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Account (" + account + ") cannot be destroyed since the Message " + messageList1OrphanCheckMessage + " in its messageList1 field has a non-nullable sendAccount field.");
            }
            List<Complaint> complaintListOrphanCheck = account.getComplaintList();
            for (Complaint complaintListOrphanCheckComplaint : complaintListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Account (" + account + ") cannot be destroyed since the Complaint " + complaintListOrphanCheckComplaint + " in its complaintList field has a non-nullable resolveAccount field.");
            }
            List<Complaint> complaintList1OrphanCheck = account.getComplaintList1();
            for (Complaint complaintList1OrphanCheckComplaint : complaintList1OrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Account (" + account + ") cannot be destroyed since the Complaint " + complaintList1OrphanCheckComplaint + " in its complaintList1 field has a non-nullable complainAccount field.");
            }
            List<Request> requestListOrphanCheck = account.getRequestList();
            for (Request requestListOrphanCheckRequest : requestListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Account (" + account + ") cannot be destroyed since the Request " + requestListOrphanCheckRequest + " in its requestList field has a non-nullable requestAccount field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Department departmentId = account.getDepartmentId();
            if (departmentId != null) {
                departmentId.getAccountList().remove(account);
                departmentId = em.merge(departmentId);
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
    
}
