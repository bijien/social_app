package com.example.laborator6map.domain;

import java.util.Objects;

public class Utilizator extends Entity<Long>{

    private String firstName;
    private String lastName;
    private String userName;
    private String password;

    public Utilizator(String firstName, String lastName, String userName, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.password = password;
    }

    /**
     *
     * @return the first name of the user
     */
    public String getFirstName() {
        return firstName;
    }


    /**
     *
     * @return the last name of the user
     */
    public String getLastName() {
        return lastName;
    }



    /**
     *
     * @return the friend list of the user
     */
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Utilizator{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Utilizator that = (Utilizator) o;
        return userName.equals(that.userName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName);
    }
}
