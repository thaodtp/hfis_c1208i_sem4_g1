/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author The
 */
@Entity
@Table(name = "Software")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Software.findAll", query = "SELECT s FROM Software s"),
    @NamedQuery(name = "Software.findById", query = "SELECT s FROM Software s WHERE s.id = :id"),
    @NamedQuery(name = "Software.findByName", query = "SELECT s FROM Software s WHERE s.name = :name"),
    @NamedQuery(name = "Software.findByExperiationDate", query = "SELECT s FROM Software s WHERE s.experiationDate = :experiationDate")})
public class Software implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "Id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "Name")
    private String name;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 2147483647)
    @Column(name = "InstallationProcedure")
    private String installationProcedure;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "ExperiationDate")
    private String experiationDate;

    public Software() {
    }

    public Software(Integer id) {
        this.id = id;
    }

    public Software(Integer id, String name, String installationProcedure, String experiationDate) {
        this.id = id;
        this.name = name;
        this.installationProcedure = installationProcedure;
        this.experiationDate = experiationDate;
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

    public String getInstallationProcedure() {
        return installationProcedure;
    }

    public void setInstallationProcedure(String installationProcedure) {
        this.installationProcedure = installationProcedure;
    }

    public String getExperiationDate() {
        return experiationDate;
    }

    public void setExperiationDate(String experiationDate) {
        this.experiationDate = experiationDate;
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
        if (!(object instanceof Software)) {
            return false;
        }
        Software other = (Software) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Software[ id=" + id + " ]";
    }
    
}
