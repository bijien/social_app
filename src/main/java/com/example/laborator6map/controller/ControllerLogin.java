package com.example.laborator6map.controller;

import com.example.laborator6map.domain.Message;
import com.example.laborator6map.domain.Prietenie;
import com.example.laborator6map.domain.Tuple;
import com.example.laborator6map.domain.Utilizator;
import com.example.laborator6map.repository.Repository;
import com.example.laborator6map.repository.db.MessageDbRepository;
import com.example.laborator6map.repository.db.PrietenieDbRepository;
import com.example.laborator6map.repository.db.UtilizatorDbRepository;
import com.example.laborator6map.service.ServiceMessage;
import com.example.laborator6map.service.ServiceNetwork;
import com.example.laborator6map.service.ServicePrietenie;
import com.example.laborator6map.service.ServiceUser;
import com.example.laborator6map.validators.RepositoryException;
import com.example.laborator6map.validators.UserValidator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class ControllerLogin<serviceNetwork> {

    private Stage stage;
    private Scene scene;
    private Parent root;
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

    public void initialize() {
        Repository<Long, Utilizator> utilizatorRepoDB = new UtilizatorDbRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "postgres", new UserValidator());
        Repository<Long, Message> messageRepositoryDb = new MessageDbRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "postgres", utilizatorRepoDB);
        ServiceUser serviceUser = new ServiceUser(utilizatorRepoDB, new UserValidator());
        Repository<Tuple<Long, Long>, Prietenie> prietenieDbRepository = new PrietenieDbRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "postgres");
        ServiceMessage serviceMessage = new ServiceMessage(utilizatorRepoDB, prietenieDbRepository, messageRepositoryDb);
        ServicePrietenie servicePrietenie = new ServicePrietenie(utilizatorRepoDB, prietenieDbRepository);
        this.serviceNetwork = new ServiceNetwork(serviceUser, servicePrietenie, serviceMessage);
    }


    @FXML
    protected void onClickLogIn(ActionEvent actionEvent) throws IOException {
        initialize();
        String username = textFieldLoginIn.getText();
        try {
            Utilizator utilizator = serviceNetwork.findUserByUsername(username);
            if(passwordFieldLogin.getText().equals(utilizator.getPassword())) {
                userIdLoggedIn = utilizator.getId();
            /*Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("com/example/laborator6map/friendlist-view.fxml"));
            stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
             */
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("com/example/laborator6map/friendlist-view.fxml"));
                Parent root = (Parent) fxmlLoader.load();
                ControllerFriendList controller = fxmlLoader.<ControllerFriendList>getController();
                controller.setUserId(userIdLoggedIn);
                stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            }
            else {
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

}

