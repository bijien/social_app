package com.example.laborator6map;

import com.example.laborator6map.controller.ControllerLogin;
import com.example.laborator6map.domain.*;
import com.example.laborator6map.repository.Repository;
import com.example.laborator6map.repository.db.EventDbRepository;
import com.example.laborator6map.repository.db.MessageDbRepository;
import com.example.laborator6map.repository.db.PrietenieDbRepository;
import com.example.laborator6map.repository.db.UtilizatorDbRepository;
import com.example.laborator6map.service.*;
import com.example.laborator6map.validators.UserValidator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Repository<Long, Utilizator> utilizatorRepoDB = new UtilizatorDbRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "postgres");
        MessageDbRepository messageRepositoryDb = new MessageDbRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "postgres", utilizatorRepoDB);
        ServiceUser serviceUser = new ServiceUser(utilizatorRepoDB, new UserValidator());
        EventDbRepository evenimentDbRepository = new EventDbRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "postgres", utilizatorRepoDB);
        Repository<Tuple<Long, Long>, Prietenie> prietenieDbRepository = new PrietenieDbRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "postgres");
        ServiceEveniment serviceEveniment = new ServiceEveniment(utilizatorRepoDB, evenimentDbRepository);
        ServiceMessage serviceMessage = new ServiceMessage(utilizatorRepoDB, messageRepositoryDb);
        ServicePrietenie servicePrietenie = new ServicePrietenie(utilizatorRepoDB, prietenieDbRepository);
        ServiceNetwork serviceNetwork = new ServiceNetwork(serviceUser, servicePrietenie, serviceMessage, serviceEveniment);


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