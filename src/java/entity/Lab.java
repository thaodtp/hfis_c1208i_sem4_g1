/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entity;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
@Table(name = "Lab")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Lab.findAll", query = "SELECT l FROM Lab l"),
    @NamedQuery(name = "Lab.findById", query = "SELECT l FROM Lab l WHERE l.id = :id"),
    @NamedQuery(name = "Lab.findByName", query = "SELECT l FROM Lab l WHERE l.name = :name"),
    @NamedQuery(name = "Lab.findByType", query = "SELECT l FROM Lab l WHERE l.type = :type"),
    @NamedQuery(name = "Lab.findByStatus", query = "SELECT l FROM Lab l WHERE l.status = :status")})
public class Lab implements Serializable {
    public static final int ROOM_LAB = 1;
    public static final int ROOM_SERVER = 2;
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "Name")
    private String name;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Type")
    private int type;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Status")
    private int status;
    @OneToMany(mappedBy = "labId")
    private List<LabSchedule> labScheduleList;

    public Lab() {
    }

    public Lab(Integer id) {
        this.id = id;
    }

    public Lab(Integer id, String name, int type, int status) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @XmlTransient
    public List<LabSchedule> getLabScheduleList() {
        return labScheduleList;
    }

    public void setLabScheduleList(List<LabSchedule> labScheduleList) {
        this.labScheduleList = labScheduleList;
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
        if (!(object instanceof Lab)) {
            return false;
        }
        Lab other = (Lab) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Lab[ id=" + id + " ]";
    }
    
}
