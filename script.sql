create database foreignCook;
use foreignCook;
create table usuarios(
correo varchar(100) primary key,
nombreUsuario varchar(50),
contrasena varchar(64),
nombreImagen varchar(50),
descripcion varchar(5000)
);

create table recetas(
idReceta int not null auto_increment primary key,
nombreReceta varchar(100) not null,
ingredientes varchar(5000) not null,
procedimiento varchar(5000) not null,
categoria varchar(50),
likes int,
nombreImagen varchar(10),
linkVideo varchar(200),
correo varchar(100),
foreign key (correo) references usuarios(correo)
);

create table comentarios(
	idReceta int,
	idComentario int not null auto_increment primary key,
	contenido varchar(5000),
	nombreUsuario varchar(50),
	foreign key (idReceta) references recetas(idReceta)
);

create user 'usuarioCook'@'%' identified by 'acdc@-8221053';
Grant insert on foreignCook.* to 'usuarioCook'@'%';
Grant select on foreignCook.* to 'usuarioCook'@'%';
Grant delete on foreignCook.* to 'usuarioCook'@'%';
Grant update on foreignCook.* to 'usuarioCook'@'%';