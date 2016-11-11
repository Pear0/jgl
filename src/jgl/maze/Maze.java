package jgl.maze;

import java.util.*;

/**
 * Created by william on 10/27/16.
 */
public class Maze {

    private static class Edge {

        public final int x;
        public final int y;
        public final int direction;

        public Edge(int x, int y, int direction) {
            this.x = x;
            this.y = y;
            this.direction = direction;
        }
    }

    private static class Sentinel {

        private Sentinel parent;

        public Sentinel root() {
            return parent != null ? parent.root() : this;
        }

        public boolean isConnected(Sentinel other) {
            return root() == other.root();
        }

        public void connect(Sentinel other) {
            other.root().parent = this;
        }

    }

    public static final int N = 1;
    public static final int S = 2;
    public static final int E = 4;
    public static final int W = 8;

    public static boolean isOpenNorth(int n) {
        return (n & N) != 0;
    }

    public static boolean isOpenSouth(int n) {
        return (n & S) != 0;
    }

    public static boolean isOpenEast(int n) {
        return (n & E) != 0;
    }

    public static boolean isOpenWest(int n) {
        return (n & W) != 0;
    }

    private static int dX(int n) {
        if (n == E) return 1;
        if (n == W) return -1;
        return 0;
    }

    private static int dY(int n) {
        if (n == N) return -1;
        if (n == S) return 1;
        return 0;
    }

    private static int opposite(int n) {
        if (n == N) return S;
        if (n == S) return N;
        if (n == E) return W;
        if (n == W) return E;
        throw new IllegalArgumentException("Unknown integer: " + n);
    }

    public static int[][] generateKruskal(int width, int height) {
        int[][] grid = new int[width][height];
        Sentinel[][] sentinels = new Sentinel[width][height];

        //Over allocate by a small amount
        List<Edge> edges = new ArrayList<>(width * height * 2);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                sentinels[x][y] = new Sentinel();
                if (y > 0) edges.add(new Edge(x, y, N));
                if (x > 0) edges.add(new Edge(x, y, W));
            }
        }

        Collections.shuffle(edges);

        for (Edge edge : edges) {
            int nx = edge.x + dX(edge.direction);
            int ny = edge.y + dY(edge.direction);

            if (!sentinels[edge.x][edge.y].isConnected(sentinels[nx][ny])) {
                sentinels[edge.x][edge.y].connect(sentinels[nx][ny]);
                grid[edge.x][edge.y] |= edge.direction;
                grid[nx][ny] |= opposite(edge.direction);

            }
        }

        Set<Object> sen = new HashSet<>();
        for (Sentinel[] sens : sentinels) {
            for (Sentinel s : sens) {
                sen.add(s.root());

            }
        }
        sen.forEach(System.out::println);

        return grid;
    }

}
