/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import biz.AccountManager;
import biz.RequestManager;
import entity.Account;
import entity.Request;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author Thao
 */
@ManagedBean
@ViewScoped
public class RequestUI implements Serializable {

    @EJB
    private AccountManager accountManager;
    @EJB
    private RequestManager requestManager;

    @ManagedProperty(value = "#{login}")
    private Login loginBean;

    private Request currentReq;
    private String content;
    private int status;
    private int type;
    private int id;
    private String msg;
    private Account requestAccount;
    private Account resolveAccount;

    public void assignResolver() {
        currentReq.setResolveAccount(resolveAccount);
        requestManager.edit(currentReq);
    }

    public List<Request> getMessages() {
        return requestManager.getMessages(loginBean.getAccount().getUsername());
    }

    public void completeRequest() {
        currentReq.setStatus(Request.STATUS_COMPLETE);
        requestManager.edit(currentReq);
        sendMessage(currentReq.getRequestAccount(), "Your request has been resolved");
        List<Account> admins = accountManager.getAccountByRole(Account.ROLE_ADMIN);
        if (!admins.isEmpty()) {
            for (Account a : admins) {
                try {
                    sendMessage(a, currentReq.getResolveAccount().getName() + " has completed a request");
                } catch (Exception ex) {
                }
            }
        }
    }
    
    public List<Request> getMyTechnicalWork(){
        return requestManager.getUnresolvedComplaints(loginBean.getAccount().getUsername());
    }
    public List<Request> getUserComplaint() {
        List<Request> pending = requestManager.getRequests(
                loginBean.getAccount().getUsername(), 
                Request.TYPE_COMPLAINT, 
                Request.STATUS_PENDING);
        List<Request> completed = requestManager.getRequests(
                loginBean.getAccount().getUsername(), 
                Request.TYPE_COMPLAINT, 
                Request.STATUS_COMPLETE);
        pending.addAll(completed);
        return pending;
        
    }

    public void sendMessage(Account toUser, String content) {
        requestManager.sendMessage(toUser, content);
    }
    public Map<String, Integer> createStatistic(int type) {
        Map<String, Integer> result = new HashMap<>();
        List<Request> source = requestManager.getRequests(type);
        SimpleDateFormat sdf = new SimpleDateFormat("\"dd/MM/yyyy\"");
        for (Request req : source) {
            String date = sdf.format(req.getTime());
            if (result.containsKey(date)) {
                result.put(date, result.get(date) + 1);
            } else {
                if (result.size() > 5) {
                    break;
                } else {
                    result.put(date, 1);
                }
            }
        }
        return result;
    }

    public Request getCurrrentReq() {
        return currentReq;
    }

    public void setCurrrentReq(Request currrentReq) {
        this.currentReq = currrentReq;
    }

    public Map<String, Integer> getComplaintStatisticByDay() {
        return createStatistic(Request.TYPE_COMPLAINT);
    }

    public List<Request> getUnresolvedComplaint() {
        return requestManager.getRequestsByStatus(Request.STATUS_PENDING, Request.TYPE_COMPLAINT);
    }

    public List<Request> getUnresolvedRequest() {
        return requestManager.getRequestsByStatus(Request.STATUS_PENDING, Request.TYPE_REQUEST);
    }

    public List<Request> getAllRequest() {
        return requestManager.getRequests(Request.TYPE_REQUEST);
    }

    public List<Request> getAllComplaint() {
        return requestManager.getRequests(Request.TYPE_COMPLAINT);
    }
    
    public List<Request> getAllComplaintOfOneDepartment() {
        return requestManager.getAllComplaintOfOneDepartment(loginBean.getAccount().getDepartmentId());
    }

    public List<Request> getCompletedComplaints() {
        return requestManager.getRequestsByStatus(Request.STATUS_COMPLETE, Request.TYPE_COMPLAINT);
    }

    public String create() {
        try {
            if (content == null || content.isEmpty()) {
                msg = "Content too short";
                return "";
            }
            requestManager.create(new Request(content, new Date(),
                    Request.STATUS_PENDING,
                    Request.TYPE_COMPLAINT, loginBean.getAccount(), null));
//            requestManager.create(new Request(content, type));
            return "/success.xhtml";
        } catch (Exception ex) {
            msg = "Can't send complaint";
            Logger.getLogger(Request.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Account getRequestAccount() {
        return requestAccount;
    }

    public void setRequestAccount(Account requestAccount) {
        this.requestAccount = requestAccount;
    }

    public Account getResolveAccount() {
        return resolveAccount;
    }

    public void setResolveAccount(Account resolveAccount) {
        this.resolveAccount = resolveAccount;
    }

    public Login getLoginBean() {
        return loginBean;
    }

    public void setLoginBean(Login loginBean) {
        this.loginBean = loginBean;
    }

}
