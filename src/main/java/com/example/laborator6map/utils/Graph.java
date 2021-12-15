package com.example.laborator6map.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class Graph {
    private int V; // No. of vertices in graph.

    private LinkedList<Integer>[] adj; // Adjacency List
    // representation

    ArrayList<ArrayList<Integer>> components
            = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public Graph(int v)
    {
        V = v;
        adj = new LinkedList[v+1];

        for (int i = 1; i <= v; i++)
            adj[i] = new LinkedList();
    }

    public void addEdge(int u, int w)
    {
        adj[u].add(w);
        adj[w].add(u); // Undirected Graph.
    }

    void DFSUtil(int v, boolean[] visited,
                 ArrayList<Integer> al)
    {
        visited[v] = true;
        al.add(v);
        //System.out.print(v + " ");
        Iterator<Integer> it = adj[v].iterator();

        while (it.hasNext()) {
            int n = it.next();
            if (!visited[n])
                DFSUtil(n, visited, al);
        }
    }

    public void DFS()
    {
        boolean[] visited = new boolean[V+1];

        for (int i = 1; i <= V; i++) {
            ArrayList<Integer> al = new ArrayList<>();
            if (!visited[i]) {
                DFSUtil(i, visited, al);
                components.add(al);
            }
        }
    }

    public int ConnecetedComponents() { return components.size(); }
}
