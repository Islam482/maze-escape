/**
 * Missions 4 and 5. The slides: a directed graph. Cells are 0 .. n-1, slide i goes from s.from()[i] to s.to()[i]
 * and takes s.time()[i]. There are no slides from a cell to itself, and the exit can always be reached from the start.
 *
 * Ready for you: Graphs.slidesFrom(s)[u] is the list of the slides that leave cell u, and Graphs.slidesInto(s)[v] is the list
 * of the slides that enter cell v (reversed graph). The lists hold slide numbers i; the cell at the other end is s.to()[i]
 * or s.from()[i].
 */

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

public class Zones {

    /**
     * Mission 4. Kosaraju. Returns zoneOf: zoneOf[cell] = number of the zone, numbers 0 .. zones-1.
     * Number the zones in the order in which the second pass finds them. That order is a topological order
     * of the zone graph: every slide between two zones goes from a smaller number to a larger number.
     */
    public static int[] kosaraju(Input.Slides s) {
        int n = s.n();
        int[][] slidesFrom = Graphs.slidesFrom(s);
        int[][] slidesInto = Graphs.slidesInto(s);
        int[] to = s.to();
        int[] from = s.from();

        boolean[] visited = new boolean[n];
        Deque<Integer> finishOrder = new ArrayDeque<>();

        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                Deque<Object[]> stack = new ArrayDeque<>();
                stack.push(new Object[]{i, 0});
                visited[i] = true;

                while (!stack.isEmpty()) {
                    Object[] top = stack.peek();
                    int u = (int) top[0];
                    int edgeIdx = (int) top[1];

                    int[] outSlides = slidesFrom[u];
                    if (edgeIdx < outSlides.length) {
                        top[1] = edgeIdx + 1;
                        int slideId = outSlides[edgeIdx];
                        int v = to[slideId];
                        if (!visited[v]) {
                            visited[v] = true;
                            stack.push(new Object[]{v, 0});
                        }
                    } else {
                        stack.pop();
                        finishOrder.push(u);
                    }
                }
            }
        }

        int[] zoneOf = new int[n];
        Arrays.fill(zoneOf, -1);
        int zoneCount = 0;

        for (int u : finishOrder) {
            if (zoneOf[u] == -1) {
                Deque<Integer> compStack = new ArrayDeque<>();
                compStack.push(u);
                zoneOf[u] = zoneCount;

                while (!compStack.isEmpty()) {
                    int curr = compStack.pop();
                    for (int slideId : slidesInto[curr]) {
                        int prev = from[slideId];
                        if (zoneOf[prev] == -1) {
                            zoneOf[prev] = zoneCount;
                            compStack.push(prev);
                        }
                    }
                }
                zoneCount++;
            }
        }

        return zoneOf;
    }

    /**
     * Mission 5. The graph of zones: the edge X to Y costs the cheapest slide from X to Y, walking inside a zone is free.
     * Go through the zones in the order of their numbers (no priority queue). Returns two numbers:
     * [0] flow stages: the number of zones on the longest chain of zones;
     * [1] the cost of the cheapest route from the zone of cell 0 to the zone of the last cell.
     */
    public static long[] routes(Input.Slides s, int[] zoneOf, int zoneCount) {
        int n = s.n();
        int[] from = s.from();
        int[] to = s.to();
        int[] time = s.time();
        int numSlides = from.length;

        class ZoneEdge {
            int targetZone;
            int cost;
            ZoneEdge(int targetZone, int cost) {
                this.targetZone = targetZone;
                this.cost = cost;
            }
        }

        int[][] minCostTo = new int[zoneCount][zoneCount];
        for (int i = 0; i < zoneCount; i++) {
            Arrays.fill(minCostTo[i], -1);
        }

        for (int i = 0; i < numSlides; i++) {
            int u = from[i];
            int v = to[i];
            int zU = zoneOf[u];
            int zV = zoneOf[v];
            if (zU != zV) {
                int c = time[i];
                if (minCostTo[zU][zV] == -1 || c < minCostTo[zU][zV]) {
                    minCostTo[zU][zV] = c;
                }
            }
        }

        @SuppressWarnings("unchecked")
        List<ZoneEdge>[] zoneEdges = new ArrayList[zoneCount];
        for (int i = 0; i < zoneCount; i++) {
            zoneEdges[i] = new ArrayList<>();
        }

        for (int uZ = 0; uZ < zoneCount; uZ++) {
            for (int vZ = 0; vZ < zoneCount; vZ++) {
                if (minCostTo[uZ][vZ] != -1) {
                    zoneEdges[uZ].add(new ZoneEdge(vZ, minCostTo[uZ][vZ]));
                }
            }
        }

        int startZone = zoneOf[0];
        int endZone = zoneOf[n - 1];

        long[] longestChain = new long[zoneCount];
        long[] minCost = new long[zoneCount];
        Arrays.fill(minCost, Long.MAX_VALUE / 2);
        Arrays.fill(longestChain, -1);

        longestChain[startZone] = 1;
        minCost[startZone] = 0;

        for (int uZ = startZone; uZ < zoneCount; uZ++) {
            if (longestChain[uZ] != -1) {
                for (ZoneEdge edge : zoneEdges[uZ]) {
                    int vZ = edge.targetZone;
                    int cost = edge.cost;

                    if (longestChain[uZ] + 1 > longestChain[vZ]) {
                        longestChain[vZ] = longestChain[uZ] + 1;
                    }

                    if (minCost[uZ] + cost < minCost[vZ]) {
                        minCost[vZ] = minCost[uZ] + cost;
                    }
                }
            }
        }

        long flowStages = longestChain[endZone];
        long fastestCost = minCost[endZone];

        return new long[]{flowStages, fastestCost};
    }
}