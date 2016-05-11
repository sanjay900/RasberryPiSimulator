package maze;

import main.Main;
import simbad.sim.Wall;

import javax.vecmath.Vector3d;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Created by sanjay on 22/04/16.
 */
public class Tile {
    int x,y;
    Random r = new Random();
    HashSet<Direction> closed = new HashSet<>(Direction.values());

    public Tile(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public Tile randomWall() {
        List<Direction> tiles = Direction.values().stream().filter(c -> this.getRelative(c).inBounds()).collect(Collectors.toList());
        if (tiles.isEmpty()) return null;
        Direction d = tiles.get(r.nextInt(tiles.size()));
        Tile t = getRelative(d);
        closed.remove(d);
        t.closed.remove(d.reversed());
        return t;
    }
    public boolean inBounds() {
        return x > -1 && x < Maze.width && y > -1 && y < Maze.height && !Maze.instance.tiles.contains(this);
    }
    public boolean inBoundsReal() {
        return x > -1 && x < Maze.width && y > -1 && y < Maze.height;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }

    public Tile getRelative(Direction dir) {
        return new Tile(x+dir.x,y+dir.y);
    }
    public boolean equals(Object other) {
        return other instanceof Tile && ((Tile) other).x == x && ((Tile) other).y == y;
    }
    final float size = 2f/Maze.width;
    public void draw() {
        float x = this.x*size;
        float y = this.y*size;
        Wall w;
        for (Direction dir : closed) {
            if (dir.x == -1 && dir.y == 0) {
                w = new Wall(new Vector3d(y+0.3f*size,0,x),size,1,0.3f*size,Main.arena);
            } else if (dir.x == 1 && dir.y == 0) {
                w = new Wall(new Vector3d(y+0.3f*size,0,x+size),size,1,0.3f*size,Main.arena);
            } else if (dir.x == 0 && dir.y == -1) {
                w = new Wall(new Vector3d(y,0,x+0.3f*size),0.3f*size,1,size,Main.arena);
            } else {
                w = new Wall(new Vector3d(y+size,0,x+0.3f*size+0.3f*size),0.3f*size,1,size+0.3f*size,Main.arena);
            }
            //Shift quadrant
            w.translateTo(new Vector3d(-2,0,0));
            Main.arena.add(w);
        }

    }
    @Override
    public String toString() {
        return "Tile{" +
                "y=" + y +
                ", x=" + x +
                '}';
    }
}
