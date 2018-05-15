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
import persistencia.Recetas;
import persistencia.Usuarios;

/**
 *
 * @author Miguel Leonardo Jimenez Jimenez
 * @date 13/05/2018
 * @time 08:08:56 PM
 */
@Stateless
@Path("persistencia.recetas")
public class RecetasFacadeREST extends AbstractFacade<Recetas> {

    @PersistenceContext(unitName = "ForeignCookPU")
    private EntityManager em;

    public RecetasFacadeREST() {
        super(Recetas.class);
    }

    @POST
    @Override
    @Consumes({MediaType.APPLICATION_JSON})
    public void create(Recetas entity) {
        super.create(entity);
    }
    
    @POST
    @Path("{correo}")
    @Consumes({MediaType.APPLICATION_JSON})
    public void guardarReceta(@PathParam("correo") String correo, Recetas receta) {
        EntityManager conexion = getEntityManager();
        Usuarios usuario = conexion.find(Usuarios.class,correo);
        receta.setCorreo(usuario);
        super.create(receta);
    }

    @PUT
    @Path("{correo}")
    @Consumes({MediaType.APPLICATION_JSON})
    public void edit(@PathParam("correo") String correo, Recetas receta) {
        EntityManager conexion = getEntityManager();
        try {
            Usuarios usuario = conexion.find(Usuarios.class, correo);
            receta.setCorreo(usuario);
            super.edit(receta);
        } catch (Exception e) {

        }
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") Integer id) {
        super.remove(super.find(id));
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Recetas find(@PathParam("id") Integer id) {
        return super.find(id);
    }

    @GET
    @Override
    @Produces({MediaType.APPLICATION_JSON})
    public List<Recetas> findAll() {
        return super.findAll();
    }

    @GET
    @Path("{from}/{to}")
    @Produces({MediaType.APPLICATION_JSON})
    public List<Recetas> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        return super.findRange(new int[]{from, to});
    }
    
    @GET
    @Path("buscarPorNombre/{nombre}")
    @Produces({MediaType.APPLICATION_JSON})
    public List<Recetas> buscaRecetasPorNombre(@PathParam("nombre") String nombre) {
        List<Recetas> recetas = null;
        String palabraClave = "%"+nombre+"%";
        EntityManager conexion = getEntityManager();
        
        try {
            recetas = conexion.createQuery("SELECT r FROM Recetas r WHERE r.nombreReceta LIKE :palabraClave").setParameter("nombreClave", palabraClave).getResultList();
        } catch (Exception e) {
            recetas = new ArrayList();
        }
        
        return recetas;
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
