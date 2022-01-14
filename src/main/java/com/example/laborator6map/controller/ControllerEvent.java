package com.example.laborator6map.controller;

import com.example.laborator6map.domain.Eveniment;
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
import java.time.format.DateTimeFormatter;

public class ControllerEvent {

    public TableView<Eveniment> tableViewEvents;
    public TableColumn<Eveniment, String> columnEventsCreator;
    public TableColumn<Eveniment, String> columnEventsNume;
    public TableColumn<Eveniment, String> columnEventsLocatie;
    public TableColumn<Eveniment, String> columnEventsDescriere;
    public TableColumn<Eveniment, String> columnEventsData;

    private Stage stage;


    private ServiceNetwork serviceNetwork;
    private Long userIdLoggedIn;
    private final ObservableList<Eveniment> dataList = FXCollections.observableArrayList();
    private boolean sameSession;
    public void setSameSession(boolean sameSession) {
        this.sameSession = sameSession;
    }

    public ServiceNetwork getServiceNetwork() {
        return serviceNetwork;
    }

    public void setServiceNetwork(ServiceNetwork serviceNetwork) {
        this.serviceNetwork = serviceNetwork;
    }

    public void setUserId(Long userIdLoggedIn) {
        this.userIdLoggedIn = userIdLoggedIn;
    }

    @FXML
    private void initialize() {
        Platform.runLater(() -> {
            initializeEventList();
        });
    }

    private void initializeEventList() {
        columnEventsCreator.setCellValueFactory(cellData -> new SimpleStringProperty(serviceNetwork.findUser(cellData.getValue().getCreator().getId()).getFirstName()+ " " + serviceNetwork.findUser(cellData.getValue().getCreator().getId()).getLastName()));
        columnEventsNume.setCellValueFactory(new PropertyValueFactory<>("nume"));
        columnEventsLocatie.setCellValueFactory(new PropertyValueFactory<>("locatie"));
        columnEventsDescriere.setCellValueFactory(new PropertyValueFactory<>("descriere"));
        columnEventsData.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getData().format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm"))));
        for(Eveniment eveniment : serviceNetwork.getAllEvenimenteWhereUserNotParticipating(userIdLoggedIn)) {
            dataList.add(eveniment);
        }
        tableViewEvents.setItems(dataList);
    }

    public void onClickParticipa(ActionEvent actionEvent) {
        Eveniment eveniment = tableViewEvents.getSelectionModel().getSelectedItem();
        if(eveniment == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("");
            alert.setHeaderText("Trebuie sa selectati un eveniment");
            alert.setContentText("");
            alert.showAndWait().ifPresent(rs -> {
                if (rs == ButtonType.OK) {
                    System.out.println("Pressed OK.");
                }
            });
        }
        else {
            serviceNetwork.participaLaEveniment(eveniment.getId(), userIdLoggedIn);
            dataList.clear();
            initializeEventList();
        }
    }

    public void onClickGoBack(ActionEvent actionEvent) throws IOException {
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
}
