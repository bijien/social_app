package com.example.laborator6map.controller;

import com.example.laborator6map.domain.Message;
import com.example.laborator6map.service.ServiceNetwork;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ControllerChat {
    @FXML
    public Label labelUser;
    @FXML
    public ListView<String> listViewChat;
    @FXML
    public TextField textFieldChat;
    private Long userIdLoggedIn;
    private Long userIdChattingTo;
    private ServiceNetwork serviceNetwork;
    private Stage stage;
    private final ObservableList<String> dataList = FXCollections.observableArrayList();

    public ServiceNetwork getServiceNetwork() {
        return serviceNetwork;
    }

    public void setServiceNetwork(ServiceNetwork serviceNetwork) {
        this.serviceNetwork = serviceNetwork;
    }

    @FXML
    private void initialize() {
        Platform.runLater(() -> {
            setLabelUserLoggedIn(userIdChattingTo);
            initializeListViewWithMessages();
        });
    }

    private void initializeListViewWithMessages() {

        for (Message message : serviceNetwork.conversatieUtilizatori(userIdLoggedIn, userIdChattingTo)) {
            if (message.getFrom().getId().equals(userIdLoggedIn))
                dataList.add("You: " + message.getMessage());
            else
                dataList.add(message.getFrom().getFirstName() + " " + message.getFrom().getLastName() + ": " + message.getMessage());
        }
        listViewChat.setItems(dataList);
    }

    public void setUserId(Long userIdLoggedIn) {
        this.userIdLoggedIn = userIdLoggedIn;
    }

    public void setUserIdChattingTo(Long id) {
        this.userIdChattingTo = id;
    }

    public void setLabelUserLoggedIn(Long id) {
        labelUser.setText("Chatting with " + serviceNetwork.findUser(id).getFirstName() +
                " " + serviceNetwork.findUser(id).getLastName());
    }

    public void onClickSend(ActionEvent actionEvent) {
        if (textFieldChat.getText().equals("")) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Mesaj");
            alert.setHeaderText("Mesajul nu poate fi trimis");
            alert.setContentText("");
            alert.showAndWait().ifPresent(rs -> {
                if (rs == ButtonType.OK) {
                    System.out.println("Pressed OK.");
                }
            });
        } else {
            List<Long> userList = new ArrayList<Long>();
            userList.add(userIdChattingTo);
            serviceNetwork.trimiteMesaj(userIdLoggedIn, userList, textFieldChat.getText());
            dataList.clear();
            initializeListViewWithMessages();
            textFieldChat.clear();

        }
    }

    public void onClickBack(ActionEvent actionEvent) throws IOException {
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

