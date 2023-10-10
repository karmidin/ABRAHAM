package com.example.abraham;

import java.util.ArrayList;
import java.util.PriorityQueue;

public class BestFirstSearch {
    static ArrayList<ArrayList<edge>> adj = new ArrayList<>();

    // initialize variabel adj
    public BestFirstSearch(int v)
    {
        for (int i = 0; i < v; i++)
            adj.add(new ArrayList<>());
    }

    // function for adding edges to graph
    static class edge implements Comparable<edge>
    {
        int v, cost;
        edge(int v, int cost)
        {
            this.v = v;
            this.cost = cost;
        }

        @Override public int compareTo(edge ed)
        {
            if (ed.cost < cost)
                return 1;
            else
                return -1;
        }
    }

    void addedge(int u, int v, int cost)
    {
        adj.get(u).add(new edge(v, cost));
        adj.get(v).add(new edge(u, cost));
    }

    static void process(int source, int target, int v)
    {
        PriorityQueue<BestFirstSearch.edge> pq = new PriorityQueue<>();
        boolean visited[] = new boolean[v];
        visited[0] = true;

        // sorting in pq gets done by first value of pair
        pq.add(new BestFirstSearch.edge(source, -1));
        while (!pq.isEmpty()) {
            int x = pq.poll().v;

            // Displaying the path having lowest cost
            if (target == x) {
                break;
            }
            for (BestFirstSearch.edge adjacentNodeEdge : adj.get(x)) {
                if (!visited[adjacentNodeEdge.v]) {
                    visited[adjacentNodeEdge.v] = true;
                    pq.add(adjacentNodeEdge);
                }
            }
        }
    }
}
