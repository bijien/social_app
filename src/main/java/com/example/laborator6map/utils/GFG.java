package com.example.laborator6map.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class GFG {

    // visited[] array to make nodes visited
    // src is starting node for DFS traversal
    // prev_len is sum of cable length till current node
    // max_len is pointer which stores the maximum length
    // of cable value after DFS traversal

    // Class containing left and
    // right child of current
    // node and key value
    public static class pair {

        public int x, y;

        public pair(int f, int s) {
            x = f;
            y = s;
        }
    }

    // maximum length of cable among the connected
    // cities
    static int max_len = Integer.MIN_VALUE;

    static void DFS(Vector<Vector<pair>> graph, int src,
                    int prev_len, boolean[] visited) {

        // Mark the src node visited
        visited[src] = true;

        // curr_len is for length of cable
        // from src city to its adjacent city
        int curr_len = 0;

        // Adjacent is pair type which stores
        // destination city and cable length
        pair adjacent;

        // Traverse all adjacent
        for (int i = 0; i < graph.get(src).size(); i++) {
            // Adjacent element
            adjacent = graph.get(src).get(i);

            // If node or city is not visited
            if (!visited[adjacent.x]) {
                // Total length of cable from
                // src city to its adjacent
                curr_len = prev_len + adjacent.y;
                // Call DFS for adjacent city
                DFS(graph, adjacent.x, curr_len, visited);
            }

            // If total cable length till
            // now greater than previous
            // length then update it
            if (max_len < curr_len) {
                max_len = curr_len;

            }

            // make curr_len = 0 for next adjacent
            curr_len = 0;
        }
    }

    // n is number of cities or nodes in graph
    // cable_lines is total cable_lines among the cities
    // or edges in graph
    public static int longestCable(Vector<Vector<pair>> graph, int n) {
        // call DFS for each city to find maximum
        // length of cable
        for (int i = 1; i <= n; i++) {
            // initialize visited array with 0
            boolean[] visited = new boolean[n + 1];

            // Call DFS for src vertex i
            DFS(graph, i, 0, visited);
        }

        return max_len;
    }


}