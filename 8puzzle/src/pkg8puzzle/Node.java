package com.example.npuzzleai;

import java.util.Arrays;
import java.util.Vector;

public class Node {
    public State state;
    public int f;
    public int g;
    public int h;
    public int cost;
    public Node parent;

    public Node(State state, int cost) {
        this.state = state;
        this.cost = cost;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node n = (Node) o;
        return Arrays.equals(state.value, n.state.value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(state.value);
    }

    public int estimate(State goalState) {
        return state.estimate(goalState);
    }

    public Vector<Node> successors() {
        Vector<Node> nodes = new Vector<>();
        Vector<State> states = state.successors();
        for (State value : states) {
            nodes.add(new Node(value, 1));
        }
        return nodes;
    }
}