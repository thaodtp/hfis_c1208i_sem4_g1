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
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
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
@Table(name = "LabSchedule")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "LabSchedule.findAll", query = "SELECT l FROM LabSchedule l"),
    @NamedQuery(name = "LabSchedule.findById", query = "SELECT l FROM LabSchedule l WHERE l.id = :id"),
    @NamedQuery(name = "LabSchedule.findBySlot", query = "SELECT l FROM LabSchedule l WHERE l.slot = :slot"),
    @NamedQuery(name = "LabSchedule.findByDate", query = "SELECT l FROM LabSchedule l WHERE l.date = :date")})
public class LabSchedule implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;
    @Column(name = "Slot")
    private Integer slot;
    @Column(name = "Date")
    @Temporal(TemporalType.DATE)
    private Date date;
    @JoinColumn(name = "SequenceId", referencedColumnName = "Id")
//    @Column(name="SequenceId")
    @ManyToOne
    private ScheduleSequence sequenceId;
    @JoinColumn(name = "LabId", referencedColumnName = "Id")
    @ManyToOne
    private Lab labId;

    public LabSchedule() {
    }

    public LabSchedule(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSlot() {
        return slot;
    }

    public void setSlot(Integer slot) {
        this.slot = slot;
    }

    public String getDisplayDate() {
        return new SimpleDateFormat("dd/MM/yyyy").format(date);
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public ScheduleSequence getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(ScheduleSequence sequenceId) {
        this.sequenceId = sequenceId;
    }

    public Lab getLabId() {
        return labId;
    }

    public void setLabId(Lab labId) {
        this.labId = labId;
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
        if (!(object instanceof LabSchedule)) {
            return false;
        }
        LabSchedule other = (LabSchedule) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.LabSchedule[ id=" + id + " ]";
    }
    
}
