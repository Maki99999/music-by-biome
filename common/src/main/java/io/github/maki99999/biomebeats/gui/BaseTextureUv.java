package io.github.maki99999.biomebeats.gui;

import io.github.maki99999.biomebeats.Constants;
import io.github.maki99999.biomebeats.util.Rect;
import net.minecraft.resources.ResourceLocation;

public class BaseTextureUv {
    public static final ResourceLocation RL = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/base.png");

    public static final Rect CONTAINER_UV_TL = new Rect(0, 0, 4, 4);
    public static final Rect CONTAINER_UV_TR = new Rect(8, 0, 4, 4);
    public static final Rect CONTAINER_UV_BL = new Rect(0, 8, 4, 4);
    public static final Rect CONTAINER_UV_BR = new Rect(8, 8, 4, 4);
    public static final Rect CONTAINER_UV_T = new Rect(4, 0, 4, 4);
    public static final Rect CONTAINER_UV_B = new Rect(4, 8, 4, 4);
    public static final Rect CONTAINER_UV_L = new Rect(0, 4, 4, 4);
    public static final Rect CONTAINER_UV_R = new Rect(8, 4, 4, 4);
    public static final Rect CONTAINER_UV_C = new Rect(4, 4, 4, 4);

    public static final Rect BUTTON_BASE_UV = new Rect(0, 16, 16, 16);
    public static final Rect BUTTON_BASE_FOCUSED_UV = new Rect(18, 16, 16, 16);
    public static final Rect BUTTON_BASE_DISABLED_UV = new Rect(36, 16, 16, 16);
    public static final Rect BUTTON_BASE_INVERTED_UV = new Rect(54, 16, 16, 16);

    public static final Rect TAB_LEFT_ACTIVE_UV = new Rect(0, 36, 60, 20);
    public static final Rect TAB_LEFT_INACTIVE_UV = new Rect(62, 36, 60, 20);

    public static final Rect DELETE_UV = new Rect(0, 60, 16, 16);
    public static final Rect EDIT_UV = new Rect(18, 60, 16, 16);
    public static final Rect PLAY_UV = new Rect(36, 60, 16, 16);
    public static final Rect CHECKBOX_CHECKED_UV = new Rect(54, 60, 16, 16);
    public static final Rect STOP_UV = new Rect(72, 60, 16, 16);
    public static final Rect FOLDER_UV = new Rect(90, 60, 16, 16);
    public static final Rect RELOAD_UV = new Rect(108, 60, 16, 16);
    public static final Rect ACCORDION_OPEN_UV = new Rect(126, 60, 16, 16);
    public static final Rect ACCORDION_CLOSE_UV = new Rect(144, 60, 16, 16);
}
