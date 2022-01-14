package com.example.laborator6map.controller;


import com.example.laborator6map.domain.Prietenie;
import com.example.laborator6map.service.ServiceNetwork;
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

    private boolean sameSession;

    public void setSameSession(boolean sameSession) {
        this.sameSession = sameSession;
    }

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
            initializeFriendRequestList();
        });
    }

    private void initializeFriendRequestList() {
        columnFirstNameFriendRequest.setCellValueFactory(cellData -> new SimpleStringProperty(serviceNetwork.findUser(cellData.getValue().getId().getLeft()).getFirstName()));
        columnLastNameFriendRequest.setCellValueFactory(cellData -> new SimpleStringProperty(serviceNetwork.findUser(cellData.getValue().getId().getLeft()).getLastName()));
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
        controller.setServiceNetwork(this.getServiceNetwork());
        controller.setUserId(userIdLoggedIn);
        controller.setSameSession(true);
        stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void onClickAcceptFriendRequest(ActionEvent actionEvent) {
        Prietenie prietenieSelected = TableViewFriendRequest.getSelectionModel().getSelectedItem();
        if (prietenieSelected == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Date incomplete");
            alert.setHeaderText("Trebuie sa selectati o cerere din lista");
            alert.setContentText("Incercati din nou");
            alert.showAndWait().ifPresent(rs -> {
                if (rs == ButtonType.OK) {
                    System.out.println("Pressed OK.");
                }
            });
        } else {
            if (prietenieSelected.getStatus().equals("pending")) {
                serviceNetwork.acceptaPrietenie(prietenieSelected.getId().getLeft(), prietenieSelected.getId().getRight());
                dataList.clear();
                for (Prietenie prietenie : serviceNetwork.friendRequestForAUser(userIdLoggedIn)) {
                    dataList.add(prietenie);
                }
                TableViewFriendRequest.setItems(dataList);
            } else if (prietenieSelected.getStatus().equals("declined")) {
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
    }

    public void onClickDeclineFriendRequest(ActionEvent actionEvent) {
        Prietenie prietenieSelected = TableViewFriendRequest.getSelectionModel().getSelectedItem();
        if (prietenieSelected == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Date incomplete");
            alert.setHeaderText("Trebuie sa selectati o cerere din lista");
            alert.setContentText("Incercati din nou");
            alert.showAndWait().ifPresent(rs -> {
                if (rs == ButtonType.OK) {
                    System.out.println("Pressed OK.");
                }
            });
        } else {
            if (prietenieSelected.getStatus().equals("pending")) {
                serviceNetwork.respingePrietenie(prietenieSelected.getId().getLeft(), prietenieSelected.getId().getRight());
                dataList.clear();
                for (Prietenie prietenie : serviceNetwork.friendRequestForAUser(userIdLoggedIn)) {
                    dataList.add(prietenie);
                }
                TableViewFriendRequest.setItems(dataList);
            } else if (prietenieSelected.getStatus().equals("declined")) {
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
}
