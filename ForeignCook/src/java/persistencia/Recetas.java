/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package persistencia;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
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
 * @author Miguel Leonardo Jimenez Jimenez
 * @date 13/05/2018
 * @time 08:08:09 PM
 */
@Entity
@Table(name = "recetas")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Recetas.findAll", query = "SELECT r FROM Recetas r")
    , @NamedQuery(name = "Recetas.findByIdReceta", query = "SELECT r FROM Recetas r WHERE r.idReceta = :idReceta")
    , @NamedQuery(name = "Recetas.findByNombreReceta", query = "SELECT r FROM Recetas r WHERE r.nombreReceta = :nombreReceta")
    , @NamedQuery(name = "Recetas.findByIngredientes", query = "SELECT r FROM Recetas r WHERE r.ingredientes = :ingredientes")
    , @NamedQuery(name = "Recetas.findByProcedimiento", query = "SELECT r FROM Recetas r WHERE r.procedimiento = :procedimiento")
    , @NamedQuery(name = "Recetas.findByLikes", query = "SELECT r FROM Recetas r WHERE r.likes = :likes")
    , @NamedQuery(name = "Recetas.findByNombreImagen", query = "SELECT r FROM Recetas r WHERE r.nombreImagen = :nombreImagen")
    , @NamedQuery(name = "Recetas.findByLinkVideo", query = "SELECT r FROM Recetas r WHERE r.linkVideo = :linkVideo")})
public class Recetas implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idReceta")
    private Integer idReceta;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "nombreReceta")
    private String nombreReceta;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 5000)
    @Column(name = "ingredientes")
    private String ingredientes;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 5000)
    @Column(name = "procedimiento")
    private String procedimiento;
    @Column(name = "likes")
    private Integer likes;
    @Size(max = 10)
    @Column(name = "nombreImagen")
    private String nombreImagen;
    @Size(max = 200)
    @Column(name = "linkVideo")
    private String linkVideo;
    @JoinColumn(name = "correo", referencedColumnName = "correo")
    @ManyToOne
    private Usuarios correo;
    @OneToMany(mappedBy = "idReceta")
    private List<Comentarios> comentariosList;

    public Recetas() {
    }

    public Recetas(Integer idReceta) {
        this.idReceta = idReceta;
    }

    public Recetas(Integer idReceta, String nombreReceta, String ingredientes, String procedimiento) {
        this.idReceta = idReceta;
        this.nombreReceta = nombreReceta;
        this.ingredientes = ingredientes;
        this.procedimiento = procedimiento;
    }

    public Integer getIdReceta() {
        return idReceta;
    }

    public void setIdReceta(Integer idReceta) {
        this.idReceta = idReceta;
    }

    public String getNombreReceta() {
        return nombreReceta;
    }

    public void setNombreReceta(String nombreReceta) {
        this.nombreReceta = nombreReceta;
    }

    public String getIngredientes() {
        return ingredientes;
    }

    public void setIngredientes(String ingredientes) {
        this.ingredientes = ingredientes;
    }

    public String getProcedimiento() {
        return procedimiento;
    }

    public void setProcedimiento(String procedimiento) {
        this.procedimiento = procedimiento;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public String getNombreImagen() {
        return nombreImagen;
    }

    public void setNombreImagen(String nombreImagen) {
        this.nombreImagen = nombreImagen;
    }

    public String getLinkVideo() {
        return linkVideo;
    }

    public void setLinkVideo(String linkVideo) {
        this.linkVideo = linkVideo;
    }

    public Usuarios getCorreo() {
        return correo;
    }

    public void setCorreo(Usuarios correo) {
        this.correo = correo;
    }

    @XmlTransient
    public List<Comentarios> getComentariosList() {
        return comentariosList;
    }

    public void setComentariosList(List<Comentarios> comentariosList) {
        this.comentariosList = comentariosList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idReceta != null ? idReceta.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Recetas)) {
            return false;
        }
        Recetas other = (Recetas) object;
        if ((this.idReceta == null && other.idReceta != null) || (this.idReceta != null && !this.idReceta.equals(other.idReceta))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "persistencia.Recetas[ idReceta=" + idReceta + " ]";
    }

}
