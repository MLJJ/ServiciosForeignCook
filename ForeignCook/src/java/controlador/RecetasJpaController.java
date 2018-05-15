/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package controlador;

import controlador.exceptions.NonexistentEntityException;
import controlador.exceptions.RollbackFailureException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import persistencia.Usuarios;
import persistencia.Comentarios;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;
import persistencia.Recetas;

/**
 *
 * @author Miguel Leonardo Jimenez Jimenez
 * @date 13/05/2018
 * @time 08:19:13 PM
 */
public class RecetasJpaController implements Serializable {

    public RecetasJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Recetas recetas) throws RollbackFailureException, Exception {
        if (recetas.getComentariosList() == null) {
            recetas.setComentariosList(new ArrayList<Comentarios>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Usuarios correo = recetas.getCorreo();
            if (correo != null) {
                correo = em.getReference(correo.getClass(), correo.getCorreo());
                recetas.setCorreo(correo);
            }
            List<Comentarios> attachedComentariosList = new ArrayList<Comentarios>();
            for (Comentarios comentariosListComentariosToAttach : recetas.getComentariosList()) {
                comentariosListComentariosToAttach = em.getReference(comentariosListComentariosToAttach.getClass(), comentariosListComentariosToAttach.getIdComentario());
                attachedComentariosList.add(comentariosListComentariosToAttach);
            }
            recetas.setComentariosList(attachedComentariosList);
            em.persist(recetas);
            if (correo != null) {
                correo.getRecetasList().add(recetas);
                correo = em.merge(correo);
            }
            for (Comentarios comentariosListComentarios : recetas.getComentariosList()) {
                Recetas oldIdRecetaOfComentariosListComentarios = comentariosListComentarios.getIdReceta();
                comentariosListComentarios.setIdReceta(recetas);
                comentariosListComentarios = em.merge(comentariosListComentarios);
                if (oldIdRecetaOfComentariosListComentarios != null) {
                    oldIdRecetaOfComentariosListComentarios.getComentariosList().remove(comentariosListComentarios);
                    oldIdRecetaOfComentariosListComentarios = em.merge(oldIdRecetaOfComentariosListComentarios);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Recetas recetas) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Recetas persistentRecetas = em.find(Recetas.class, recetas.getIdReceta());
            Usuarios correoOld = persistentRecetas.getCorreo();
            Usuarios correoNew = recetas.getCorreo();
            List<Comentarios> comentariosListOld = persistentRecetas.getComentariosList();
            List<Comentarios> comentariosListNew = recetas.getComentariosList();
            if (correoNew != null) {
                correoNew = em.getReference(correoNew.getClass(), correoNew.getCorreo());
                recetas.setCorreo(correoNew);
            }
            List<Comentarios> attachedComentariosListNew = new ArrayList<Comentarios>();
            for (Comentarios comentariosListNewComentariosToAttach : comentariosListNew) {
                comentariosListNewComentariosToAttach = em.getReference(comentariosListNewComentariosToAttach.getClass(), comentariosListNewComentariosToAttach.getIdComentario());
                attachedComentariosListNew.add(comentariosListNewComentariosToAttach);
            }
            comentariosListNew = attachedComentariosListNew;
            recetas.setComentariosList(comentariosListNew);
            recetas = em.merge(recetas);
            if (correoOld != null && !correoOld.equals(correoNew)) {
                correoOld.getRecetasList().remove(recetas);
                correoOld = em.merge(correoOld);
            }
            if (correoNew != null && !correoNew.equals(correoOld)) {
                correoNew.getRecetasList().add(recetas);
                correoNew = em.merge(correoNew);
            }
            for (Comentarios comentariosListOldComentarios : comentariosListOld) {
                if (!comentariosListNew.contains(comentariosListOldComentarios)) {
                    comentariosListOldComentarios.setIdReceta(null);
                    comentariosListOldComentarios = em.merge(comentariosListOldComentarios);
                }
            }
            for (Comentarios comentariosListNewComentarios : comentariosListNew) {
                if (!comentariosListOld.contains(comentariosListNewComentarios)) {
                    Recetas oldIdRecetaOfComentariosListNewComentarios = comentariosListNewComentarios.getIdReceta();
                    comentariosListNewComentarios.setIdReceta(recetas);
                    comentariosListNewComentarios = em.merge(comentariosListNewComentarios);
                    if (oldIdRecetaOfComentariosListNewComentarios != null && !oldIdRecetaOfComentariosListNewComentarios.equals(recetas)) {
                        oldIdRecetaOfComentariosListNewComentarios.getComentariosList().remove(comentariosListNewComentarios);
                        oldIdRecetaOfComentariosListNewComentarios = em.merge(oldIdRecetaOfComentariosListNewComentarios);
                    }
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = recetas.getIdReceta();
                if (findRecetas(id) == null) {
                    throw new NonexistentEntityException("The recetas with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Recetas recetas;
            try {
                recetas = em.getReference(Recetas.class, id);
                recetas.getIdReceta();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The recetas with id " + id + " no longer exists.", enfe);
            }
            Usuarios correo = recetas.getCorreo();
            if (correo != null) {
                correo.getRecetasList().remove(recetas);
                correo = em.merge(correo);
            }
            List<Comentarios> comentariosList = recetas.getComentariosList();
            for (Comentarios comentariosListComentarios : comentariosList) {
                comentariosListComentarios.setIdReceta(null);
                comentariosListComentarios = em.merge(comentariosListComentarios);
            }
            em.remove(recetas);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Recetas> findRecetasEntities() {
        return findRecetasEntities(true, -1, -1);
    }

    public List<Recetas> findRecetasEntities(int maxResults, int firstResult) {
        return findRecetasEntities(false, maxResults, firstResult);
    }

    private List<Recetas> findRecetasEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Recetas.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Recetas findRecetas(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Recetas.class, id);
        } finally {
            em.close();
        }
    }

    public int getRecetasCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Recetas> rt = cq.from(Recetas.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
