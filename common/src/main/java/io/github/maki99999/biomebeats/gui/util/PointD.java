package io.github.maki99999.biomebeats.gui.util;

public record PointD(double x, double y) {
    public Point toIntPoint() {
        return new Point((int) x, (int) y);
    }

    public PointD translate(double dx, double dy) {
        return new PointD(x + dx, y + dy);
    }
}