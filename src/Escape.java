/**
 * Mission 3. Escape: DFS from cell 0 to the last cell (rows * cols - 1) in the maze of Mission 1.
 * Two neighbouring cells are connected when the wall between them is broken.
 * The neighbours are tried in this order: up, right, down, left (cell - cols, cell + 1, cell + cols, cell - 1).
 * The ready method Graphs.open(w, broken) gives you the open neighbours of every cell, in no particular order:
 * to visit them in the required order, go through the four cells above one by one and take those that are in the list.
 * The program must not overflow the stack on a maze of 1000 by 1000 cells.
 */

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

public class Escape {

    /**
     * path: the cells of the way out, from 0 to the last cell.
     * visited: how many cells are marked as visited at the moment the exit is marked (the start and the exit are counted).
     * deadEnds: how many cells of the maze have exactly one open neighbour.
     */
    public record Result(int[] path, int visited, int deadEnds) {}

    public static Result escape(Input.Walls w, int[] broken) {
        int n = w.cells();
        int c = w.cols();
        int exitCell = n - 1;
        int[][] openList = Graphs.open(w, broken);

        boolean[][] isOpen = new boolean[n][4];
        for (int i = 0; i < n; i++) {
            if (openList[i] != null) {
                for (int nb : openList[i]) {
                    if (nb == i - c) isOpen[i][0] = true;
                    else if (nb == i + 1) isOpen[i][1] = true;
                    else if (nb == i + c) isOpen[i][2] = true;
                    else if (nb == i - 1) isOpen[i][3] = true;
                }
            }
        }

        int deadEnds = 0;
        for (int i = 0; i < n; i++) {
            int openCount = 0;
            for (int d = 0; d < 4; d++) {
                if (isOpen[i][d]) openCount++;
            }
            if (openCount == 1) {
                deadEnds++;
            }
        }

        boolean[] visitedArr = new boolean[n];
        int[] parent = new int[n];
        for (int i = 0; i < n; i++) parent[i] = -1;

        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(0);
        int visitedCount = 0;

        while (!stack.isEmpty()) {
            int curr = stack.pop();

            if (!visitedArr[curr]) {
                visitedArr[curr] = true;
                visitedCount++;
            }

            if (curr == exitCell) {
                break;
            }

            int[] cand = {curr - c, curr + 1, curr + c, curr - 1};
            for (int d = 3; d >= 0; d--) {
                if (isOpen[curr][d]) {
                    int next = cand[d];
                    if (d == 1 && curr % c == c - 1) continue;
                    if (d == 3 && curr % c == 0) continue;
                    if (next >= 0 && next < n && !visitedArr[next]) {
                        parent[next] = curr;
                        stack.push(next);
                    }
                }
            }
        }

        List<Integer> pathList = new ArrayList<>();
        int curr = exitCell;
        while (curr != -1) {
            pathList.add(curr);
            curr = parent[curr];
        }
        Collections.reverse(pathList);

        int[] path = new int[pathList.size()];
        for (int i = 0; i < pathList.size(); i++) {
            path[i] = pathList.get(i);
        }

        return new Result(path, visitedCount, deadEnds);
    }
}