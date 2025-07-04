package io.github.maki99999.biomebeats.gui.util;

public enum BiomeBeatsColor {
    BLACK(0xFF000000),
    DARKER_GREY(0xFF373737),
    DARK_GREY(0xFF555555),
    LIGHT_GREY(0xFF8B8B8B),
    LIGHTER_GREY(0xFFC6C6C6),
    WHITE(0xFFFFFFFF),
    BLUE(0xFF4A90E2),
    RED(0xFFFF0000),
    GREEN(0xFF00FF00),
    GREEN_50(0x7F00FF00),
    TRANSPARENT(0x00000000);

    private final int hex;

    BiomeBeatsColor(int hex) {
        this.hex = hex;
    }

    public int getHex() {
        return hex;
    }
}
