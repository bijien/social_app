package com.example.laborator6map.ui;

import com.example.laborator6map.domain.Utilizator;
import com.example.laborator6map.service.ServiceNetwork;
import com.example.laborator6map.service.ServiceUser;
import com.example.laborator6map.validators.RepositoryException;
import com.example.laborator6map.validators.ValidationException;

import java.security.Provider;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class UserInterface {
    private ServiceNetwork serviceNetwork;

    public UserInterface(ServiceNetwork serviceNetwork) {
        this.serviceNetwork = serviceNetwork;
    }

    public void run() {
        int command;
        Scanner scanner = new Scanner(System.in);
        Scanner scannerNume = new Scanner(System.in);
        while (true) {
            System.out.println("0: Exit");
            System.out.println("1: Afiseaza utilizatori");
            System.out.println("2: Adauga utilizator");
            System.out.println("3: Sterge utilizator");
            System.out.println("4: Log in");
            command = scanner.nextInt();
            try {
                switch (command) {
                    case 0:
                        return;
                    case 1:
                        serviceNetwork.getAllUtilizator().forEach(System.out::println);
                        break;
                    case 2:
                        System.out.println("Enter first name and last name");
                        String[] name = scannerNume.nextLine().split(" ");
                        serviceNetwork.addUtilizator(name[0], name[1], name[0]+name[1], "parola");
                        break;
                    case 3:
                        System.out.println("Enter ID");
                        Long id = scannerNume.nextLong();
                        serviceNetwork.deleteUtilizator(id);
                        break;
                    case 4:
                        System.out.println("Enter first name and last name");
                        name = scannerNume.nextLine().split(" ");
                        Long idUser = serviceNetwork.findUserByFirstNameAndLastName(name[0], name[1]).getId();
                        while (true) {
                            System.out.println("0: Exit");
                            System.out.println("1: Adauga prietenie");
                            System.out.println("2: Sterge prietenie");
                            System.out.println("3: Lista de prieteni pentru un utilizator");
                            System.out.println("4: Lista de prieteni pentru un utilizator dint-o anumita luna");
                            System.out.println("5: Inbox utilizator");
                            System.out.println("6: Trimite mesaj");
                            System.out.println("7: Raspunde mesaj");
                            System.out.println("8: Conversatie doi utilizatori");
                            System.out.println("9: Accepta prietenie");
                            System.out.println("10:Respinge prietenie");
                            System.out.println("11:Reply all");
                            Integer commandLogin = scanner.nextInt();
                            try {
                                switch (commandLogin) {
                                    case 0:
                                        return;
                                    case 1:
                                        System.out.println("Enter ID for user");
                                        Long id1 = scannerNume.nextLong();
                                        serviceNetwork.addPrietenie(idUser, id1);
                                        break;
                                    case 2:
                                        System.out.println("Enter ID for user1");
                                        id1 = scannerNume.nextLong();
                                        serviceNetwork.deletePrietenie(idUser, id1);
                                        break;
                                    case 3:
                                        Long finalId1 = idUser;
                                        serviceNetwork.friendListForAUser(finalId1).forEach(x -> {
                                            if (x.getId().getLeft().equals(finalId1)) {
                                                System.out.println(serviceNetwork.findUser(x.getId().getRight()).getFirstName() + "|" +
                                                        serviceNetwork.findUser(x.getId().getRight()).getLastName() + "|" +
                                                        x.getLocalDate());
                                            } else {
                                                System.out.println(serviceNetwork.findUser(x.getId().getLeft()).getFirstName() + "|" +
                                                        serviceNetwork.findUser(x.getId().getLeft()).getLastName() + "|" +
                                                        x.getLocalDate());
                                            }
                                        });
                                        break;
                                    case 4:
                                        System.out.println("Enter month");
                                        Integer month = scannerNume.nextInt();
                                        Long finalId = idUser;
                                        serviceNetwork.friendListForAUserAndDate(finalId, month).forEach(x -> {
                                            if (x.getId().getLeft().equals(finalId)) {
                                                System.out.println(serviceNetwork.findUser(x.getId().getRight()).getFirstName() + "|" +
                                                        serviceNetwork.findUser(x.getId().getRight()).getLastName() + "|" +
                                                        x.getLocalDate());
                                            } else {
                                                System.out.println(serviceNetwork.findUser(x.getId().getLeft()).getFirstName() + "|" +
                                                        serviceNetwork.findUser(x.getId().getLeft()).getLastName() + "|" +
                                                        x.getLocalDate());
                                            }
                                        });
                                        break;
                                    case 5:
                                        id = idUser;
                                        serviceNetwork.mesajeUtilizator(id).forEach(x -> {
                                            System.out.println(x.getId() + "|from: " + x.getFrom() + "|to:" + x.getTo() + "|date: " + x.getData().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")) + "|mesaj: " +
                                                    "" + x.getMessage() + "|reply: " + x.getReply());
                                        });
                                        break;
                                    case 6:
                                        System.out.println("Enter message");
                                        scanner.nextLine();
                                        String mesaj = scanner.nextLine();
                                        id1 = idUser;
                                        System.out.println("Enter number of users");
                                        Integer numarUtilizatori = scannerNume.nextInt();
                                        List<Long> receiversId = new ArrayList<>();
                                        for (int i = 0; i < numarUtilizatori.intValue(); i++) {
                                            System.out.println("Enter id for receiver");
                                            Long id2 = scannerNume.nextLong();
                                            receiversId.add(id2);
                                        }
                                        serviceNetwork.trimiteMesaj(id1, receiversId, mesaj);
                                        break;
                                    case 7:
                                        System.out.println("Enter message");
                                        scanner.nextLine();
                                        mesaj = scanner.nextLine();
                                        id1 = idUser;
                                        System.out.println("Enter ID for message you want to reply");
                                        Long id2 = scannerNume.nextLong();
                                        serviceNetwork.raspundeMesaj(id1, id2, mesaj);
                                        break;
                                    case 8:
                                        id1 = idUser;
                                        System.out.println("Enter id for second user");
                                        id2 = scannerNume.nextLong();
                                        serviceNetwork.conversatieUtilizatori(id1, id2).forEach(x -> {
                                            System.out.println(x.getId() + "|from: " + x.getFrom() + "|to:" + x.getTo() + "|date: " + x.getData().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")) + "|mesaj: " +
                                                    "" + x.getMessage() + "|reply: " + x.getReply());
                                        });
                                        break;
                                    case 9:
                                        System.out.println("Enter id for sender");
                                        id1 = scannerNume.nextLong();
                                        id2 = idUser;
                                        serviceNetwork.acceptaPrietenie(id1, id2);
                                        break;
                                    case 10:
                                        System.out.println("Enter id for sender");
                                        id1 = scannerNume.nextLong();
                                        id2 = idUser;
                                        serviceNetwork.respingePrietenie(id1, id2);
                                        break;
                                    case 11:
                                        System.out.println("Enter message");
                                        scanner.nextLine();
                                        mesaj = scanner.nextLine();
                                        id1 = idUser;
                                        System.out.println("Enter id for message you want to reply");
                                        id2 = scannerNume.nextLong();
                                        serviceNetwork.replyAll(id1, id2, mesaj);
                                        break;
                                    default:
                                        System.out.println("Comanda invalida!");
                                }
                            } catch (ValidationException validationException) {
                                System.out.println(validationException.toString());
                            } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
                                System.out.println("Date invalide");
                            } catch (RepositoryException repositoryException) {
                                System.out.println(repositoryException.toString());
                            } catch (InputMismatchException inputMismatchException) {
                                System.out.println("Wrong input");
                            }
                        }
                    default:
                        System.out.println("Comanda invalida!");
                }
            } catch (ValidationException validationException) {
                System.out.println(validationException.toString());
            } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
                System.out.println("Date invalide");
            } catch (RepositoryException repositoryException) {
                System.out.println(repositoryException.toString());
            } catch (InputMismatchException inputMismatchException) {
                System.out.println("Wrong input");
            }
        }
    }
}




