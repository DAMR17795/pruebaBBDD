package com.example.pruebabbdd;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

public class HelloController {
    private ObservableList <Productos> datos;
    private ObservableList<ObservableList> data;
    @FXML
    private TableView tvDatos;
    @FXML
    private TextField txtAreaConsulta;
    @FXML
    private Button btConsulta;
    @FXML
    private TableColumn tcProductCode;
    @FXML
    private TableColumn tcProductName;
    @FXML
    private TableColumn tcProductLine;
    @FXML
    private TableColumn tcProductScale;
    @FXML
    private TableColumn tcProductVendor;
    @FXML
    private TableColumn tcQuantityInStock;
    @FXML
    private TableColumn tcBuyPrice;
    @FXML
    private TableColumn tcMSRP;
    @FXML
    private TableColumn tcProductDescription;

    @Deprecated
    public void onEjecutarConsulta(ActionEvent actionEvent) {
     //obtenerProductos();
     cargarDatosTabla();
    }

    public void cargarDatos2() {
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
            //String SQL = txtAreaConsulta.getText();
            String SQL = "SELECT * FROM Products";
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
    private Connection conexionBBDD;
    private final String servidor = "jdbc:mariadb://localhost:5555/noinch?useSSL=false";
    private final String usuario = "adminer";
    private final String passwd = "adminer";

    //PRIMERO OBTENEMOS LOS PRODUCTOS Y LOS GUARDAMOS EN UN ARRAYLIST DE PRODUCTOS
    //ADEMÁS LOS MOSTRAMOS POR CONSOLA PARA VER QUE SE HA AÑADIDO
        public ObservableList<Productos> obtenerProductos() {

            ObservableList<Productos> datosResultadoConsulta = FXCollections.observableArrayList();
            try {
                // Nos conectamos
                conexionBBDD = DriverManager.getConnection(servidor, usuario, passwd);
                String SQL = "SELECT * "
                        + "FROM products "
                        + "ORDER By productName";

                // Ejecutamos la consulta y nos devuelve una matriz de filas (registros) y columnas (datos)
                ResultSet resultadoConsulta = conexionBBDD.createStatement().executeQuery(SQL);

                // Debemos cargar los datos en el ObservableList (Que es un ArrayList especial)
                // Los datos que devuelve la consulta no son directamente cargables en nuestro objeto
                while (resultadoConsulta.next()) {
                    datosResultadoConsulta.add(new Productos(
                            resultadoConsulta.getString("productCode"),
                            resultadoConsulta.getString("productName"),
                            resultadoConsulta.getString("productLine"),
                            resultadoConsulta.getString("productScale"),
                            resultadoConsulta.getString("productVendor"),
                            resultadoConsulta.getString("productDescription"),
                            resultadoConsulta.getInt("quantityInStock"),
                            resultadoConsulta.getDouble("buyPrice"),
                            resultadoConsulta.getDouble("MSRP"))
                    );
                    System.out.println("Row [1] added " + resultadoConsulta.toString());
                }
                conexionBBDD.close();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error:" + e.toString());
                conexionBBDD.close();
            } finally {
                return datosResultadoConsulta;
            }
        }

    @FXML
    public void pulsarIntro(Event event) {
        txtAreaConsulta.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                    cargarDatos2();
                }
            }
        });
    }
    //LUEGO UTILIZAMOS EL METODO PARA CARGAR LOS DATOS EN LA TABLA
    private void cargarDatosTabla () {
        datos = obtenerProductos();

        tcProductCode.setCellValueFactory(new PropertyValueFactory<Productos, String>("productCode"));
        tcProductDescription.setCellValueFactory(new PropertyValueFactory<Productos, String>("productDescription"));
        tcProductLine.setCellValueFactory(new PropertyValueFactory<Productos, String>("productLine"));
        tcProductName.setCellValueFactory(new PropertyValueFactory<Productos, String>("productName"));
        tcProductScale.setCellValueFactory(new PropertyValueFactory<Productos, String>("productScale"));
        tcProductVendor.setCellValueFactory(new PropertyValueFactory<Productos, String>("productVendor"));
        tcBuyPrice.setCellValueFactory(new PropertyValueFactory<Productos, Double>("buyPrice"));
        tcMSRP.setCellValueFactory(new PropertyValueFactory<Productos, Double>("MSRP"));
        tcQuantityInStock.setCellValueFactory(new PropertyValueFactory<Productos, Integer>("quantityInStock"));

        tvDatos.setItems(datos);
    }
    //METODO PARA INICIALIZAR
    public void initialize() {
            cargarDatosTabla();
    }
}