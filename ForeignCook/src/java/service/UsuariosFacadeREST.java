package service;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import persistencia.Usuarios;
import sun.misc.BASE64Decoder;

/**
 *
 * @author Miguel Leonardo Jimenez Jimenez
 * @date 13/05/2018
 * @time 08:08:59 PM
 */
@Stateless
@MultipartConfig(location="/tmp", fileSizeThreshold=1024*1024,maxFileSize=1024*1024*5, maxRequestSize=1024*1024*5*5)
@Path("persistencia.usuarios")
public class UsuariosFacadeREST extends AbstractFacade<Usuarios> {

    @PersistenceContext(unitName = "ForeignCookPU")
    private EntityManager em;

    public UsuariosFacadeREST() {
        super(Usuarios.class);
    }

    @POST
    @Override
    @Consumes({MediaType.APPLICATION_JSON})
    public void create(Usuarios entity) {
        entity.setNombreImagen(entity.getCorreo()+".jpg");
        super.create(entity);
    }

    @PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_JSON})
    public void edit(@PathParam("id") String id, Usuarios entity) {
        super.edit(entity);
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") String id) {
        super.remove(super.find(id));
    }

    @POST
    @Path("foto")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response uploadFile(Usuarios usuario){
        String output = "";
        try{
                //String ruta = new File(".").getCanonicalPath() + "/fotos/" + usuario.getCorreo() + ".jpg";
                
                byte arr[] = new BASE64Decoder().decodeBuffer(usuario.getNombreImagen());
                int tamaño = arr.length;
                FileOutputStream arch = new FileOutputStream("C:\\Users\\Leonardo\\Documents\\GitHub\\ServiciosForeignCook\\ForeignCook\\web\\fotos\\imagen5.jpg");
                arch.write(arr, 0, tamaño);
                arch.close();
                output = "{\"respuesta\": \"OK\"}";
            
        }catch(Exception exception){
            output = "{\"respuesta\": \"" + exception.toString() + "\"}";
        }
        return Response.status(200).entity(output).build();
    }
    
    @POST
    @Path("/fotos/{id}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(@Context HttpServletRequest request, @PathParam("id") String id){
        String output = "";
        try{
            if (request.getParts().size() > 0){
                Part parte = (Part) request.getParts();
                //String path = new File(".").getCanonicalPath() + "/fotos/" + id + ".jpg";
                String path = "C:\\Users\\Leonardo\\Documents\\GitHub\\ServiciosForeignCook\\ForeignCook\\web\\fotos\\"+id+".jpg";
                InputStream is = parte.getInputStream();
                int tam = (int) parte.getSize();
                byte arr[] = new byte[tam];
                is.read(arr, 0, tam);
                FileOutputStream arch = new FileOutputStream(path);
                arch.write(arr, 0, tam);
                arch.close();
                output = "{\"respuesta\": \"OK\"}";
            }
        }catch(Exception exception){
            System.out.println(exception.getMessage());
            output = "{\"respuesta\": \"" + exception.toString() + "\"}";
        }
        return Response.status(200).entity(output).build();
    }
    
    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Usuarios find(@PathParam("id") String id) {
        return super.find(id);
    }
    
    @POST
    @Path("iniciarSesion")
    @Produces({MediaType.APPLICATION_JSON})
    public Response iniciarSesion(Usuarios sesion) {
        boolean validacion = true;
        String salida = "";
        EntityManager conexion = getEntityManager();
        Usuarios usuario = null;
        try {
            usuario = conexion.find(Usuarios.class, sesion.getCorreo());
        } catch (Exception e) {
            
        }
        
        if(usuario != null){
            validacion = usuario.getContrasena().equals(sesion.getContrasena());
        }else{
            validacion = false;
        }
        
        salida = validacion?"{\"respuesta\": \"OK\"}":"{\"respuesta\": \"FAIL\"}";
        return Response.status(200).entity(salida).build();
    }
    

    @GET
    @Override
    @Produces({MediaType.APPLICATION_JSON})
    public List<Usuarios> findAll() {
        return super.findAll();
    }

    @GET
    @Path("{from}/{to}")
    @Produces({MediaType.APPLICATION_JSON})
    public List<Usuarios> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
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
