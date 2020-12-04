/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.diego.libros;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 *
 * @author diego
 */
public class Formulario extends Application {
    public void start (Stage primaryStage) throws Exception{
        
        Parent contenedor = new FXMLLoader().load(getClass().getResource("FormularioLibros.fxml"));
        
        Scene escena = new Scene(contenedor, 1000 , 500);
        escena.getStylesheets().addAll(getClass().getResource("estilos.css").toExternalForm());
        primaryStage.setTitle("Gestor de colección de libros");
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new Image("/img/libro.png"));
        primaryStage.setScene(escena);
        primaryStage.show();
    }
    public static void main(String[]  args){
        launch(args);
    }
}
