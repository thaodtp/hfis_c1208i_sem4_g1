/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author The
 */
@Entity
@Table(name = "Request")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Request.findAll", query = "SELECT r FROM Request r"),
    @NamedQuery(name = "Request.findById", query = "SELECT r FROM Request r WHERE r.id = :id"),
    @NamedQuery(name = "Request.findByStatus", query = "SELECT r FROM Request r WHERE r.status = :status"),
    @NamedQuery(name = "Request.findByType", query = "SELECT r FROM Request r WHERE r.type = :type")})
public class Request implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "Time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date time;
    public static final int TYPE_REQUEST = 1;
    public static final int TYPE_COMPLAINT = 2;
    public static final int TYPE_REPORT = 3;
    public static final int TYPE_MESSAGE = 4;

    public static final int STATUS_PENDING = 0;
    public static final int STATUS_COMPLETE = 1;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    //@NotNull
    @Column(name = "Id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 2147483647)
    @Column(name = "Content")
    private String content;
    @Column(name = "Status")
    private Integer status;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Type")
    private int type;
    @JoinColumn(name = "ResolveAccount", referencedColumnName = "Username")
    @ManyToOne(optional = true)
    private Account resolveAccount;
    @JoinColumn(name = "RequestAccount", referencedColumnName = "Username")
    @ManyToOne(optional = true)
    private Account requestAccount;

    public Request() {
    }

    public Request(Integer id) {
        this.id = id;
    }

    public Request(Integer id, String content, int type) {
        this.id = id;
        this.content = content;
        this.type = type;
    }

    public Request(String content, int status, int type, Account requestAccount, Account resolveAccount) {
        this.content = content;
        this.status = status;
        this.type = type;
        this.requestAccount = requestAccount;
        this.resolveAccount = resolveAccount;
    }

    public Request(String content, int type) {
        this.content = content;
        this.type = type;
    }

 
    
    public String getDisplayTime() {
        return new SimpleDateFormat("mm:hh  dd/MM/yyyy").format(time);
    }

    public String getSummary() {
        try {
            return this.content.substring(0, 11) + "...";
        } catch (IndexOutOfBoundsException ex) {
            return this.content;
        }
    }

    public String getStatusName() {
        switch (this.status) {
            case STATUS_COMPLETE:
                return "Complete";
            case STATUS_PENDING:
                if (resolveAccount == null) {
                    return "Unassigned";
                } else {
                    return "Pending";
                }
            default:
                return "Error";
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Account getResolveAccount() {
        return resolveAccount;
    }

    public void setResolveAccount(Account resolveAccount) {
        this.resolveAccount = resolveAccount;
    }

    public Account getRequestAccount() {
        return requestAccount;
    }

    public void setRequestAccount(Account requestAccount) {
        this.requestAccount = requestAccount;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Request)) {
            return false;
        }
        Request other = (Request) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Request[ id=" + id + " ]";
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

}
