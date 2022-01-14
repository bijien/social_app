package com.example.laborator6map.controller;

import com.example.laborator6map.domain.*;
import com.example.laborator6map.service.ServiceNetwork;
import javafx.application.Platform;
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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class ControllerFriendList {
    @FXML
    public TableView<Utilizator> tableViewUsers;
    @FXML
    public TableColumn<Utilizator, String> columnFirstName;
    @FXML
    public TableColumn<Utilizator, String> columnLastName;
    @FXML
    public TableColumn<Utilizator, String> columnUsername;

    private final ObservableList<Utilizator> dataList = FXCollections.observableArrayList();
    @FXML
    public TextField filterField;
    @FXML
    public TextField filterFieldFriendList;
    @FXML
    public TableView<Utilizator> tableViewFriendList;
    @FXML
    public TableColumn<Utilizator, String> columnFirstNameFreindList;
    @FXML
    public TableColumn<Utilizator, String> columnLastNameFriendList;
    @FXML
    public TableColumn<Utilizator, String> columnUsernameFriendList;
    private final ObservableList<Utilizator> dataListFriends = FXCollections.observableArrayList();

    private Stage stage;
    @FXML
    public Label labelLoggedInUser;
    @FXML
    public Button buttonLogOut;

    private Long userIdLoggedIn;

    private ServiceNetwork serviceNetwork;

    public ServiceNetwork getServiceNetwork() {
        return serviceNetwork;
    }
    private boolean sameSession;
    public void setSameSession(boolean sameSession) {
        this.sameSession = sameSession;
    }

    public void setServiceNetwork(ServiceNetwork serviceNetwork) {
        this.serviceNetwork = serviceNetwork;
    }

    @FXML
    private void initialize() {
        Platform.runLater(() -> {
            setLabelUserLoggedIn(userIdLoggedIn);
            initializeUserList();
            initializeFriendList();
            serviceNetwork.deletePastEventsByADate(LocalDateTime.now());
            notifyAboutUpcomingEvents();
        });
    }

    private void notifyAboutUpcomingEvents() {
        for(Eveniment eveniment : serviceNetwork.getAllEvenimenteWhereUserParticipating(userIdLoggedIn)) {
            long dateDiff = ChronoUnit.DAYS.between(LocalDateTime.now(), eveniment.getData());
            if(serviceNetwork.isParticipating(eveniment.getId(), userIdLoggedIn).equals("DA") && dateDiff <= 3 && !sameSession) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Urmeaza urmatorul eveniment");
                alert.setHeaderText("Organizator: " + eveniment.getCreator().getFirstName() + " " + eveniment.getCreator().getLastName() + "\n" +
                        "Nume: " + eveniment.getNume() + "\n" +
                        "Locatie: " + eveniment.getLocatie() + "\n" +
                        "Descriere: " + eveniment.getDescriere() + "\n" +
                        "Data :" + eveniment.getData().format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm"))
                );
                alert.setContentText("");
                alert.showAndWait().ifPresent(rs -> {
                    if (rs == ButtonType.OK) {
                        System.out.println("Pressed OK.");
                    }
                });

            }
        }
    }


    public void initializeUserList() {
        columnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        columnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        columnUsername.setCellValueFactory(new PropertyValueFactory<>("userName"));
        for (Utilizator utilizator : serviceNetwork.listaUtilizatoriCareNusuntPrieteni(userIdLoggedIn)) {
            dataList.add(utilizator);
        }
        FilteredList<Utilizator> filteredData = new FilteredList<>(dataList, b -> true);
        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(utilizator -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                if (utilizator.getFirstName().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true;
                } else if (utilizator.getLastName().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true;
                } else if (utilizator.getUserName().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true;
                } else if ((utilizator.getFirstName() + " " + utilizator.getLastName()).toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true;
                } else
                    return false;
            });
        });
        SortedList<Utilizator> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableViewUsers.comparatorProperty());
        tableViewUsers.setItems(sortedData);
    }

    public void initializeFriendList() {
        columnFirstNameFreindList.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        columnLastNameFriendList.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        columnUsernameFriendList.setCellValueFactory(new PropertyValueFactory<>("userName"));
        for (Prietenie prietenie : serviceNetwork.friendListForAUser(userIdLoggedIn)) {
            if (!prietenie.getId().getLeft().equals(userIdLoggedIn))
                dataListFriends.add(serviceNetwork.findUser(prietenie.getId().getLeft()));
            else if (!prietenie.getId().getRight().equals(userIdLoggedIn))
                dataListFriends.add(serviceNetwork.findUser(prietenie.getId().getRight()));
        }
        FilteredList<Utilizator> filteredData = new FilteredList<>(dataListFriends, b -> true);
        filterFieldFriendList.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(utilizator -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                if (utilizator.getFirstName().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true;
                } else if (utilizator.getLastName().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true;
                } else if (utilizator.getUserName().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true;
                } else if ((utilizator.getFirstName() + " " + utilizator.getLastName()).toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true;
                } else
                    return false;
            });
        });
        SortedList<Utilizator> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableViewFriendList.comparatorProperty());
        tableViewFriendList.setItems(sortedData);
    }


    public void setLabelUserLoggedIn(Long id) {
        labelLoggedInUser.setText("Logged in as " + serviceNetwork.findUser(id).getFirstName() +
                " " + serviceNetwork.findUser(id).getLastName());
    }


    public void setUserId(Long userIdLoggedIn) {
        this.userIdLoggedIn = userIdLoggedIn;
    }

    public void onClickLogOut(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("com/example/laborator6map/login-view.fxml"));
        Parent root = (Parent) fxmlLoader.load();
        ControllerLogin controller = fxmlLoader.<ControllerLogin>getController();
        controller.setServiceNetwork(this.getServiceNetwork());
        stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void onClickAddFriend(ActionEvent actionEvent) {
        Utilizator selectedUtilizator = tableViewUsers.getSelectionModel().getSelectedItem();
        if (selectedUtilizator == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Date incomplete");
            alert.setHeaderText("Trebuie sa selectati un utilizator din lista");
            alert.setContentText("Incercati din nou");
            alert.showAndWait().ifPresent(rs -> {
                if (rs == ButtonType.OK) {
                    System.out.println("Pressed OK.");
                }
            });
        } else {
            serviceNetwork.addPrietenie(userIdLoggedIn, selectedUtilizator.getId());
            dataList.clear();
            initializeUserList();
        }
    }

    public void onClickRemoveFriend(ActionEvent actionEvent) {
        Utilizator selectedUtizator = tableViewFriendList.getSelectionModel().getSelectedItem();
        if (selectedUtizator == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Date incomplete");
            alert.setHeaderText("Trebuie sa selectati un utilizator din lista");
            alert.setContentText("Incercati din nou");
            alert.showAndWait().ifPresent(rs -> {
                if (rs == ButtonType.OK) {
                    System.out.println("Pressed OK.");
                }
            });
        } else {
            serviceNetwork.deletePrietenie(userIdLoggedIn, selectedUtizator.getId());
            dataListFriends.clear();
            initializeFriendList();
            dataList.clear();
            initializeUserList();
        }
    }

    public void onClickForFriendRequests(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("com/example/laborator6map/friendrequest-view.fxml"));
        Parent root = (Parent) fxmlLoader.load();
        ControllerFriendRequest controller = fxmlLoader.<ControllerFriendRequest>getController();
        controller.setServiceNetwork(this.getServiceNetwork());
        controller.setUserId(userIdLoggedIn);
        controller.setSameSession(true);
        stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void onClickViewSentFriendRequests(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("com/example/laborator6map/sentfriendrequests-view.fxml"));
        Parent root = (Parent) fxmlLoader.load();
        ControllerSentFriendRequests controller = fxmlLoader.<ControllerSentFriendRequests>getController();
        controller.setServiceNetwork(this.getServiceNetwork());
        controller.setUserId(userIdLoggedIn);
        controller.setSameSession(true);
        stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void onClickStartChat(ActionEvent actionEvent) throws IOException {
        if (tableViewFriendList.getSelectionModel().getSelectedItem() == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Date incomplete");
            alert.setHeaderText("Trebuie sa selectati un utilizator din lista");
            alert.setContentText("Incercati din nou");
            alert.showAndWait().ifPresent(rs -> {
                if (rs == ButtonType.OK) {
                    System.out.println("Pressed OK.");
                }
            });
        } else {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("com/example/laborator6map/chat-view.fxml"));
            Parent root = (Parent) fxmlLoader.load();
            ControllerChat controller = fxmlLoader.<ControllerChat>getController();
            controller.setServiceNetwork(this.getServiceNetwork());
            controller.setUserId(userIdLoggedIn);
            controller.setUserIdChattingTo(tableViewFriendList.getSelectionModel().getSelectedItem().getId());
            controller.setSameSession(true);
            stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
    }

    public void onClickRapoarte(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("com/example/laborator6map/rapoarte-view.fxml"));
        Parent root = (Parent) fxmlLoader.load();
        ControllerRapoarte controller = fxmlLoader.<ControllerRapoarte>getController();
        controller.setServiceNetwork(this.getServiceNetwork());
        controller.setUserId(userIdLoggedIn);
        controller.setSameSession(true);
        stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void onClickCreeazaEveniment(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("com/example/laborator6map/createevent-view.fxml"));
        Parent root = (Parent) fxmlLoader.load();
        ControllerCreateEvent controller = fxmlLoader.<ControllerCreateEvent>getController();
        controller.setServiceNetwork(this.getServiceNetwork());
        controller.setUserId(userIdLoggedIn);
        controller.setSameSession(true);
        stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void onClickVeziEvenimente(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("com/example/laborator6map/events-view.fxml"));
        Parent root = (Parent) fxmlLoader.load();
        ControllerEvent controller = fxmlLoader.<ControllerEvent>getController();
        controller.setServiceNetwork(this.getServiceNetwork());
        controller.setUserId(userIdLoggedIn);
        controller.setSameSession(true);
        stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void onClickEvenimenteLaCareParticip(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("com/example/laborator6map/participatingevents-view.fxml"));
        Parent root = (Parent) fxmlLoader.load();
        ControllerParticipatingEvents controller = fxmlLoader.<ControllerParticipatingEvents>getController();
        controller.setServiceNetwork(this.getServiceNetwork());
        controller.setUserId(userIdLoggedIn);
        controller.setSameSession(true);
        stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
