package tanks;

import jgl.math.Vec2;
import jgl.maze.Maze;
import tanks.entity.Boundary;
import tanks.world.IWorld;

/**
 * Created by william on 10/31/16.
 */
public class TanksUtil {

    public static void fillMaze(IWorld world, int originX, int originY, int cellWidth, int cellHeight, int width, int height, int boundarySize) {
        int[][] maze = Maze.generateKruskal(width, height);

        System.out.print(" ");
        for (int i = 0; i < width * 2 - 1; i++) {
            System.out.print("_");
        }
        System.out.println();

        for (int y = 0; y < height; y++) {
            System.out.print("|");

            for (int x = 0; x < width; x++) {
                int cell = maze[x][y];

                if (cell == 0)
                    System.out.print("X");

                System.out.print(Maze.isOpenSouth(cell) ? " " : "_");

                if (Maze.isOpenEast(cell)) {
                    System.out.print(Maze.isOpenSouth(cell | maze[x + 1][y]) ? " " : "_");
                } else {
                    System.out.print("|");
                }

            }
            System.out.println();
        }

        for (int y = 0; y < height; y++) {
            int yVal = originY + cellHeight * y;
            world.add(new Boundary.Builder(
                    new Vec2(0, yVal + cellHeight / 2.0),
                    new Vec2(boundarySize, cellHeight + boundarySize / 2.0)
            ));

            for (int x = 0; x < width; x++) {
                int xVal = originX + cellWidth * x;
                if (y == 0) {
                    world.add(new Boundary.Builder(
                            new Vec2(xVal + cellWidth / 2.0, 0),
                            new Vec2(cellWidth + boundarySize / 2.0, boundarySize)
                    ));
                }


                if (!Maze.isOpenEast(maze[x][y])) {
                    world.add(new Boundary.Builder(
                            new Vec2(xVal + cellWidth, yVal + cellHeight / 2.0),
                            new Vec2(boundarySize, cellHeight + boundarySize / 2.0)
                    ));
                }
                if (!Maze.isOpenSouth(maze[x][y])) {
                    world.add(new Boundary.Builder(
                            new Vec2(xVal + cellWidth / 2.0, yVal + cellHeight),
                            new Vec2(cellWidth + boundarySize / 2.0, boundarySize)
                    ));
                }
            }
        }

    }

}
