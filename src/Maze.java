/**
 * Missions 1 and 2. Building the maze out of the walls.
 * A wall k joins the cells w.a()[k] and w.b()[k] and costs w.cost()[k]. Indexes start from 0,
 * the line number in walls.txt is the index + 1. The cells are connected by DisjointSet.
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

public class Maze {

    /**
     * Mission 1. Take the walls in the order of the file. Break a wall when its two cells are in different sets.
     * Returns the indexes of the broken walls, in the order of breaking.
     */
    public static int[] breakWalls(Input.Walls w) {
        int m = w.cost().length;
        int n = w.cells();

        DisjointSet dsu = new DisjointSet(n);
        List<Integer> broken = new ArrayList<>();

        for (int k = 0; k < m; k++) {
            int u = w.a()[k];
            int v = w.b()[k];
            if (dsu.union(u, v)) {
                broken.add(k);
            }
        }

        int[] result = new int[broken.size()];
        for (int i = 0; i < broken.size(); i++) {
            result[i] = broken.get(i);
        }
        return result;
    }

    /**
     * Mission 2. Kruskal: the same, but take the walls from the cheapest to the most expensive.
     * Returns the indexes of the broken walls.
     */
    public static int[] cheapestMaze(Input.Walls w) {
        int m = w.cost().length;
        int n = w.cells();

        Integer[] indices = new Integer[m];
        for (int i = 0; i < m; i++) {
            indices[i] = i;
        }

        int[] costs = w.cost();
        Arrays.sort(indices, (i1, i2) -> Integer.compare(costs[i1], costs[i2]));

        DisjointSet dsu = new DisjointSet(n);
        List<Integer> broken = new ArrayList<>();

        for (int k : indices) {
            int u = w.a()[k];
            int v = w.b()[k];
            if (dsu.union(u, v)) {
                broken.add(k);
            }
        }

        int[] result = new int[broken.size()];
        for (int i = 0; i < broken.size(); i++) {
            result[i] = broken.get(i);
        }
        return result;
    }

    /**
     * Mission 2. Prim with a priority queue. Returns the total cost of the cheapest maze.
     * It must be equal to the total cost of cheapestMaze.
     */
    public static long primCost(Input.Walls w) {
        int m = w.cost().length;
        int n = w.cells();

        @SuppressWarnings("unchecked")
        List<Edge>[] adj = new ArrayList[n];
        for (int i = 0; i < n; i++) {
            adj[i] = new ArrayList<>();
        }

        int[] a = w.a();
        int[] b = w.b();
        int[] cost = w.cost();

        for (int k = 0; k < m; k++) {
            adj[a[k]].add(new Edge(b[k], cost[k]));
            adj[b[k]].add(new Edge(a[k], cost[k]));
        }

        boolean[] visited = new boolean[n];
        PriorityQueue<Edge> pq = new PriorityQueue<>((e1, e2) -> Integer.compare(e1.cost, e2.cost));

        visited[0] = true;
        pq.addAll(adj[0]);

        long totalCost = 0;
        int edgesCount = 0;

        while (!pq.isEmpty() && edgesCount < n - 1) {
            Edge edge = pq.poll();
            if (visited[edge.to]) {
                continue;
            }
            visited[edge.to] = true;
            totalCost += edge.cost;
            edgesCount++;

            for (Edge next : adj[edge.to]) {
                if (!visited[next.to]) {
                    pq.add(next);
                }
            }
        }

        return totalCost;
    }

    private static class Edge {
        int to;
        int cost;

        public Edge(int to, int cost) {
            this.to = to;
            this.cost = cost;
        }
    }
}