module com.example.pruebabbdd {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.mariadb.jdbc;
    requires java.sql;


    opens com.example.pruebabbdd to javafx.fxml;
    exports com.example.pruebabbdd;
}