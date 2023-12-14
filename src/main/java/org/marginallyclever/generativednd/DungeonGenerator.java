package org.marginallyclever.generativednd;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Generates a random dungeon map.
 */
public class DungeonGenerator {
    static class Tile implements Comparable<Tile> {
        public int x,y;

        public Tile(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Tile && ((Tile)obj).x==x && ((Tile)obj).y==y;
        }

        @Override
        public int compareTo(Tile b) {
            int scoreA = x*x + y*y;
            int scoreB = b.x*b.x + b.y*b.y;
            return scoreB-scoreA;
        }
    }

    private final List<Tile> tiles = new ArrayList<>();
    List<Tile> walls = new ArrayList<>();

    public DungeonGenerator(int count) {
        tiles.add(new Tile(0,0));
        addRooms(count);
        drawMap();
    }

    private void drawMap() {
        Rectangle bounds = new Rectangle();
        for(Tile r : tiles) bounds.add(r.x,r.y);
        for(Tile r : walls) bounds.add(r.x,r.y);
        int w = bounds.width+1;
        int h = bounds.height+1;
        // fill in the map
        char [][] map = new char[h][w];
        // initialize to invisible
        for(int y=0;y<h;++y) {
            for(int x=0;x<w;++x) {
                map[y][x] = ' ';
            }
        }
        // new rooms must be outside old rooms, thus they qualify as walls.
        for(Tile r : walls) {
            int y = r.y - bounds.y;
            int x = r.x - bounds.x;
            map[y][x] = '#';
        }
        // rooms are open spaces
        for(Tile r : tiles) {
            int y = r.y - bounds.y;
            int x = r.x - bounds.x;
            map[y][x] = '.';
        }

        // print the map
        for(int y=0;y<h;++y) {
            System.out.println(map[y]);
        }
        System.out.println("Rooms: "+ tiles.size());
        System.out.println("Walls: "+ walls.size());
    }

    private void addRooms(int count) {
        walls.add(new Tile(-1,0));
        walls.add(new Tile( 1,0));
        walls.add(new Tile(0,-1));
        walls.add(new Tile(0,1));

        for(int i=0;i<count-1;++i) {
            // by sorting rooms by furthest away first and squaring the random value (always <1)
            // we're more likely to pick rooms that are further away, which creates more interesting shapes.
            // reverse the sort order to make smoother, rounder rooms.
            Collections.sort(walls);
            double random = Math.random();
            int index = (int)((random*random)* walls.size());
            Tile r = walls.remove(index);
            tiles.add(r);
            int x = r.x;
            int y = r.y;
            maybeAdd(x-1,y  );
            maybeAdd(x+1,y  );
            maybeAdd(x  ,y-1);
            maybeAdd(x  ,y+1);
        }

        walls.removeAll(tiles);
    }

    private void maybeAdd(int x, int y) {
        if(tiles.contains(new Tile(x,y))) return;
        if(walls.contains(new Tile(x,y))) return;
        walls.add(new Tile(x,y));
    }

    public static void main(String[] args) {
        new DungeonGenerator(250);  // <-- change number of rooms here.
    }
}