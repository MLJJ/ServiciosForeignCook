package service;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import persistencia.Comentarios;
import persistencia.Recetas;

/**
 *
 * @author Miguel Leonardo Jimenez Jimenez
 * @date 13/05/2018
 * @time 08:08:53 PM
 */
@Stateless
@Path("persistencia.comentarios")
public class ComentariosFacadeREST extends AbstractFacade<Comentarios> {

    @PersistenceContext(unitName = "ForeignCookPU")
    private EntityManager em;

    public ComentariosFacadeREST() {
        super(Comentarios.class);
    }

    @POST
    @Override
    @Consumes({MediaType.APPLICATION_JSON})
    public void create(Comentarios entity) {
        super.create(entity);
    }
    
    @POST
    @Path("{idReceta}")
    @Consumes({MediaType.APPLICATION_JSON})
    public void guardarComentario(@PathParam("idReceta") Integer idReceta, Comentarios comentario) {
        EntityManager conexion = getEntityManager();
        
        Recetas receta = conexion.find(Recetas.class, idReceta);
        comentario.setIdReceta(receta);
        super.create(comentario);
    }

    @PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_JSON})
    public void edit(@PathParam("id") Integer id, Comentarios entity) {
        super.edit(entity);
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") Integer id) {
        super.remove(super.find(id));
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Comentarios find(@PathParam("id") Integer id) {
        return super.find(id);
    }
    
    /**
     * Retorna los comentarios de una receta
     * 
     * @param idReceta identificador de la receta
     * @return Comentarios de la receta
     */
    @GET
    @Path("comentariosRecetas/{idReceta}")
    @Produces({MediaType.APPLICATION_JSON})
    public List<Comentarios> buscarComentariosDeReceta(@PathParam("idReceta") Integer idReceta) {
        EntityManager conexion = getEntityManager();
        List<Comentarios> comentarios = null;
        
        try{
            comentarios = conexion.createQuery("SELECT c FROM Comentarios c WHERE c.idReceta.idReceta = :idReceta").setParameter("idReceta", idReceta).getResultList();
        }catch(Exception ex){
            ex.printStackTrace();
            comentarios = new ArrayList();
        }
        return comentarios;
    }

    @GET
    @Override
    @Produces({MediaType.APPLICATION_JSON})
    public List<Comentarios> findAll() {
        return super.findAll();
    }

    @GET
    @Path("{from}/{to}")
    @Produces({MediaType.APPLICATION_JSON})
    public List<Comentarios> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        return super.findRange(new int[]{from, to});
    }

    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public String countREST() {
        return String.valueOf(super.count());
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

}