/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.diego.libros;


import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javax.imageio.ImageIO;

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
    // Géneros ordenados alfabéticamente de forma inversa para que aparezcan en orden en el spinner
    ObservableList<String> generos = FXCollections.observableArrayList("Terror", "Realista", "Policíaco", "Infantil", "Filosofía", "Fantasía", "Drama", "Distópico", "Ciencia ficción", "Aventuras");
    SpinnerValueFactory<String> factoria = new SpinnerValueFactory.ListSpinnerValueFactory<>(generos);
    
    @FXML
    TableView<Libro> tablaLibros;
    
    @FXML
     TextField totalLibros;
    
    @FXML
     TextField librosLeidos;
    
    LibroDao libroDao;
    
    int id = 0;
    // Fecha predeterminada que se le pone a un libro si no se le ha especificado una concreta
    LocalDate date = LocalDate.parse("1900-01-01");
    // Variables que se usarán para comprobar si los campos están cubiertos, inicializadas a false
    boolean isbnEs = false, tituloEs = false, autorEs = false, paginasEs = false, sinopsisEs = false, portadaEs = false;
    
    
   public void guardar(){
         // Método que guarda la información introducida en los campos  
         if(isbn.getText().length() > LibroDao.ISBNMAX) {
                 Alert errorISBN = lanzarPopup("Error", "ISBN-13 demasiado largo", 2);
                 errorISBN.showAndWait();
             } else if (portada.getText().length() > LibroDao.PORTADAMAX) {             
                 Alert errorPortada = lanzarPopup("Error", "La URL introducida es demasiado larga", 2);
                 errorPortada.showAndWait();         
             } else if (paginas.getText().length() > LibroDao.PAGINASMAX) {
                 Alert errorPaginas = lanzarPopup("Error", "Campo páginas demasiado largo", 2);
                 errorPaginas.showAndWait();  
             } else {
                 // Lanzar popup de confirmación
                 Alert popupGuardar = lanzarPopup("Guardar", "La información se va a guardar en la base de datos. ¿Estás seguro?", 1);
         
                 Optional<ButtonType> result = popupGuardar.showAndWait();
         
                 if (result.get() == ButtonType.OK) {
             
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
                     cargarNumeroLibrosColeccion();
       
                     limpiarCampos();
        
                     fecha.setDisable(true);  
                     editar.setDisable(true);
                     eliminar.setDisable(true);
                     guardar.setDisable(true);
        
                     // Poner todos los campos como vacios de nuevo
                     camposBooleanosVacios();
                } 
             }    
    } 
   
   public void editar() {
       // Método que habilita la posibilidad de editar los elementos cargados en visualizar()  
       habilitarCampos();
       
       if (leido.isSelected()) {
           fecha.setDisable(false);
       } else {
           fecha.setDisable(true);
       }
       
       portada.setDisable(false);
       guardar.setDisable(false);   
       editar.setDisable(true);
       
       // Poner todos los campos como rellenados
       camposBooleanosLlenos();
           
   }
   
   public void cancelar() {
       // Método que limpia todos los campos sin realizar cambios en la base de datos
       
         Alert popupCancelar = lanzarPopup("Cancelar", "Los cambios no se guardará. ¿Estás seguro?", 1);
         
         Optional<ButtonType> result = popupCancelar.showAndWait();
         
         if (result.get() == ButtonType.OK) {
           habilitarCampos();     
           limpiarCampos();
       
            id = 0;
       
            // Poner todos los campos como vacios de nuevo
            camposBooleanosVacios();
       
            guardar.setDisable(true); 
            editar.setDisable(true);
            eliminar.setDisable(true);
       } 
           
   }
   
   public void visualizar() {
       // Método que se activa al pulsar un elemento del TableView y lo carga
       Libro libro = tablaLibros.getSelectionModel().getSelectedItem();
       isbn.setText(Long.toString(libro.getIsbn()));
       isbn.setDisable(true);
       titulo.setText(libro.getTitulo());
       titulo.setDisable(true);
       autor.setText(libro.getAutor());
       autor.setDisable(true);
       paginas.setText(Integer.toString(libro.getPaginas()));
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

       Image img =  new Image("/img/portada_no_disponible.png");
       
       if (testImagen(portada.getText())) {
            img = new  Image(portada.getText());
       }
       
       imagen.setImage(img);
  
       guardar.setDisable(true);
       editar.setDisable(false);
       eliminar.setDisable(false);
       
   }
   
   public void eliminar() {
       // Método para elminar un libro de la base de datos
       Libro libro = tablaLibros.getSelectionModel().getSelectedItem();
       // Crear el popup
       Alert popupEliminar = lanzarPopup("Eliminar", "La información del libro se eliminará definitivamente. ¿Estás seguro?", 1);
       Optional<ButtonType> result = popupEliminar.showAndWait();
       if (result.get() == ButtonType.OK) {
            libroDao.eliminar(libro);
            cargarLibrosDeLaBase();
       
             id = 0;
       
             limpiarCampos();
             cargarNumeroLibrosColeccion();
             
             guardar.setDisable(true);
             editar.setDisable(true);
             eliminar.setDisable(true);
       
            habilitarCampos();
       }
  
   }
   
   public void camposBooleanosLlenos() {
       // Método que pone las variables booleanas de los campos como llenas
       isbnEs = true;
       tituloEs = true;
       autorEs = true;
       paginasEs = true;
       sinopsisEs = true;
       portadaEs = true;   
   }
   
   public void camposBooleanosVacios() {
       // Método que pone las variables booleanas de los campos como vacías
       isbnEs = false;
       tituloEs = false;
       autorEs = false;
       paginasEs = false;
       sinopsisEs = false;
       portadaEs = false;   
   }
   
   public void limpiarCampos() {
       // Método para limpiar los TextField
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
   }
   
   public void habilitarCampos() {
       // Método que deshabilita los TextField
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
   
   public Alert lanzarPopup(String titulo, String contenido, int tipo) {
       // Método para crear un popup con los string recibidos y devolverlo 
       Alert popup = null;
       if (tipo == 1) {
           popup = new Alert(Alert.AlertType.CONFIRMATION);
       } else if(tipo == 2) {
           popup = new Alert(Alert.AlertType.ERROR);
       }
       
       popup.setTitle(titulo);
       popup.setHeaderText(null);
       popup.setContentText(contenido);
       popup.initStyle(StageStyle.DECORATED); 
       Stage stage = (Stage) popup.getDialogPane().getScene().getWindow();
       stage.getIcons().add(new Image("/img/libro.png"));
       
       DialogPane dialogPane = popup.getDialogPane();
        dialogPane.getStylesheets().add(
        getClass().getResource("popup.css").toExternalForm());
        dialogPane.getStyleClass().add("popup");
       
        return popup;
            
   }
   
  public Boolean testImagen(String url) {  
      // Método que comprueba si la url de una imagen es válida
    try {  
        BufferedImage image = ImageIO.read(new URL(url));  
        return image != null;
    } catch (MalformedURLException e) {  
        return false;
    } catch (IOException e) {  
        return false;
    }
}  
   
   public void activarDesactivarFecha() {
       // Metodo que activa y desactiva el campo de la fecha
       if (leido.isSelected()) {
            fecha.setDisable(false);
       } else {
           fecha.setDisable(true);
       }
   }
   
   public void escribirISBN() {
        // Método que escucha cada vez que se pulsa una tecla en el campo de isbn y comprueba si está vacio o no
        isbnEs = !isbn.getText().isEmpty();
        activarGuardar();
   }
   
   public void escribirTitulo() {
        // Método que escucha cada vez que se pulsa una tecla en el capo de titulo y comprueba si está vacío o no
        tituloEs = !titulo.getText().isEmpty();
        activarGuardar();
   }
   
   public void escribirAutor() {
        // Método que escucha cada vez que se pulsa una tecla en el capo de autor y comprueba si está vacío o no
        autorEs = !autor.getText().isEmpty();
        activarGuardar();
   }
   
   public void escribirPaginas() {
        // Método que escucha cada vez que se pulsa una tecla en el capo de páginas y comprueba si está vacío o no
        paginasEs = !paginas.getText().isEmpty();
        activarGuardar();
   }
   
   public void escribirSinopsis() {
        // Método que escucha cada vez que se pulsa una tecla en el capo de sinópsis y comprueba si está vacío o no
        sinopsisEs = !sinopsis.getText().isEmpty();
        activarGuardar();
   }
   
   public void escribirPortada() {
        // Método que escucha cada vez que se pulsa una tecla en el capo de portada y comprueba si está vacío o no
        portadaEs = !portada.getText().isEmpty();
        activarGuardar();
   }
   
   public void activarGuardar() {
       // Método que comprueba si todos los campos están escritos para así activar o desactivar el botón de guardar la información
       if ((isbnEs == true) && (tituloEs == true) && (autorEs == true) && (paginasEs == true) && (sinopsisEs == true) && (portadaEs == true)) {
           guardar.setDisable(false);
       } else {
           guardar.setDisable(true);
       }
   }   
   
   @Override
   public void initialize(URL location, ResourceBundle resources) {
       // Método que inicializa el programa
       libroDao = new LibroDao();
       cargarLibrosDeLaBase();
       configurarTamanioColumnas();
       factoria.setValue("Aventuras");
       this.spinner.setValueFactory(factoria);
       editar.setDisable(true);
       eliminar.setDisable(true);
       fecha.setDisable(true);
       guardar.setDisable(true);
       sinopsis.setWrapText(true);
       
       cargarNumeroLibrosColeccion();
     
       // Bloquear valores no numericos en el campo del ISBN-13
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
   
   private void cargarNumeroLibrosColeccion() {
       // Método que carga en los TextField el número de libros total de la colección y el número de libros leidos y los pone no editables
       totalLibros.setEditable(false);
       totalLibros.setText(Integer.toString(libroDao.totalLibros()));
       librosLeidos.setEditable(false);
       librosLeidos.setText(Integer.toString(libroDao.librosLeidos()));
   }
   
   private void cargarLibrosDeLaBase() {
       // Método que rellena la tabla con los libros de la base de datos
       ObservableList<Libro> libros = FXCollections.observableArrayList();
       List<Libro> librosEncontrados = libroDao.buscarTodos();
       libros.addAll(librosEncontrados);
       tablaLibros.setItems(libros);   
   }
   
   private void configurarTamanioColumnas() {
       tablaLibros.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
       ObservableList<TableColumn<Libro, ?>> columnas = tablaLibros.getColumns();
       columnas.get(0).setMaxWidth(1f * Integer.MAX_VALUE * 10);
       columnas.get(1).setMaxWidth(1f * Integer.MAX_VALUE * 20);
       columnas.get(2).setMaxWidth(1f * Integer.MAX_VALUE *15);
       columnas.get(3).setMaxWidth(1f * Integer.MAX_VALUE * 5);
       columnas.get(4).setMaxWidth(1f * Integer.MAX_VALUE * 8);
       columnas.get(5).setMaxWidth(1f * Integer.MAX_VALUE * 43);
       
   }
   
}
