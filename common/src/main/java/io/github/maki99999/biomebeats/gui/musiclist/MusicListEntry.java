package io.github.maki99999.biomebeats.gui.musiclist;

import io.github.maki99999.biomebeats.Constants;
import io.github.maki99999.biomebeats.gui.BaseTextureUv;
import io.github.maki99999.biomebeats.gui.common.*;
import io.github.maki99999.biomebeats.gui.util.Point;
import io.github.maki99999.biomebeats.music.MusicTrack;
import io.github.maki99999.biomebeats.music.PreviewListener;
import io.github.maki99999.biomebeats.gui.util.BiomeBeatsColor;
import io.github.maki99999.biomebeats.gui.util.Rect;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import static io.github.maki99999.biomebeats.gui.util.DrawUtils.drawScrollingString;

class MusicListEntry extends UiElement implements PreviewListener {
    private final MusicListEntryGroup musicListEntryGroup;
    private final MusicTrack musicTrack;
    private final TwoStateImageButton1 previewButton;
    private final ImageButton1 editButton;
    private final TwoStateImageButton1 checkbox;
    private final EditBoxWrapper volumeModifierEditBox;

    private boolean editing = false;

    public MusicListEntry(MusicListEntryGroup musicListEntryGroup, MusicTrack musicTrack, Rect bounds) {
        super(Component.literal(musicTrack.getName()), bounds);
        this.musicListEntryGroup = musicListEntryGroup;
        this.musicTrack = musicTrack;

        volumeModifierEditBox = addChild(new EditBoxWrapper(Component.translatable("menu.biomebeats.volume_multiplier"), new Rect(getWidth() - 60,
                MusicListEntryGroup.CHILDREN_HEIGHT, 60, MusicListEntryGroup.CHILDREN_HEIGHT)));
        volumeModifierEditBox.setHint(Component.literal("0"));
        volumeModifierEditBox.setResponder(this::onVolumeModifierChange);
        volumeModifierEditBox.setFilter(s -> s.matches("^\\d{0,2}\\.?\\d{0,4}$"));
        volumeModifierEditBox.setVisible(false);

        checkbox = addChild(new TwoStateImageButton1(Component.literal("Checkbox"), null, 0, 0,
                new LayeredImageButton1(Component.literal("Checkbox"), null, 0, 0,
                        BaseTextureUv.CHECKBOX_CHECKED_UV, null),
                new LayeredImageButton1(Component.literal("Checkbox"), null, 0, 0,
                        BaseTextureUv.BUTTON_BASE_INVERTED_UV, null),
                (c, newValue)
                        -> musicListEntryGroup.musicList.onMusicTrackToggle.onMusicTrackToggle(musicTrack, newValue), false));

        previewButton = addChild(new TwoStateImageButton1(Component.literal("Play / Stop"), null,
                getWidth() - BaseTextureUv.PLAY_UV.w(), 0,
                new LayeredImageButton1(Component.translatable("menu.biomebeats.stop"), Component.translatable("menu.biomebeats.stop"), getWidth() - BaseTextureUv.PLAY_UV.w(), 0,
                        BaseTextureUv.STOP_UV, null),
                new LayeredImageButton1(Component.translatable("menu.biomebeats.play"), Component.translatable("menu.biomebeats.play"), getWidth() - BaseTextureUv.PLAY_UV.w(), 0,
                        BaseTextureUv.PLAY_UV, null), (btn, newValue) -> {
            if (newValue) {
                Constants.MUSIC_MANAGER.playPreviewTrack(musicTrack);
            } else {
                Constants.MUSIC_MANAGER.stopPreviewTrack();
            }

        }, false));
        editButton = addChild(new LayeredImageButton1(Component.translatable("menu.biomebeats.edit"), Component.translatable("menu.biomebeats.edit"),
                getWidth() - previewButton.getWidth() - BaseTextureUv.EDIT_UV.w(), 0,
                BaseTextureUv.EDIT_UV, btn -> onEdit()));

        Constants.MUSIC_MANAGER.addPreviewListener(this);
    }

    public boolean isEditing() {
        return editing;
    }

    public void setEditing(boolean editing) {
        this.editing = editing;
        volumeModifierEditBox.setVisible(editing);
        setHeight(MusicListEntryGroup.CHILDREN_HEIGHT * (editing ? 2 : 1));
    }

    private void onVolumeModifierChange(String s) {
        double volumeMultiplier;
        try {
            volumeMultiplier = Double.parseDouble(s);
        } catch (NumberFormatException e) {
            volumeMultiplier = 1;
        }

        musicTrack.setVolumeMultiplier(volumeMultiplier);
    }

    private void onEdit() {
        for (MusicListEntry sibling : musicListEntryGroup.getTypedChildren()) {
            sibling.setEditing(sibling == this && !isEditing());
        }
        musicListEntryGroup.UpdateHeight();
    }

    @Override
    public void onClose() {
        Constants.MUSIC_MANAGER.removePreviewListener(this);
    }

    @Override
    protected void render(@NotNull GuiGraphics guiGraphics, Point mousePos, float partialTicks) {
        guiGraphics.fill(getX(), getY(), getX() + getWidth(), getY() + MusicListEntryGroup.CHILDREN_HEIGHT,
                BiomeBeatsColor.LIGHT_GREY.getHex());

        drawScrollingString(guiGraphics, getMinecraft().font, getName(), getTextRect(),
                (int) -musicListEntryGroup.musicList.getScrollAmount(), BiomeBeatsColor.WHITE.getHex());

        if (isEditing()) {
            renderAddon(guiGraphics);
        }
    }

    private @NotNull Rect getTextRect() {
        return new Rect(getX() + 18, getY(), editButton.getX() - (getX() + 22),
                MusicListEntryGroup.CHILDREN_HEIGHT);
    }

    private void renderAddon(GuiGraphics guiGraphics) {
        guiGraphics.fill(getX(), getY() + MusicListEntryGroup.CHILDREN_HEIGHT, getX() + getWidth(), getY() + getHeight(), BiomeBeatsColor.LIGHT_GREY.getHex());

        Rect symbolBounds = Rect.fromCoordinates(getX(), getY() + MusicListEntryGroup.CHILDREN_HEIGHT, getX() + getMinecraft().font.width("└"), getY() + getHeight());
        drawScrollingString(guiGraphics, getMinecraft().font, Component.literal("└"), symbolBounds, (int) -musicListEntryGroup.musicList.getScrollAmount(), BiomeBeatsColor.WHITE.getHex());
        Rect textRect = Rect.fromCoordinates(symbolBounds.x2() + 1, getY() + MusicListEntryGroup.CHILDREN_HEIGHT, volumeModifierEditBox.getX() - 2, getY() + getHeight());
        drawScrollingString(guiGraphics, getMinecraft().font, Component.translatable("menu.biomebeats.volume_multiplier"), textRect, (int) -musicListEntryGroup.musicList.getScrollAmount(), BiomeBeatsColor.WHITE.getHex());
    }

    public MusicTrack getMusicTrack() {
        return musicTrack;
    }

    public void setCheckedState(boolean newValue) {
        checkbox.setState(newValue);
    }

    @Override
    public void onPreviewChanged(MusicTrack previewTrack) {
        previewButton.setState(previewTrack == musicTrack);
    }

    @Override
    public void renderTooltips(GuiGraphics guiGraphics, Point mousePos, Point absolutePos) {
        super.renderTooltips(guiGraphics, mousePos, absolutePos);
        if(getTextRect().contains(mousePos)) {
            guiGraphics.renderTooltip(getMinecraft().font, Component.literal(musicTrack.getPathName()),
                    absolutePos.x(), absolutePos.y());
        }
    }

    @Override
    public void setWidth(int width) {
        super.setWidth(width);
        previewButton.setX(getWidth() - BaseTextureUv.PLAY_UV.w());
        editButton.setX(getWidth() - previewButton.getWidth() - BaseTextureUv.EDIT_UV.w());
    }

    @Override
    public void setY(int y) {
        super.setY(y);
    }
}
