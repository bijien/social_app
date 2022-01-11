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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;

public class ControllerSentFriendRequests {
    private final ObservableList<Prietenie> dataList = FXCollections.observableArrayList();
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    public TableView<Prietenie> tableSentFriendRequests;
    @FXML
    public TableColumn<Prietenie, String> columnFirstNameSentFriendRequest;
    @FXML
    public TableColumn<Prietenie, String> columnLastNameSentFriendRequest;
    @FXML
    public TableColumn<Prietenie, String> columnDataSentFriendRequest;
    @FXML
    public TableColumn<Prietenie, String> columnStatusSentFriendRequest;
    @FXML
    public Button buttonRemoveFriendRequest;
    @FXML
    public Button buttonBack;

    private ServiceNetwork serviceNetwork;
    public ServiceNetwork getServiceNetwork() {
        return serviceNetwork;
    }

    public void setServiceNetwork(ServiceNetwork serviceNetwork) {
        this.serviceNetwork = serviceNetwork;
    }
    private Long userIdLoggedIn;

    @FXML
    private void initialize() {
        Platform.runLater(() -> {
            initializeSentFriendRequestList();
        });
    }

    private void initializeSentFriendRequestList() {
        columnFirstNameSentFriendRequest.setCellValueFactory(cellData-> new SimpleStringProperty(serviceNetwork.findUser(cellData.getValue().getId().getRight()).getFirstName()));
        columnLastNameSentFriendRequest.setCellValueFactory(cellData-> new SimpleStringProperty(serviceNetwork.findUser(cellData.getValue().getId().getRight()).getLastName()));
        columnDataSentFriendRequest.setCellValueFactory(new PropertyValueFactory<>("localDate"));
        columnStatusSentFriendRequest.setCellValueFactory(new PropertyValueFactory<>("status"));
        for (Prietenie prietenie : serviceNetwork.sentFriendRequestsForAUser(userIdLoggedIn)) {
            dataList.add(prietenie);
        }
        tableSentFriendRequests.setItems(dataList);
    }

    public void setUserId(Long userIdLoggedIn) {
        this.userIdLoggedIn = userIdLoggedIn;
    }

    public void onClickRemoveFriendRequest(ActionEvent actionEvent) {
        Prietenie prietenieSelected = tableSentFriendRequests.getSelectionModel().getSelectedItem();
        serviceNetwork.deletePrietenie(prietenieSelected.getId().getLeft(),prietenieSelected.getId().getRight());
        dataList.clear();
        for (Prietenie prietenie : serviceNetwork.sentFriendRequestsForAUser(userIdLoggedIn)) {
            dataList.add(prietenie);
        }
        tableSentFriendRequests.setItems(dataList);
    }

    public void onClickGoBack(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("com/example/laborator6map/friendlist-view.fxml"));
        Parent root = (Parent) fxmlLoader.load();
        ControllerFriendList controller = fxmlLoader.<ControllerFriendList>getController();
        controller.setServiceNetwork(this.getServiceNetwork());
        controller.setUserId(userIdLoggedIn);
        stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
