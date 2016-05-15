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
        Tile random;
        //We push each tile on, but as we go, the current tile will slowly branch out but then track backwards
        //The stack is empty once we reach the last tile
        while (!stack.isEmpty()) {
            t = stack.peek();
            //If randomwall returns null, pop a new tile from the stack, this will backtrack to the last tile
            //And then check all its directions until we find a tile with free directions
            //If the stack is empty we have backtracked to the start.
            while ((random = t.randomWall()) == null) {
                if (stack.isEmpty()) break;
                stack.pop();
                if (stack.isEmpty()) break;
                t = stack.peek();
            }
            if (stack.isEmpty()) break;
            t = random;
            tiles.add(t);
            stack.push(t);
        }
        tiles.forEach(Tile::draw);
    }
}
