module dynamic.com.dynamic {
    requires javafx.controls;
    requires javafx.fxml;


    opens dynamic.com.dynamic to javafx.fxml;
    exports dynamic.com.dynamic;
}