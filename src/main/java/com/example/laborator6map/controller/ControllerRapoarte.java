package com.example.laborator6map.controller;

import com.example.laborator6map.domain.Message;
import com.example.laborator6map.domain.Prietenie;
import com.example.laborator6map.domain.Utilizator;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import javax.swing.JFileChooser;

public class ControllerRapoarte {

    @FXML
    public TableView<Utilizator> tableViewFriendListRaport;
    @FXML
    public TableColumn<Utilizator, String> columnFirstNameFriendListRaport;
    @FXML
    public TableColumn<Utilizator, String> columnLastNameFriendListRaport;
    @FXML
    public TableColumn<Utilizator, String> columnUserNameFriendListRaport;
    @FXML
    public DatePicker datePickerDataInceput;
    @FXML
    public DatePicker datePickerDataSfarsit;

    private Stage stage;

    private boolean sameSession;

    public void setSameSession(boolean sameSession) {
        this.sameSession = sameSession;
    }


    private ServiceNetwork serviceNetwork;
    private Long userIdLoggedIn;
    private final ObservableList<Utilizator> dataListFriends = FXCollections.observableArrayList();

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
            initializeFriendList();
        });
    }

    private void initializeFriendList() {
        columnFirstNameFriendListRaport.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        columnLastNameFriendListRaport.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        columnUserNameFriendListRaport.setCellValueFactory(new PropertyValueFactory<>("userName"));
        for (Prietenie prietenie : serviceNetwork.friendListForAUser(userIdLoggedIn)) {
            if (!prietenie.getId().getLeft().equals(userIdLoggedIn))
                dataListFriends.add(serviceNetwork.findUser(prietenie.getId().getLeft()));
            else if (!prietenie.getId().getRight().equals(userIdLoggedIn))
                dataListFriends.add(serviceNetwork.findUser(prietenie.getId().getRight()));
        }
        tableViewFriendListRaport.setItems(dataListFriends);
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

    public void onClickRaportMesajeSiPrieteni(ActionEvent actionEvent) throws IOException {
        if (datePickerDataInceput.getValue() == null || datePickerDataSfarsit.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Date incomplete");
            alert.setHeaderText("Trebuie sa selectati data");
            alert.setContentText("Incercati din nou");
            alert.showAndWait().ifPresent(rs -> {
                if (rs == ButtonType.OK) {
                    System.out.println("Pressed OK.");
                }
            });
        } else {
            File dir = null;
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int resp = fc.showOpenDialog(null);
            if (resp == JFileChooser.APPROVE_OPTION) {
                dir = fc.getSelectedFile();
            }

            PDDocument document = new PDDocument();
            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);


            contentStream.beginText();
            contentStream.newLineAtOffset(20, 700);
            contentStream.setFont(PDType1Font.COURIER, 16);
            contentStream.showText("Raport de la data de " + datePickerDataInceput.getValue().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + " pana la data de " + datePickerDataSfarsit.getValue().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Utilizator: " + serviceNetwork.findUser(userIdLoggedIn).getFirstName() + " " + serviceNetwork.findUser(userIdLoggedIn).getLastName());
            contentStream.newLineAtOffset(0, -40);
            contentStream.setFont(PDType1Font.COURIER_BOLD, 20);
            contentStream.showText("Prieteni noi");
            contentStream.setFont(PDType1Font.COURIER, 16);
            for (Utilizator utilizator : serviceNetwork.findNewFriendsByDate(datePickerDataInceput.getValue(), datePickerDataSfarsit.getValue(), userIdLoggedIn)) {
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText(utilizator.getFirstName() + " " + utilizator.getLastName());
            }
            contentStream.newLineAtOffset(0, -60);
            contentStream.setFont(PDType1Font.COURIER_BOLD, 20);
            contentStream.showText("Mesaje");
            contentStream.newLineAtOffset(0, -20);
            contentStream.setFont(PDType1Font.COURIER, 16);
            for (Message message : serviceNetwork.findMessagesByDate(datePickerDataInceput.getValue(), datePickerDataSfarsit.getValue(), userIdLoggedIn)) {

                contentStream.showText("De la: " + message.getFrom().getFirstName() + " " + message.getFrom().getLastName());
                contentStream.newLineAtOffset(250, 0);
                contentStream.showText("Mesaj: " + message.getMessage());
                contentStream.newLineAtOffset(-250, -20);
            }
            contentStream.endText();
            contentStream.close();

            document.save(dir + "/raportPrieteniMesaje" + serviceNetwork.findUser(userIdLoggedIn).getUserName() + ".pdf");
            document.close();
        }

    }

    public void onClickRaportMesajeDeLaUnPrieten(ActionEvent actionEvent) throws IOException {
        Utilizator friend = tableViewFriendListRaport.getSelectionModel().getSelectedItem();
        if (datePickerDataInceput.getValue() == null || datePickerDataSfarsit.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Date incomplete");
            alert.setHeaderText("Trebuie sa selectati data");
            alert.setContentText("Incercati din nou");
            alert.showAndWait().ifPresent(rs -> {
                if (rs == ButtonType.OK) {
                    System.out.println("Pressed OK.");
                }
            });
        } else if (friend == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Date incomplete");
            alert.setHeaderText("Trebuie sa selectati un prieten din lista");
            alert.setContentText("Incercati din nou");
            alert.showAndWait().ifPresent(rs -> {
                if (rs == ButtonType.OK) {
                    System.out.println("Pressed OK.");
                }
            });
        } else {
            File dir = null;
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int resp = fc.showOpenDialog(null);
            if (resp == JFileChooser.APPROVE_OPTION) {
                dir = fc.getSelectedFile();
            }

            PDDocument document = new PDDocument();
            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            contentStream.beginText();
            contentStream.newLineAtOffset(20, 700);
            contentStream.setFont(PDType1Font.COURIER, 16);
            contentStream.showText("Raport de la data de " + datePickerDataInceput.getValue().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + " pana la data de " + datePickerDataSfarsit.getValue().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Utilizator: " + serviceNetwork.findUser(userIdLoggedIn).getFirstName() + " " + serviceNetwork.findUser(userIdLoggedIn).getLastName());
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Mesaje de la utilizatorul " + friend.getFirstName() + " " + friend.getLastName());
            contentStream.newLineAtOffset(0, -60);
            contentStream.setFont(PDType1Font.COURIER_BOLD, 20);
            contentStream.showText("Mesaje");
            contentStream.newLineAtOffset(0, -20);
            contentStream.setFont(PDType1Font.COURIER, 16);
            for (Message message : serviceNetwork.findMessagesByDateFromAFriend(datePickerDataInceput.getValue(), datePickerDataSfarsit.getValue(), userIdLoggedIn, friend.getId())) {
                contentStream.showText("Mesaj: " + message.getMessage());
                contentStream.newLineAtOffset(250, 0);
                contentStream.showText("Data: " + message.getData().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
                contentStream.newLineAtOffset(-250, -20);
            }
            contentStream.endText();
            contentStream.close();

            document.save(dir + "/raportMesajeDeLa" + friend.getUserName() + "Pentru" + serviceNetwork.findUser(userIdLoggedIn).getLastName() + ".pdf");
            document.close();

        }
    }

}
