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

public class ControllerParticipatingEvents {
    public TableView<Eveniment> tableViewParticipatingEvents;
    public TableColumn<Eveniment, String> columnEventsParticipatingCreator;
    public TableColumn<Eveniment, String> columnEventsParticipatingNume;
    public TableColumn<Eveniment, String> columnEventsParticipatingLocatie;
    public TableColumn<Eveniment, String> columnEventsParticipatingDescriere;
    public TableColumn<Eveniment, String> columnEventsParticipatingData;
    public TableColumn<Eveniment, String> columnEventsParticipatingAbonat;
    private ServiceNetwork serviceNetwork;
    private Long userIdLoggedIn;
    private final ObservableList<Eveniment> dataList = FXCollections.observableArrayList();

    Stage stage;
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
        columnEventsParticipatingCreator.setCellValueFactory(cellData -> new SimpleStringProperty(serviceNetwork.findUser(cellData.getValue().getCreator().getId()).getFirstName() + " " + serviceNetwork.findUser(cellData.getValue().getCreator().getId()).getLastName()));
        columnEventsParticipatingNume.setCellValueFactory(new PropertyValueFactory<>("nume"));
        columnEventsParticipatingLocatie.setCellValueFactory(new PropertyValueFactory<>("locatie"));
        columnEventsParticipatingDescriere.setCellValueFactory(new PropertyValueFactory<>("descriere"));
        columnEventsParticipatingData.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getData().format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm"))));
        columnEventsParticipatingAbonat.setCellValueFactory(cellData -> new SimpleStringProperty(serviceNetwork.isParticipating(cellData.getValue().getId(), userIdLoggedIn)));

        for (Eveniment eveniment : serviceNetwork.getAllEvenimenteWhereUserParticipating(userIdLoggedIn)) {
            dataList.add(eveniment);
        }
        tableViewParticipatingEvents.setItems(dataList);
    }


    public void onClickAbonareNotificari(ActionEvent actionEvent) {
        Eveniment eveniment = tableViewParticipatingEvents.getSelectionModel().getSelectedItem();
        if (eveniment == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("");
            alert.setHeaderText("Trebuie sa selectati un eveniment");
            alert.setContentText("Incercati din nou");
            alert.showAndWait().ifPresent(rs -> {
                if (rs == ButtonType.OK) {
                    System.out.println("Pressed OK.");
                }
            });

        } else {
            if (serviceNetwork.isParticipating(eveniment.getId(), userIdLoggedIn).equals("DA")) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("");
                alert.setHeaderText("Sunteti deja abonat la notificarile acestui eveniment");
                alert.setContentText("");
                alert.showAndWait().ifPresent(rs -> {
                    if (rs == ButtonType.OK) {
                        System.out.println("Pressed OK.");
                    }
                });
            } else {
                serviceNetwork.abonareLaEveniment(eveniment.getId(), userIdLoggedIn);
                dataList.clear();
                initializeEventList();
            }
        }
    }

    public void onClickDezabonareNotificari(ActionEvent actionEvent) {
        Eveniment eveniment = tableViewParticipatingEvents.getSelectionModel().getSelectedItem();
        if (eveniment == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("");
            alert.setHeaderText("Trebuie sa selectati un eveniment");
            alert.setContentText("Incercati din nou");
            alert.showAndWait().ifPresent(rs -> {
                if (rs == ButtonType.OK) {
                    System.out.println("Pressed OK.");
                }
            });

        } else {
            if (serviceNetwork.isParticipating(eveniment.getId(), userIdLoggedIn).equals("NU")) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("");
                alert.setHeaderText("Sunteti deja dezabonat la notificarile acestui eveniment");
                alert.setContentText("");
                alert.showAndWait().ifPresent(rs -> {
                    if (rs == ButtonType.OK) {
                        System.out.println("Pressed OK.");
                    }
                });
            } else {
                serviceNetwork.dezabonareLaEveniment(eveniment.getId(), userIdLoggedIn);
                dataList.clear();
                initializeEventList();
            }
        }
    }

    public void onClickNuMaiParticip(ActionEvent actionEvent) {
        Eveniment eveniment = tableViewParticipatingEvents.getSelectionModel().getSelectedItem();
        if (eveniment == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("");
            alert.setHeaderText("Trebuie sa selectati un eveniment");
            alert.setContentText("Incercati din nou");
            alert.showAndWait().ifPresent(rs -> {
                if (rs == ButtonType.OK) {
                    System.out.println("Pressed OK.");
                }
            });
        } else {
            serviceNetwork.nuMaiParticipaLaEveniment(eveniment.getId(), userIdLoggedIn);
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
