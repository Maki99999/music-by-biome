package io.github.maki99999.biomebeats.util;

public final class GridLayout {
    private final int startX;
    private final int startY;
    private final int cellWidth;
    private final int cellHeight;
    private final int spacing;

    public GridLayout(Rect bounds, int rows, int columns, int spacing) {
        this.startX = bounds.x() + spacing;
        this.startY = bounds.y() + spacing;
        this.cellWidth = (bounds.w() - (spacing * (columns + 1))) / columns;
        this.cellHeight = (bounds.h() - (spacing * (rows + 1))) / rows;
        this.spacing = spacing;
    }

    public Rect getCell(int row, int col) {
        int x = startX + col * (cellWidth + spacing);
        int y = startY + row * (cellHeight + spacing);
        return new Rect(x, y, cellWidth, cellHeight);
    }
}
