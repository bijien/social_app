package com.example.laborator6map.controller;

import com.example.laborator6map.service.ServiceNetwork;
import com.example.laborator6map.validators.RepositoryException;
import com.example.laborator6map.validators.ValidationException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;

public class ControllerSignUp {
    @FXML
    public TextField textFieldFirstName;
    @FXML
    public TextField textFieldLastName;
    @FXML
    public TextField textFieldUserName;
    @FXML
    public PasswordField passwordFieldPassword;
    @FXML
    public PasswordField passwordFieldConfirmPassword;
    private Stage stage;

    private ServiceNetwork serviceNetwork;

    public ServiceNetwork getServiceNetwork() {
        return serviceNetwork;
    }

    public void setServiceNetwork(ServiceNetwork serviceNetwork) {
        this.serviceNetwork = serviceNetwork;
    }

    public void onClickSignUp(ActionEvent actionEvent) throws NoSuchAlgorithmException {
        if(!Objects.equals(passwordFieldPassword.getText(), "") && passwordFieldPassword.getText().equals(passwordFieldConfirmPassword.getText())) {
            try {
                String rawPassword =passwordFieldPassword.getText();
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hash = digest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
                String encoded = Base64.getEncoder().encodeToString(hash);
                serviceNetwork.addUtilizator(textFieldFirstName.getText(),textFieldLastName.getText(),textFieldUserName.getText(),encoded);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("");
                alert.setHeaderText("Contul a fost creat cu succes!");
                alert.setContentText("");
                alert.showAndWait().ifPresent(rs -> {
                    if (rs == ButtonType.OK) {
                        System.out.println("Pressed OK.");
                    }
                });
            } catch(RepositoryException repositoryException) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Utilizator existent");
                alert.setHeaderText("Exista deja un utilizator cu acest username");
                alert.setContentText("Incercati din nou");
                alert.showAndWait().ifPresent(rs -> {
                    if (rs == ButtonType.OK) {
                        System.out.println("Pressed OK.");
                    }
                });
            } catch(ValidationException validationException) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Date invalide");
                alert.setHeaderText(validationException.getMessage());
                alert.setContentText("Incercati din nou");
                alert.showAndWait().ifPresent(rs -> {
                    if (rs == ButtonType.OK) {
                        System.out.println("Pressed OK.");
                    }
                });
            }
        }
        else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Parola invalida");
            alert.setHeaderText("Parolele nu se potrivesc");
            alert.setContentText("Incercati din nou");
            alert.showAndWait().ifPresent(rs -> {
                if (rs == ButtonType.OK) {
                    System.out.println("Pressed OK.");
                }
            });
        }
    }

    public void onClickGoBack(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("com/example/laborator6map/login-view.fxml"));
        Parent root = (Parent) fxmlLoader.load();
        ControllerLogin controller = fxmlLoader.<ControllerLogin>getController();
        controller.setServiceNetwork(this.getServiceNetwork());
        stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
