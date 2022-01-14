package com.example.laborator6map.controller;

import com.example.laborator6map.domain.Message;
import com.example.laborator6map.domain.Tuple;
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
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ControllerChat {
    @FXML
    public Label labelUser;
    @FXML
    public ListView<String> listViewChat;
    @FXML
    public TextField textFieldChat;
    public Button previousButton;
    public Button nextButton;
    private Long userIdLoggedIn;
    private Long userIdChattingTo;
    private ServiceNetwork serviceNetwork;
    private Stage stage;
    private final ObservableList<String> dataList = FXCollections.observableArrayList();
    private static final int NUM_OF_MESSAGES_TO_LOAD = 5;
    private int currentPage = 1;
    private int maxPages;
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

    @FXML
    private void initialize() {
        Platform.runLater(() -> {
            setLabelUserLoggedIn(userIdChattingTo);
            //initializeListViewWithMessages();
            initializeListViewPaginated();
        });
    }

    private Tuple<Integer, Integer> getOffsetLimit(Integer currentPage, Integer pagesize) {
        return new Tuple<>((currentPage - 1) * pagesize, pagesize);
    }

    public List<Message> getConversationPaginated(Integer page, Integer pageSize) {
        Utilizator myUser = serviceNetwork.findUser(userIdLoggedIn);
        Utilizator friendUser = serviceNetwork.findUser(userIdChattingTo);
        Tuple<Integer, Integer> limitOffset = getOffsetLimit(page, pageSize);
        Integer offset = limitOffset.getLeft();
        Integer limit = limitOffset.getRight();
        var conversation = serviceNetwork.getConversationPaginated(myUser.getId(), friendUser.getId(), offset, limit);
        List<Message> messageList = new ArrayList<>();
        for (Message message : conversation) {
            messageList.add(message);
        }
        messageList.sort(Comparator.comparing(Message::getData));
        return messageList;
    }

    private void initializeListViewPaginated() {

        dataList.clear();
        currentPage = 1;
        updatePaginationButtons();
        List<Message> messageList = new ArrayList<>();
        for (Message message : serviceNetwork.conversatieUtilizatori(userIdLoggedIn, userIdChattingTo)) {
            messageList.add(message);
        }
        if (messageList.size() % NUM_OF_MESSAGES_TO_LOAD == 0) {
            maxPages = messageList.size() / NUM_OF_MESSAGES_TO_LOAD;
        } else {
            maxPages = (messageList.size() / NUM_OF_MESSAGES_TO_LOAD) + 1;
        }
        List<Message> conversation = new ArrayList<>();
        for (Message messageConversation : getConversationPaginated(currentPage, NUM_OF_MESSAGES_TO_LOAD)) {
            conversation.add(messageConversation);
        }
        for (Message m : conversation) {
            if (m.getFrom().getId().equals(userIdLoggedIn))
                dataList.add("You: " + m.getMessage());
            else
                dataList.add(m.getFrom().getFirstName() + " " + m.getFrom().getLastName() + ": " + m.getMessage());
        }
        listViewChat.setItems(dataList);
    }

    private void updatePaginationButtons() {
        previousButton.setDisable(currentPage == 1);
        nextButton.setDisable(currentPage == maxPages);
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
            //dataList.clear();
            //initializeListViewWithMessages();
            initializeListViewPaginated();
            textFieldChat.clear();

        }
    }

    public void onClickBack(ActionEvent actionEvent) throws IOException {
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

    public void onClickGoPreviousPage(ActionEvent actionEvent) {
        currentPage--;
        dataList.clear();
        List<Message> conversation = new ArrayList<>();
        for (Message messageConversation : getConversationPaginated(currentPage, NUM_OF_MESSAGES_TO_LOAD)) {
            conversation.add(messageConversation);
        }
        for (Message m : conversation) {
            if (m.getFrom().getId().equals(userIdLoggedIn))
                dataList.add("You: " + m.getMessage());
            else
                dataList.add(m.getFrom().getFirstName() + " " + m.getFrom().getLastName() + ": " + m.getMessage());
        }
        updatePaginationButtons();
    }


    public void onClickGoNextPage(ActionEvent actionEvent) {
        currentPage++;
        dataList.clear();
        List<Message> conversation = new ArrayList<>();
        for (Message messageConversation : getConversationPaginated(currentPage, NUM_OF_MESSAGES_TO_LOAD)) {
            conversation.add(messageConversation);
        }
        for (Message m : conversation) {
            if (m.getFrom().getId().equals(userIdLoggedIn))
                dataList.add("You: " + m.getMessage());
            else
                dataList.add(m.getFrom().getFirstName() + " " + m.getFrom().getLastName() + ": " + m.getMessage());
        }
        updatePaginationButtons();
    }
}

