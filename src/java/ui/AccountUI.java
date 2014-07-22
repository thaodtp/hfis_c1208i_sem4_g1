/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import biz.AccountManager;
import biz.DepartmentManager;
import entity.Account;
import entity.Department;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
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
    private Account account;
    private String username;
    private String password;
    private String confirmPassword;
    private int role;
    private String roleName;
    private String name;
    private String birthday;
    private String email;
    private String phone;
    private Department department;
    private String msg;
    private String keyword;

    public AccountUI() {
        username = "";
        password = "";
        confirmPassword = "";
        msg = "";
    }

    public List<Account> getAllAccounts() {
        return accountManager.getAllAccount();
    }

    public Account getAccountByUsername() {
        Account account;
        try {
            account = accountManager.getAccountByUsername(username);
            return account;
        } catch (Exception ex) {
            Logger.getLogger(AccountUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new Account();
    }

    public List<Account> getStaffs() {
        List<Account> result = accountManager.getAccountByRole(Account.ROLE_TECHNICAL);
        return result;
    }

    public String create() {
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
            accountManager.create(
                    new Account(username, 
                            password, 
                            role, 
                            name, 
                            new SimpleDateFormat("dd/MM/yyyy").parse(birthday), 
                            email, phone, department));
            return "/success.xhtml";
        } catch (Exception ex) {
            msg = "Can't register at the moment.";
            Logger.getLogger(AccountUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    public String deleteAccount() {
        try {
            accountManager.delete(username);
            return "/success.xhtml?faces-redirect=true";
        } catch (Exception ex) {
            msg = "Can't delete account";
            Logger.getLogger(AccountUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    public List<Account> Search() {
        try {
            return accountManager.searchAccByName(keyword);
        } catch (Exception ex) {
            msg = "Can't search account";
            Logger.getLogger(AccountUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new LinkedList<Account>();
    }

    public String editAccount() {
        if (!Pattern.matches("[a-zA-Z0-9._-]{5,}", username)) {
            msg = "Username not valid";
            return "";
        }
        if (!Pattern.matches(".{6,}", password)) {
            msg = "Password not valid";
            return "";
        }
        if (getName().isEmpty()) {
            msg = "Name not valid";
            return "";
        }
        if (!Pattern.matches("^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{4})$", phone)) {
            msg = "Phone number not valid";
            return "";
        }
//        if (!Pattern.matches("[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", getEmail())) {
//            msg = "Email not valid"+ getEmail();
//            return "";
//        }
        try { 
            account.setPassword(password);
            account.setName(name);
            account.setBirthday(new SimpleDateFormat("dd/MM/yyyy").parse(birthday));
            account.setPhone(phone);
            account.setEmail(email);
//            account.setRole(role);
          //  account.setDepartmentId(departmentManager.getDepartmentById(departmentId));
            account.setDepartmentId(department);
            accountManager.edit(account);
            return "/success.xhtml";
        } catch (Exception ex) {
            msg = "Can't update account";
            Logger.getLogger(AccountUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        account = accountManager.getAccountByUsername(username);
        if (account!=null) {
            password = account.getPassword();
            roleName = account.getRoleName();
            name = account.getName();
            birthday = account.getDisplayBirthday();
            email = account.getEmail();
            phone = account.getPhone();
            department = account.getDepartmentId();
        }
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

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
    
    

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }    
    
    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
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

    public Department getDepartmentId() {
        return department;
    }

    public void setDepartmentId(Department department) {
        this.department = department;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
