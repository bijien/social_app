package com.example.laborator6map;

import com.example.laborator6map.controller.ControllerFriendList;
import com.example.laborator6map.controller.ControllerLogin;
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
import com.example.laborator6map.validators.UserValidator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.EventObject;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Repository<Long, Utilizator> utilizatorRepoDB = new UtilizatorDbRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "postgres", new UserValidator());
        Repository<Long, Message> messageRepositoryDb = new MessageDbRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "postgres", utilizatorRepoDB);
        ServiceUser serviceUser = new ServiceUser(utilizatorRepoDB, new UserValidator());
        Repository<Tuple<Long, Long>, Prietenie> prietenieDbRepository = new PrietenieDbRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "postgres");
        ServiceMessage serviceMessage = new ServiceMessage(utilizatorRepoDB, prietenieDbRepository, messageRepositoryDb);
        ServicePrietenie servicePrietenie = new ServicePrietenie(utilizatorRepoDB, prietenieDbRepository);
        ServiceNetwork serviceNetwork = new ServiceNetwork(serviceUser, servicePrietenie, serviceMessage);

        /*
        Parent root = FXMLLoader.load(getClass().getResource("login-view.fxml"));
        Scene scene = new Scene(root);
        stage.setTitle("Log in");
        stage.setScene(scene);
        stage.show();
        */
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("com/example/laborator6map/login-view.fxml"));
        Parent root = (Parent) fxmlLoader.load();
        ControllerLogin controller = fxmlLoader.<ControllerLogin>getController();
        controller.setServiceNetwork(serviceNetwork);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();


    }

    public static void main(String[] args) {
        launch();
    }
}