package maze;

import java.util.HashSet;
import java.util.Stack;

/**
 * Created by sanjay on 22/04/16.
 */
public class Maze {
    static int width = 5;
    static int height = 5;
    public static Maze instance;
    HashSet<Tile> tiles = new HashSet<>();
    public Maze(int width, int height) {
        Maze.width = width;
        Maze.height = height;
        instance = this;
        tiles.clear();
        Stack<Tile> stack = new Stack<>();
        Tile t = new Tile(width-1,height-1);
        t.closed.remove(Direction.NORTH);
        stack.push(t);
        tiles.add(t);
        while (!stack.isEmpty()) {
            t = stack.peek();
            while (t.randomWall() == null) {
                if (stack.isEmpty()) break;
                stack.pop();
                if (stack.isEmpty()) break;
                t = stack.peek();
            }
            if (stack.isEmpty()) break;
            t = t.randomWall();
            tiles.add(t);
            stack.push(t);
        }
        tiles.forEach(Tile::draw);
    }
}
