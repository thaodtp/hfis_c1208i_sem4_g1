/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author The
 */
@Entity
@Table(name = "ScheduleSequence")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ScheduleSequence.findAll", query = "SELECT s FROM ScheduleSequence s"),
    @NamedQuery(name = "ScheduleSequence.findById", query = "SELECT s FROM ScheduleSequence s WHERE s.id = :id"),
    @NamedQuery(name = "ScheduleSequence.findByDetail", query = "SELECT s FROM ScheduleSequence s WHERE s.detail = :detail"),
    @NamedQuery(name = "ScheduleSequence.findByStatus", query = "SELECT s FROM ScheduleSequence s WHERE s.status = :status")})
public class ScheduleSequence implements Serializable {

    public static final int STATUS_PENDING = 0;
    public static final int STATUS_ACCEPTED = 1;
    public static final int STATUS_DENIED = 2;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "Id")
    private Integer id;
    @Size(max = 50)
    @Column(name = "Detail")
    private String detail;
    @Column(name = "Status")
    private Integer status;
    @OneToMany(mappedBy = "sequenceId", cascade = CascadeType.ALL)
    private List<LabSchedule> labScheduleList = new LinkedList<>();
    @JoinColumn(name = "RequestAccount", referencedColumnName = "Username")
    @ManyToOne
    private Account requestAccount;

    public ScheduleSequence() {
    }

    public ScheduleSequence(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSummary() {
        try {
            return this.detail.substring(0, 11) + "...";
        } catch (IndexOutOfBoundsException ex) {
            return this.detail;
        }
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getStatuss() {
        switch (status) {
            case STATUS_ACCEPTED:
                return "Accepted";
            case STATUS_DENIED:
                return "Denied";
            case STATUS_PENDING:
                return "Pending";
            default:
                return "";
        }
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @XmlTransient
    public List<LabSchedule> getLabScheduleList() {
        return labScheduleList;
    }

    public void setLabScheduleList(List<LabSchedule> labScheduleList) {
        this.labScheduleList = labScheduleList;
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
        if (!(object instanceof ScheduleSequence)) {
            return false;
        }
        ScheduleSequence other = (ScheduleSequence) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.ScheduleSequence[ id=" + id + " ]";
    }

}
