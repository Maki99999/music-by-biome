package io.github.maki99999.biomebeats.gui.musiclist;

import io.github.maki99999.biomebeats.gui.BaseTextureUv;
import io.github.maki99999.biomebeats.gui.common.*;
import io.github.maki99999.biomebeats.gui.util.Point;
import io.github.maki99999.biomebeats.music.MusicGroup;
import io.github.maki99999.biomebeats.music.MusicTrack;
import io.github.maki99999.biomebeats.gui.util.BiomeBeatsColor;
import io.github.maki99999.biomebeats.gui.util.Rect;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

import static io.github.maki99999.biomebeats.gui.util.DrawUtils.drawScrollingString;

class MusicListEntryGroup extends TypedUiContainer<MusicListEntry> {
    static final int CHILDREN_HEIGHT = 16;
    private static final int GROUP_HEADER_HEIGHT = 16;

    final MusicList musicList;
    private final MusicGroup musicGroup;

    public MusicListEntryGroup(MusicList musicList, int x, int y, int w, Component message, MusicGroup musicGroup, boolean isCollapsed) {
        super(message, new Rect(x, y, w, 0));
        this.musicList = musicList;
        this.musicGroup = musicGroup;
        TwoStateImageButton
                collapseButton = addChild(new TwoStateImageButton(Component.translatable("menu.biomebeats.expand_collapse"), Component.translatable("menu.biomebeats.expand_collapse"), w - 24, 1,
                                                                  new ImageButton(Component.translatable("menu.biomebeats.expand_collapse"), null,
                        w - 24, 1, BaseTextureUv.ACCORDION_OPEN_UV, null),
                                                                  new ImageButton(Component.translatable("menu.biomebeats.expand_collapse"), null,
                        w - 24, 1, BaseTextureUv.ACCORDION_CLOSE_UV, null),
                                                                  (btn, newValue) -> musicList.onGroupToggle.onGroupToggle(musicGroup.getName(), newValue),
                                                                  false
        ));
        collapseButton.setState(isCollapsed);

        for (MusicTrack musicTrack : musicGroup.getMusicTracks()) {
            addTypedChild(new MusicListEntry(this, musicTrack, new Rect(1, 0, w - 2, CHILDREN_HEIGHT)));
        }
        UpdateHeight();
    }

    public void UpdateHeight() {
        int childY = GROUP_HEADER_HEIGHT + MusicList.CHILDREN_SPACING;
        for (MusicListEntry group : getTypedChildren()) {
            group.setY(childY);
            childY += group.getHeight() + MusicList.CHILDREN_SPACING;
        }
        setHeight(getTypedChildren().stream().mapToInt(UiElement::getHeight).sum() + (getTypedChildren().size() + 1) * MusicList.CHILDREN_SPACING + GROUP_HEADER_HEIGHT);
        musicList.updateY();
    }

    @Override
    protected void render(@NotNull GuiGraphics guiGraphics, Point mousePos, float partialTicks) {
        drawScrollingString(guiGraphics, getMinecraft().font, Component.literal(musicGroup.getName()),
                new Rect(getX() + 16, getY() + 4, getWidth() - 48, 8),
                            BiomeBeatsColor.WHITE.getHex());
    }

    @Override
    public void setY(int y) {
        super.setY(y);
    }

    @Override
    public void setWidth(int width) {
        super.setWidth(width);

        getTypedChildren().forEach(entry -> entry.setWidth(width - 2));
    }

    public void setCheckedMusicTracks(Collection<? extends MusicTrack> musicTracks) {
        getTypedChildren().forEach(child -> child.setCheckedState(musicTracks.contains(child.getMusicTrack())));
    }
}
