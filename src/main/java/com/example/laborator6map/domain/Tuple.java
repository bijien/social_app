package com.example.laborator6map.domain;

import java.util.Objects;

public class Tuple<E1, E2> {
    private E1 e1;
    private E2 e2;

    public Tuple(E1 e1, E2 e2) {
        this.e1 = e1;
        this.e2 = e2;
    }

    /**
     *
     * @return the first element of tuple
     */
    public E1 getLeft() {
        return e1;
    }

    /**
     * set the first element of tuple
     * @param e1 element which we set as first element of tuple
     */
    public void setLeft(E1 e1) {
        this.e1 = e1;
    }

    /**
     *
     * @return the second element of tuple
     */
    public E2 getRight() {
        return e2;
    }

    /**
     * set the second element of tuple
     * @param e2 element which we set as second element of tuple
     */
    public void setRight(E2 e2) {
        this.e2 = e2;
    }

    @Override
    public String toString() {
        return "" + e1 + "," + e2;

    }

    @Override
    public boolean equals(Object obj) {
        return this.e1.equals(((Tuple) obj).e1) && this.e2.equals(((Tuple) obj).e2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(e1, e2);
    }
}
