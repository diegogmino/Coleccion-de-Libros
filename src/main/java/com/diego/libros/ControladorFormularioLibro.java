/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.diego.libros;


import java.awt.event.MouseEvent;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;

/**
 *
 * @author diego
 */
public class ControladorFormularioLibro implements Initializable {
    
    @FXML
    TextField isbn ;
    
    @FXML
    TextField titulo ;
    
    @FXML
    TextField autor ;
    
    @FXML
    TextField paginas ;
    
    @FXML
    TextArea sinopsis;
    
    @FXML
    TextField portada;
    
    @FXML
    ImageView imagen;
    
    @FXML
    CheckBox leido;
    
    @FXML
    DatePicker fecha;
    
    @FXML
    Button guardar;
    
    @FXML
    Button editar;
    
    @FXML
    Button eliminar;
    
    @FXML
    Spinner spinner;
    
    ObservableList<String> generos = FXCollections.observableArrayList("Terror", "Realista", "Policíaco", "Infantil", "Fantasía", "Drama", "Distópico", "Ciencia ficción", "Aventuras");
    SpinnerValueFactory<String> factoria = new SpinnerValueFactory.ListSpinnerValueFactory<String>(generos);
    
    @FXML
    TableView<Libro> tablaLibros;
    
    LibroDao libroDao;
    
    int id = 0;
    LocalDate date = LocalDate.parse("1900-01-01");
    
    
   public void guardar(){
       
         Libro libro = new Libro();
         libro.setId(id);
         libro.setIsbn(Long.parseLong(isbn.getText()));
         libro.setTitulo(titulo.getText());
         libro.setAutor(autor.getText());
         libro.setPaginas(Integer.parseInt(paginas.getText()));
         libro.setSinopsis(sinopsis.getText());
         libro.setGenero(spinner.getValue().toString());
         libro.setPortada(portada.getText());
         libro.setLeido(leido.isSelected());
         if(fecha.getValue() == null) { 
             libro.setFecha(date);    
         } else {
             libro.setFecha(fecha.getValue());
         }
  
         libroDao.guardarOActualizar(libro);
         id=0;
       
         cargarLibrosDeLaBase();
       
        isbn.clear();
        titulo.clear();
        autor.clear();
        paginas.clear();
        sinopsis.clear();
        portada.clear();
        leido.setSelected(false);
        fecha.setValue(null);
        factoria.setValue("Aventuras");
        imagen.setImage(null);
        
        fecha.setDisable(true);
        
        editar.setDisable(true);
        eliminar.setDisable(true);

   } 
   
   public void editar() {
       
       isbn.setDisable(false);
       titulo.setDisable(false);
       autor.setDisable(false);
       paginas.setDisable(false);
       this.spinner.setDisable(false);
       sinopsis.setDisable(false);
       leido.setDisable(false);
       
       if (leido.isSelected()) {
           fecha.setDisable(false);
       } else {
           fecha.setDisable(true);
       }
       
       portada.setDisable(false);
       
       guardar.setDisable(false);
       
        editar.setDisable(true);
       
   }
   
   public void cancelar() {
       
       isbn.setDisable(false);
       titulo.setDisable(false);
       autor.setDisable(false);
       paginas.setDisable(false);
       this.spinner.setDisable(false);
       sinopsis.setDisable(false);
       leido.setDisable(false);
       fecha.setDisable(true);
       portada.setDisable(false);
       
       isbn.clear();
       titulo.clear();
       autor.clear();
       paginas.clear();
       sinopsis.clear();
       portada.clear();
       leido.setSelected(false);
       fecha.setValue(null);
       factoria.setValue("Aventuras");
       imagen.setImage(null);
       
       id = 0;
       
       guardar.setDisable(false);
       
       editar.setDisable(true);
       eliminar.setDisable(true);
   }
   
   public void visualizar() {
       
       Libro libro = tablaLibros.getSelectionModel().getSelectedItem();
       isbn.setText(libro.getIsbn()+"");
       isbn.setDisable(true);
       titulo.setText(libro.getTitulo());
       titulo.setDisable(true);
       autor.setText(libro.getAutor());
       autor.setDisable(true);
       paginas.setText( libro.getPaginas()+"");
       paginas.setDisable(true);
       factoria.setValue(libro.getGenero());
       this.spinner.setValueFactory(factoria);
       this.spinner.setDisable(true);
       sinopsis.setText(libro.getSinopsis());
       sinopsis.setDisable(true);
       portada.setText(libro.getPortada());
       portada.setDisable(true);
       leido.setSelected(libro.isLeido());
       leido.setDisable(true);
       
       if(libro.getFecha().equals(date)) {
           fecha.setValue(null);
       } else {
           fecha.setValue(libro.getFecha());
       }
       
       fecha.setDisable(true);
       
       id =libro.getId();
       
       Image img = new  Image(portada.getText());
       imagen.setImage(img);
       
       guardar.setDisable(true);
       editar.setDisable(false);
       eliminar.setDisable(false);
       
   }
   
   public void activarDesactivarFecha() {
       if (leido.isSelected()) {
            fecha.setDisable(false);
       } else {
           fecha.setDisable(true);
       }
   }
   
   
   public void eliminar() {
      
       Libro libro = tablaLibros.getSelectionModel().getSelectedItem();
       libroDao.eliminar(libro);
       cargarLibrosDeLaBase();
       
       isbn.clear();
       titulo.clear();
       autor.clear();
       paginas.clear();
       sinopsis.clear();
       portada.clear();
       leido.setSelected(false);
       fecha.setValue(null);
       factoria.setValue("Aventuras");
       imagen.setImage(null);
       
       guardar.setDisable(false);
       editar.setDisable(true);
       eliminar.setDisable(true);
       
       isbn.setDisable(false);
       titulo.setDisable(false);
       autor.setDisable(false);
       paginas.setDisable(false);
       this.spinner.setDisable(false);
       sinopsis.setDisable(false);
       leido.setDisable(false);
       fecha.setDisable(true);
       portada.setDisable(false);
       
   }
   
   @Override
   public void initialize(URL location, ResourceBundle resources) {
       
       libroDao = new LibroDao();
       cargarLibrosDeLaBase();
       configurarTamanioColumnas();
       factoria.setValue("Aventuras");
       this.spinner.setValueFactory(factoria);
       editar.setDisable(true);
       eliminar.setDisable(true);
       fecha.setDisable(true);
     
       // Bloquear valores no numericos en el campo del isbn
       isbn.textProperty().addListener(new ChangeListener<String>() {
       @Override
       public void changed(ObservableValue<? extends String> observable, String oldValue, 
       String newValue) {
       if (!newValue.matches("\\d*")) {
             isbn.setText(newValue.replaceAll("[^\\d]", ""));
        }
        }
        });
       
       // Bloquear valores no numéricos en el campo de las páginas
       paginas.textProperty().addListener(new ChangeListener<String>() {
       @Override
       public void changed(ObservableValue<? extends String> observable, String oldValue, 
       String newValue) {
       if (!newValue.matches("\\d*")) {
             paginas.setText(newValue.replaceAll("[^\\d]", ""));
        }
        }
        });
       
   }
   
   private void cargarLibrosDeLaBase() {
       
       ObservableList<Libro> libros = FXCollections.observableArrayList();
       List<Libro> librosEncontrados = libroDao.buscarTodos();
       libros.addAll(librosEncontrados);
       tablaLibros.setItems(libros);   
   }
   
   private void configurarTamanioColumnas() {
       tablaLibros.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
       ObservableList<TableColumn<Libro, ?>> columnas = tablaLibros.getColumns();
       columnas.get(0).setMaxWidth(1f * Integer.MAX_VALUE * 8);
       columnas.get(1).setMaxWidth(1f * Integer.MAX_VALUE * 20);
       columnas.get(2).setMaxWidth(1f * Integer.MAX_VALUE * 20);
       columnas.get(3).setMaxWidth(1f * Integer.MAX_VALUE * 5);
       columnas.get(4).setMaxWidth(1f * Integer.MAX_VALUE * 10);
       columnas.get(5).setMaxWidth(1f * Integer.MAX_VALUE * 37);
       
   }
   
}
