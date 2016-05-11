package maze;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by sanjay on 22/04/16.
 */
public class Direction {
    static Direction NORTH = new Direction(0,1),SOUTH = new Direction(0,-1),EAST= new Direction(1,0),WEST = new Direction(-1,0);
    Direction(int x, int y) {
        this.x = x;
        this.y = y;
    }
    int x,y;
    public static List<Direction> values() {
        return Arrays.asList(NORTH,SOUTH,EAST,WEST);
    }
    @Override
    public String toString() {
        return "Direction{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Direction direction = (Direction) o;

        if (x != direction.x) return false;
        return y == direction.y;

    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }

    public Direction reversed() {
        return new Direction(x == 1? -1: x == -1?1:0,y == 1? -1: y == -1?1:0);
    }
    static Random random = new Random();
    public static Direction random() {
        return values().get(random.nextInt(values().size()));
    }
    public Direction ccw() {
        return new Direction(-y,x);
    }
    public Direction cw() {
        return new Direction(y,-x);
    }
}
