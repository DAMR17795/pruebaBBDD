package com.example.pruebabbdd;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

public class HelloController {
    private ObservableList<ObservableList> data;
    @FXML
    private TableView tvDatos;
    @FXML
    private TextField txtAreaConsulta;
    @FXML
    private Button btConsulta;

    @Deprecated
    public void onEjecutarConsulta(ActionEvent actionEvent) {
       /* Dynamic TableView Data From Database
          BY Narayan G. Maharjan https://blog.ngopal.com.np/2011/10/19/dyanmic-tableview-data-from-database/comment-page-1/
          Modificado por FDGA
        */
        Connection c;
        data = FXCollections.observableArrayList();
        try {
            c = DriverManager.getConnection("jdbc:mariadb://localhost:5555/noinch?useSSL=false"
                    ,"adminer",
                    "adminer");;


            // Borramos por si es una nueva consulta
            if (! tvDatos.getColumns().isEmpty()) {
                tvDatos.setItems(null);
                tvDatos.getColumns().clear();
                data.removeAll();

            }


            //SQL FOR SELECTING ALL OF CUSTOMER
            String SQL = txtAreaConsulta.getText();
            //ResultSet
            ResultSet rs = c.createStatement().executeQuery(SQL);

            /**
             * ********************************
             * TABLE COLUMN ADDED DYNAMICALLY *
             *********************************
             */
            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                //We are using non property style for making dynamic table
                final int j = i;
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1));
                col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                        return new SimpleStringProperty(param.getValue().get(j) != null ? param.getValue().get(j).toString(): "");
                    }
                });

                tvDatos.getColumns().addAll(col);
                System.out.println("Column [" + i + "] ");
            }

            /**
             * ******************************
             * Data added to ObservableList *
             *******************************
             */
            while (rs.next()) {
                //Iterate Row
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    //Iterate Column
                    row.add(rs.getString(i));
                }
                System.out.println("Row [1] added " + row);
                data.add(row);

            }

            //FINALLY ADDED TO TableView
            tvDatos.setItems(data);
        } catch (Exception e) {
            e.printStackTrace();
            data.removeAll();
            tvDatos.getColumns().clear();
            tvDatos.setItems(null);
            System.out.println("Error on Building Data");
        }
    }

}