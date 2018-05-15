package controlador;

import controlador.exceptions.NonexistentEntityException;
import controlador.exceptions.RollbackFailureException;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.UserTransaction;
import persistencia.Comentarios;
import persistencia.Recetas;

/**
 *
 * @author Miguel Leonardo Jimenez Jimenez
 * @date 13/05/2018
 * @time 08:19:13 PM
 */
public class ComentariosJpaController implements Serializable {

    public ComentariosJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Comentarios comentarios) throws RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Recetas idReceta = comentarios.getIdReceta();
            if (idReceta != null) {
                idReceta = em.getReference(idReceta.getClass(), idReceta.getIdReceta());
                comentarios.setIdReceta(idReceta);
            }
            em.persist(comentarios);
            if (idReceta != null) {
                idReceta.getComentariosList().add(comentarios);
                idReceta = em.merge(idReceta);
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

    public void edit(Comentarios comentarios) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Comentarios persistentComentarios = em.find(Comentarios.class, comentarios.getIdComentario());
            Recetas idRecetaOld = persistentComentarios.getIdReceta();
            Recetas idRecetaNew = comentarios.getIdReceta();
            if (idRecetaNew != null) {
                idRecetaNew = em.getReference(idRecetaNew.getClass(), idRecetaNew.getIdReceta());
                comentarios.setIdReceta(idRecetaNew);
            }
            comentarios = em.merge(comentarios);
            if (idRecetaOld != null && !idRecetaOld.equals(idRecetaNew)) {
                idRecetaOld.getComentariosList().remove(comentarios);
                idRecetaOld = em.merge(idRecetaOld);
            }
            if (idRecetaNew != null && !idRecetaNew.equals(idRecetaOld)) {
                idRecetaNew.getComentariosList().add(comentarios);
                idRecetaNew = em.merge(idRecetaNew);
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
                Integer id = comentarios.getIdComentario();
                if (findComentarios(id) == null) {
                    throw new NonexistentEntityException("The comentarios with id " + id + " no longer exists.");
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
            Comentarios comentarios;
            try {
                comentarios = em.getReference(Comentarios.class, id);
                comentarios.getIdComentario();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The comentarios with id " + id + " no longer exists.", enfe);
            }
            Recetas idReceta = comentarios.getIdReceta();
            if (idReceta != null) {
                idReceta.getComentariosList().remove(comentarios);
                idReceta = em.merge(idReceta);
            }
            em.remove(comentarios);
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

    public List<Comentarios> findComentariosEntities() {
        return findComentariosEntities(true, -1, -1);
    }

    public List<Comentarios> findComentariosEntities(int maxResults, int firstResult) {
        return findComentariosEntities(false, maxResults, firstResult);
    }

    private List<Comentarios> findComentariosEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Comentarios.class));
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

    public Comentarios findComentarios(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Comentarios.class, id);
        } finally {
            em.close();
        }
    }

    public int getComentariosCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Comentarios> rt = cq.from(Comentarios.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
