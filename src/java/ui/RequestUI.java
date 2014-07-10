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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author Thao
 */
@ManagedBean
@ViewScoped
public class RequestUI {
    @EJB
    private AccountManager accountManager;
    @EJB
    private RequestManager requestManager;
    
    

    private String content;
    private int status;
    private int type;
    private String msg;
    private Account requestAccount;
    private Account resolveAccount;
    
    public List<Request> getAllRequest(){
        return requestManager.getRequests(Request.TYPE_REQUEST);
    }
    
    public List<Request> getAllComplaint(){
        return requestManager.getRequests(Request.TYPE_COMPLAINT);
    }
    
    public String create() {
        try {
            if (content == null || content.isEmpty()) {
                msg = "Place name is not valid";
                return "";
            }
            requestManager.create(new Request(content, status, type, requestAccount, resolveAccount));
            return "/faces/guide/success.xhtml";
        } catch (Exception ex) {
            msg = "Can't add this place";
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
    
    
    
}
