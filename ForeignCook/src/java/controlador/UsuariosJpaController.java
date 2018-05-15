/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package controlador;

import controlador.exceptions.NonexistentEntityException;
import controlador.exceptions.PreexistingEntityException;
import controlador.exceptions.RollbackFailureException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import persistencia.Recetas;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;
import persistencia.Usuarios;

/**
 *
 * @author Miguel Leonardo Jimenez Jimenez
 * @date 13/05/2018
 * @time 08:19:13 PM
 */
public class UsuariosJpaController implements Serializable {

    public UsuariosJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Usuarios usuarios) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (usuarios.getRecetasList() == null) {
            usuarios.setRecetasList(new ArrayList<Recetas>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            List<Recetas> attachedRecetasList = new ArrayList<Recetas>();
            for (Recetas recetasListRecetasToAttach : usuarios.getRecetasList()) {
                recetasListRecetasToAttach = em.getReference(recetasListRecetasToAttach.getClass(), recetasListRecetasToAttach.getIdReceta());
                attachedRecetasList.add(recetasListRecetasToAttach);
            }
            usuarios.setRecetasList(attachedRecetasList);
            em.persist(usuarios);
            for (Recetas recetasListRecetas : usuarios.getRecetasList()) {
                Usuarios oldCorreoOfRecetasListRecetas = recetasListRecetas.getCorreo();
                recetasListRecetas.setCorreo(usuarios);
                recetasListRecetas = em.merge(recetasListRecetas);
                if (oldCorreoOfRecetasListRecetas != null) {
                    oldCorreoOfRecetasListRecetas.getRecetasList().remove(recetasListRecetas);
                    oldCorreoOfRecetasListRecetas = em.merge(oldCorreoOfRecetasListRecetas);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findUsuarios(usuarios.getCorreo()) != null) {
                throw new PreexistingEntityException("Usuarios " + usuarios + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Usuarios usuarios) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Usuarios persistentUsuarios = em.find(Usuarios.class, usuarios.getCorreo());
            List<Recetas> recetasListOld = persistentUsuarios.getRecetasList();
            List<Recetas> recetasListNew = usuarios.getRecetasList();
            List<Recetas> attachedRecetasListNew = new ArrayList<Recetas>();
            for (Recetas recetasListNewRecetasToAttach : recetasListNew) {
                recetasListNewRecetasToAttach = em.getReference(recetasListNewRecetasToAttach.getClass(), recetasListNewRecetasToAttach.getIdReceta());
                attachedRecetasListNew.add(recetasListNewRecetasToAttach);
            }
            recetasListNew = attachedRecetasListNew;
            usuarios.setRecetasList(recetasListNew);
            usuarios = em.merge(usuarios);
            for (Recetas recetasListOldRecetas : recetasListOld) {
                if (!recetasListNew.contains(recetasListOldRecetas)) {
                    recetasListOldRecetas.setCorreo(null);
                    recetasListOldRecetas = em.merge(recetasListOldRecetas);
                }
            }
            for (Recetas recetasListNewRecetas : recetasListNew) {
                if (!recetasListOld.contains(recetasListNewRecetas)) {
                    Usuarios oldCorreoOfRecetasListNewRecetas = recetasListNewRecetas.getCorreo();
                    recetasListNewRecetas.setCorreo(usuarios);
                    recetasListNewRecetas = em.merge(recetasListNewRecetas);
                    if (oldCorreoOfRecetasListNewRecetas != null && !oldCorreoOfRecetasListNewRecetas.equals(usuarios)) {
                        oldCorreoOfRecetasListNewRecetas.getRecetasList().remove(recetasListNewRecetas);
                        oldCorreoOfRecetasListNewRecetas = em.merge(oldCorreoOfRecetasListNewRecetas);
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
                String id = usuarios.getCorreo();
                if (findUsuarios(id) == null) {
                    throw new NonexistentEntityException("The usuarios with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Usuarios usuarios;
            try {
                usuarios = em.getReference(Usuarios.class, id);
                usuarios.getCorreo();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The usuarios with id " + id + " no longer exists.", enfe);
            }
            List<Recetas> recetasList = usuarios.getRecetasList();
            for (Recetas recetasListRecetas : recetasList) {
                recetasListRecetas.setCorreo(null);
                recetasListRecetas = em.merge(recetasListRecetas);
            }
            em.remove(usuarios);
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

    public List<Usuarios> findUsuariosEntities() {
        return findUsuariosEntities(true, -1, -1);
    }

    public List<Usuarios> findUsuariosEntities(int maxResults, int firstResult) {
        return findUsuariosEntities(false, maxResults, firstResult);
    }

    private List<Usuarios> findUsuariosEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Usuarios.class));
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

    public Usuarios findUsuarios(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Usuarios.class, id);
        } finally {
            em.close();
        }
    }

    public int getUsuariosCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Usuarios> rt = cq.from(Usuarios.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
