/* @author Urvi Aryamane */

package edu.neu.coe.info6205.graphs.BFS_and_prims;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class BFS {
    private final int V;   // No. of vertices
    private final LinkedList<Integer>[] adj; //Adjacency Lists

    @SuppressWarnings("unchecked")
    public BFS(int v) {
        V = v;
        adj = new LinkedList[v];
        for (int i = 0; i < v; ++i)
            adj[i] = new LinkedList<>();
    }

    public void addEdge(int v, int w) {
        adj[v].add(w);
    }

    public List<Integer> traverse(int s) {
        boolean[] visited = new boolean[V];

        LinkedList<Integer> queue = new LinkedList<>();

        visited[s] = true;
        queue.add(s);
        List<Integer> output = new ArrayList<>();

        while (queue.size() != 0) {
            s = queue.poll();
            output.add(s);

            for (int n : adj[s]) {
                //                System.out.println(n);
                if (!visited[n]) {
                    visited[n] = true;
                    queue.add(n);
                }
            }
        }
        return output;
    }
}