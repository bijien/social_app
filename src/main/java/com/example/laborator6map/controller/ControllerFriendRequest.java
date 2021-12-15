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
import com.example.laborator6map.validators.UserValidator;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;

public class ControllerFriendRequest {
    private final ObservableList<Prietenie> dataList = FXCollections.observableArrayList();
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    public TableView<Prietenie> TableViewFriendRequest;
    @FXML
    public TableColumn<Prietenie, String> columnFirstNameFriendRequest;
    @FXML
    public TableColumn<Prietenie, String> columnLastNameFriendRequest;
    @FXML
    public TableColumn<Prietenie, String> columnDataFriendRequest;
    @FXML
    public TableColumn<Prietenie, String> columnStatusFriendRequest;
    private ServiceNetwork serviceNetwork;
    private Long userIdLoggedIn;

    @FXML
    private void initialize() {
        Platform.runLater(() -> {
            Repository<Long, Utilizator> utilizatorRepoDB = new UtilizatorDbRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "postgres", new UserValidator());
            Repository<Long, Message> messageRepositoryDb = new MessageDbRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "postgres", utilizatorRepoDB);
            ServiceUser serviceUser = new ServiceUser(utilizatorRepoDB, new UserValidator());
            Repository<Tuple<Long, Long>, Prietenie> prietenieDbRepository = new PrietenieDbRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "postgres");
            ServiceMessage serviceMessage = new ServiceMessage(utilizatorRepoDB, prietenieDbRepository, messageRepositoryDb);
            ServicePrietenie servicePrietenie = new ServicePrietenie(utilizatorRepoDB, prietenieDbRepository);
            this.serviceNetwork = new ServiceNetwork(serviceUser, servicePrietenie, serviceMessage);
            initializeFriendRequestList();
        });
    }

    private void initializeFriendRequestList() {
        columnFirstNameFriendRequest.setCellValueFactory(cellData-> new SimpleStringProperty(serviceNetwork.findUser(cellData.getValue().getId().getLeft()).getFirstName()));
        columnLastNameFriendRequest.setCellValueFactory(cellData-> new SimpleStringProperty(serviceNetwork.findUser(cellData.getValue().getId().getLeft()).getLastName()));
        columnDataFriendRequest.setCellValueFactory(new PropertyValueFactory<>("localDate"));
        columnStatusFriendRequest.setCellValueFactory(new PropertyValueFactory<>("status"));

        for (Prietenie prietenie : serviceNetwork.friendRequestForAUser(userIdLoggedIn)) {
            dataList.add(prietenie);
        }
        TableViewFriendRequest.setItems(dataList);

    }

    public void setUserId(Long userIdLoggedIn) {
        this.userIdLoggedIn = userIdLoggedIn;
    }


    public void onClickGoBackToFriendList(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("com/example/laborator6map/friendlist-view.fxml"));
        Parent root = (Parent) fxmlLoader.load();
        ControllerFriendList controller = fxmlLoader.<ControllerFriendList>getController();
        controller.setUserId(userIdLoggedIn);
        stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void onClickAcceptFriendRequest(ActionEvent actionEvent) {
        Prietenie prietenieSelected = TableViewFriendRequest.getSelectionModel().getSelectedItem();
        if(prietenieSelected.getStatus().equals("pending"))
        {
            serviceNetwork.acceptaPrietenie(prietenieSelected.getId().getLeft(),prietenieSelected.getId().getRight());
            dataList.clear();
            for (Prietenie prietenie : serviceNetwork.friendRequestForAUser(userIdLoggedIn)) {
                dataList.add(prietenie);
            }
            TableViewFriendRequest.setItems(dataList);
        }
        else if(prietenieSelected.getStatus().equals("declined")){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Cerere de prietenie");
            alert.setHeaderText("Aceasta cerere nu poate fi acceptata deoarece a fost refuzata");
            alert.setContentText("");
            alert.showAndWait().ifPresent(rs -> {
                if (rs == ButtonType.OK) {
                    System.out.println("Pressed OK.");
                }
            });
        }
    }

    public void onClickDeclineFriendRequest(ActionEvent actionEvent) {
        Prietenie prietenieSelected = TableViewFriendRequest.getSelectionModel().getSelectedItem();
        if(prietenieSelected.getStatus().equals("pending"))
        {
            serviceNetwork.respingePrietenie(prietenieSelected.getId().getLeft(),prietenieSelected.getId().getRight());
            dataList.clear();
            for (Prietenie prietenie : serviceNetwork.friendRequestForAUser(userIdLoggedIn)) {
                dataList.add(prietenie);
            }
            TableViewFriendRequest.setItems(dataList);
        }
        else if(prietenieSelected.getStatus().equals("declined")){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Cerere de prietenie");
            alert.setHeaderText("Aceasta cerere a fost deja refuzata");
            alert.setContentText("");
            alert.showAndWait().ifPresent(rs -> {
                if (rs == ButtonType.OK) {
                    System.out.println("Pressed OK.");
                }
            });
        }
    }
}
