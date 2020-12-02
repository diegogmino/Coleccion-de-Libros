/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.diego.libros;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author diego
 */
public class LibroDao {
    
     public static final String URL_CONEXION = "jdbc:h2:./libros";
    public static final String USUARIO_BDD = "root";
    public static final String PASSWORD_BDD = "";
    
     public LibroDao() {
        crearTablasSiNoExiste();
    }

    private void crearTablasSiNoExiste() {
       try (Connection conexionBD = DriverManager.getConnection(URL_CONEXION, USUARIO_BDD, PASSWORD_BDD)) {
           Statement statement = conexionBD.createStatement();
           String sentencia = "CREATE TABLE IF NOT EXISTS libro "
                   + "(id INTEGER auto_increment, "
                   + "isbn LONG, "
                   + "titulo VARCHAR(255), "
                   + "autor VARCHAR(255),"
                   + "paginas INTEGER,"
                   + "genero VARCHAR(255),"
                   + "sinopsis VARCHAR(500),"
                   + "leido BOOLEAN,"
                   + "fecha DATE,"
                   + "portada VARCHAR(1000) )";
           statement.executeUpdate(sentencia);                 
       } catch (Exception e) {
           e.printStackTrace();
       }
    }
    
    public void guardarOActualizar(Libro libro) {
        if (libro.getId()== 0) {
            guardar(libro);
        } else {
            actualizar(libro);
        }
    }
    
    public void guardar(Libro libro) {
        try ( Connection conexionDB = DriverManager.getConnection(URL_CONEXION, USUARIO_BDD, PASSWORD_BDD)) {
            Statement statement = conexionDB.createStatement();
            String sql = "INSERT INTO libro(isbn, titulo, autor, paginas, genero, sinopsis, leido, fecha, portada) "
                    + "VALUES ('" + libro.getIsbn() + "', '" + libro.getTitulo()+ "','" + libro.getAutor() + "','" + libro.getPaginas() + "','" + libro.getGenero() + "','"+ libro.getSinopsis() +"'," + libro.isLeido() +",'" +  libro.getFecha() + "','" + libro.getPortada() + "')";
            statement.executeUpdate(sql); 
        } catch (Exception e) {
            throw new RuntimeException("Ocurrió un error al guardar la información del libro: " + e.getMessage());
        }
    }
    
    public void actualizar (Libro libro) {
        try (Connection conexionDB = DriverManager.getConnection(URL_CONEXION, USUARIO_BDD, PASSWORD_BDD)) {
            Statement statement = conexionDB.createStatement();
            String sql = "UPDATE libro set isbn =" + libro.getIsbn() + ",titulo='" + libro.getTitulo()+ 
                    "', autor='" + libro.getAutor() + "', paginas='" + libro.getPaginas() + "',genero='"+ libro.getGenero() + "',sinopsis='" + libro.getSinopsis()+ "', leido=" + libro.isLeido() + ",fecha='" + libro.getFecha() + "',portada='" + libro.getPortada() + "' WHERE id=" + libro.getId();
            statement.executeUpdate(sql);
        } catch (Exception e) {
            throw new RuntimeException("Ocurrió un error al actualizar la información del libro: " + e.getMessage());
        }
    }
    
    public List<Libro> buscarTodos() {
        List<Libro> listaLibros = new ArrayList<>();
          try (Connection conexionDB = DriverManager.getConnection(URL_CONEXION, USUARIO_BDD, PASSWORD_BDD)) {
              Statement statement = conexionDB.createStatement();
              String sql = "SELECT * FROM libro ORDER BY id";
              ResultSet resultSet = statement.executeQuery(sql);
              while(resultSet.next()) {
                  Libro libro = new Libro();
                  libro.setTitulo(resultSet.getString("titulo"));
                  libro.setAutor(resultSet.getString("autor"));
                  libro.setPaginas(resultSet.getInt("paginas"));
                  libro.setIsbn(resultSet.getLong("isbn"));
                  libro.setSinopsis(resultSet.getString("sinopsis"));
                  libro.setGenero(resultSet.getString("genero"));
                  libro.setPortada(resultSet.getString("portada"));
                  libro.setId(resultSet.getInt("id"));
                  libro.setLeido(resultSet.getBoolean("leido"));
                  libro.setFecha(resultSet.getDate("fecha").toLocalDate());
                  listaLibros.add(libro);
              }
          } catch (Exception e) {
            throw new RuntimeException("Ocurrió un error al consultar la lista de libros: " + e.getMessage());
        }
          return listaLibros;
    }
    
    public void eliminar(Libro libro) {
        
         try (Connection conexionDB = DriverManager.getConnection(URL_CONEXION, USUARIO_BDD, PASSWORD_BDD)) {
              Statement statement = conexionDB.createStatement();
              String sql = "DELETE FROM libro WHERE id=" + libro.getId();
              statement.executeUpdate(sql);
          } catch (Exception e) {
            throw new RuntimeException("Ocurrió un error al eliminar el libro: " + e.getMessage());
        }  
        
    }

}
