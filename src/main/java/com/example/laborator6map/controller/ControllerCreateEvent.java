package com.example.laborator6map.controller;

import com.example.laborator6map.service.ServiceNetwork;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ControllerCreateEvent {
    public TextField textFieldNume;
    public TextField textFieldLocatie;
    public TextField textFieldDescriere;
    public DatePicker datePickerData;
    public TextField textFieldOra;
    public TextField textFieldMinute;

    private Stage stage;

    private ServiceNetwork serviceNetwork;
    private Long userIdLoggedIn;
    private boolean sameSession;

    public void setSameSession(boolean sameSession) {
        this.sameSession = sameSession;
    }

    public void setUserId(Long userIdLoggedIn) {
        this.userIdLoggedIn = userIdLoggedIn;
    }

    public ServiceNetwork getServiceNetwork() {
        return serviceNetwork;
    }

    public void setServiceNetwork(ServiceNetwork serviceNetwork) {
        this.serviceNetwork = serviceNetwork;
    }

    @FXML
    private void initialize() {
        Platform.runLater(() -> {
            datePickerData.setDayCellFactory(param -> new DateCell() {
                @Override
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    setDisable(empty || date.compareTo(LocalDate.now()) < 0);
                }
            });
        });
    }

    public void onClickCreeazaEveniment(ActionEvent actionEvent) {
        if (textFieldNume.getText().equals("") || textFieldLocatie.getText().equals("") || textFieldDescriere.getText().equals("") ||
                datePickerData.getValue() == null || textFieldOra.getText().equals("") || textFieldMinute.getText().equals("")) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Date incomplete");
            alert.setHeaderText("Toate datele trebuie completate");
            alert.setContentText("Incercati din nou");
            alert.showAndWait().ifPresent(rs -> {
                if (rs == ButtonType.OK) {
                    System.out.println("Pressed OK.");
                }
            });
        } else {
            try {
                LocalDate localDate = datePickerData.getValue();
                LocalDateTime localDateTime = localDate.atTime(Integer.parseInt(textFieldOra.getText()), Integer.parseInt(textFieldMinute.getText()));
                if (localDateTime.isBefore(LocalDateTime.now())) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Date incorecte");
                    alert.setHeaderText("Ora evenimentului este incorecta");
                    alert.setContentText("Incercati din nou");
                    alert.showAndWait().ifPresent(rs -> {
                        if (rs == ButtonType.OK) {
                            System.out.println("Pressed OK.");
                        }
                    });
                } else {
                    serviceNetwork.createEvent(userIdLoggedIn, textFieldNume.getText(), textFieldLocatie.getText(), textFieldDescriere.getText(), localDateTime);

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("");
                    alert.setHeaderText("Eveniment creat cu succes");
                    alert.setContentText("");
                    alert.showAndWait().ifPresent(rs -> {
                        if (rs == ButtonType.OK) {
                            System.out.println("Pressed OK.");
                        }
                    });
                    textFieldMinute.clear();
                    textFieldLocatie.clear();
                    textFieldOra.clear();
                    textFieldDescriere.clear();
                    textFieldNume.clear();
                }
            } catch (NumberFormatException | DateTimeException ex) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Date incorecte");
                alert.setHeaderText("Ora evenimentului este incorecta");
                alert.setContentText("Incercati din nou");
                alert.showAndWait().ifPresent(rs -> {
                    if (rs == ButtonType.OK) {
                        System.out.println("Pressed OK.");
                    }
                });
            }
        }
    }

    public void onclickGoBack(ActionEvent actionEvent) throws IOException {
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
