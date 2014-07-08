/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;



import biz.AccountManager;
import entity.Account;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ManagedBean
@SessionScoped
public class Login {
    @EJB
    private AccountManager accountManager;

    private String username;
    private String password;
    private Account account;
    private String msg;

    public String getMsg() {
        String t = msg;
        msg = "";
        return t;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean haveMsg() {
        return !msg.isEmpty();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
    
    

    /**
     * Creates a new instance of Login
     */
    public Login() {
        msg = "";
    }

    public void login() {
        try {           
         //   userInfo = new UserInfoDA().getUserInfo(userName, password);
            account = accountManager.getAccount(username, password);
            if (account != null) {
                msg = "";
                return;
            }
            msg = "Username or Password not correct!";
        } catch (Exception ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void returnHome() {
        try {
            FacesContext currentInstance = FacesContext.getCurrentInstance();
            ((HttpServletResponse)currentInstance.getExternalContext().getResponse())
                    .sendRedirect("/");
        } catch (IOException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

//    public String getGuidePerm() {
//        if (account == null ||account.getIsGuide()==false) {
//            msg = "You don't have guide permission to access that resource";
//            returnHome();
//        }
//        return "";
//    }

    public String getAutoLogin() {
        if (account == null) {
            msg = "You don't have user permission to access that resource";
            returnHome();
        }
        return "";
    }

    public void logout() {
        account = null;
        username = "";
        password = "";
    }

}
