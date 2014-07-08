/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ui;

import biz.AccountManager;
import biz.DepartmentManager;
import entity.Account;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.view.ViewScoped;

/**
 *
 * @author Thao
 */
@ManagedBean
@ViewScoped
public class AccountUI {
    @EJB
    private AccountManager accountManager;
    
    @EJB
    private DepartmentManager departmentManager;

    private String username;
    private String password;
    private String confirmPassword;
    private String role;
    private String name;
    private Date birthday;
    private String email;
    private String phone;
    private int departmentId;
    private String msg;

    public AccountUI() {
        username = "";
        password = "";
        confirmPassword = "";
        msg = "";
    }
    
    public String addAccount() {
//        msg = Pattern.matches("[a-zA-Z0-9._-]{6,}", userName)+"";
//        return "";
        try {
            if (!Pattern.matches("[a-zA-Z0-9._-]{6,}", username)) {
                msg = "Username not valid";
                return "";
            }
            if (!Pattern.matches(".{6,}", password)) {
                msg = "Password not valid";
                return "";
            }
            if (!confirmPassword.equals(password)) {
                msg = "Confirm password not match";
                return "";
            }
            if (accountManager.isAccountExist(username)) {
                msg = "This username has already existed";
            }
            accountManager.create(new Account(username, password, role, name, birthday, email, phone, departmentManager.getDepartmentById(departmentId).get(0)));
            return "/faces/success.xhtml";
        } catch (Exception ex) {
            msg = "Can't register at the moment.";
            Logger.getLogger(AccountUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
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

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }
    
    
    
    
    
}
