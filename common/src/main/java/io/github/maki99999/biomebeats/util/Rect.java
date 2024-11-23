package io.github.maki99999.biomebeats.util;

public record Rect(int x, int y, int w, int h) {
    public int x2() {return x + w;}

    public int y2() {return y + h;}

    public int x1() {return x;}

    public int y1() {return y;}

    public static Rect fromCoordinates(int x1, int y1, int x2, int y2) {
        return new Rect(x1, y1, x2 - x1, y2 - y1);
    }

    public boolean contains(int x, int y) {
        return x >= x1() && y >= y1() && x < x2() && y < y2();
    }
}
