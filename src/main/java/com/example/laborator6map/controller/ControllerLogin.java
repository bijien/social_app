package com.example.laborator6map.controller;


import com.example.laborator6map.domain.Utilizator;
import com.example.laborator6map.service.ServiceNetwork;
import com.example.laborator6map.validators.RepositoryException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class ControllerLogin {

    private Stage stage;

    private Long userIdLoggedIn;

    @FXML
    public Label labelLogin;
    @FXML
    public TextField textFieldLoginIn;
    @FXML
    public Button buttonLoginIn;
    @FXML
    public PasswordField passwordFieldLogin;

    private ServiceNetwork serviceNetwork;

    public ServiceNetwork getServiceNetwork() {
        return serviceNetwork;
    }

    public void setServiceNetwork(ServiceNetwork serviceNetwork) {
        this.serviceNetwork = serviceNetwork;
    }


    @FXML
    protected void onClickLogIn(ActionEvent actionEvent) throws IOException, NoSuchAlgorithmException {
        String username = textFieldLoginIn.getText();
        try {
            Utilizator utilizator = serviceNetwork.findUserByUsername(username);
            String rawPassword = passwordFieldLogin.getText();
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            String encoded = Base64.getEncoder().encodeToString(hash);
            if (encoded.equals(utilizator.getPassword())) {
                userIdLoggedIn = utilizator.getId();
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("com/example/laborator6map/friendlist-view.fxml"));
                Parent root = (Parent) fxmlLoader.load();
                ControllerFriendList controller = fxmlLoader.<ControllerFriendList>getController();
                controller.setServiceNetwork(this.getServiceNetwork());
                controller.setUserId(userIdLoggedIn);
                controller.setSameSession(false);
                stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Parola incorecta");
                alert.setHeaderText("Parola introdusa nu este corecta");
                alert.setContentText("Incercati din nou");
                alert.showAndWait().ifPresent(rs -> {
                    if (rs == ButtonType.OK) {
                        System.out.println("Pressed OK.");
                    }
                });
            }
        } catch (RepositoryException exception) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Utilizator neexistent");
            alert.setHeaderText("Nu exista un utilizator cu acest nume");
            alert.setContentText("Incercati din nou");
            alert.showAndWait().ifPresent(rs -> {
                if (rs == ButtonType.OK) {
                    System.out.println("Pressed OK.");
                }
            });
        }
    }

    public void onClickGoToSignUp(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("com/example/laborator6map/signup-view.fxml"));
        Parent root = (Parent) fxmlLoader.load();
        ControllerSignUp controller = fxmlLoader.<ControllerSignUp>getController();
        controller.setServiceNetwork(this.getServiceNetwork());
        stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}

