package io.github.maki99999.biomebeats.gui.musiclist;

import io.github.maki99999.biomebeats.gui.common.ScrollContainer;
import io.github.maki99999.biomebeats.gui.common.UiElement;
import io.github.maki99999.biomebeats.gui.util.Point;
import io.github.maki99999.biomebeats.gui.util.PointD;
import io.github.maki99999.biomebeats.music.MusicGroup;
import io.github.maki99999.biomebeats.music.MusicTrack;
import io.github.maki99999.biomebeats.gui.util.BiomeBeatsColor;
import io.github.maki99999.biomebeats.gui.util.Rect;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class MusicList extends ScrollContainer {
    static final int CHILDREN_SPACING = 4;

    private final List<MusicListEntryGroup> entryGroups = new ArrayList<>();
    final OnMusicTrackToggle onMusicTrackToggle;
    final OnGroupToggle onGroupToggle;
    private final List<MusicGroup> sortedMusicGroups;

    public MusicList(Rect bounds, Component message, Collection<MusicGroup> musicGroups,
                     OnMusicTrackToggle onMusicTrackToggle, OnGroupToggle onGroupToggle) {
        super(message, bounds);

        this.onMusicTrackToggle = onMusicTrackToggle;
        this.sortedMusicGroups = sortMusicGroups(musicGroups);
        this.onGroupToggle = onGroupToggle;

        sortAndFilterMusicTracks("", List.of(), List.of());
        setVisible(false);
    }

    @Override
    protected int getContentHeight() {
        int height = -2;
        for (MusicListEntryGroup entryGroup : entryGroups) {
            height += entryGroup.getHeight() + 5;
        }
        return height;
    }

    @Override
    protected void renderBackground(GuiGraphics guiGraphics) {
        guiGraphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), BiomeBeatsColor.DARK_GREY.getHex());
    }

    @Override
    protected void renderContent(@NotNull GuiGraphics guiGraphics, Point mousePos, float partialTicks) {
        if (isScrollbarVisible()) {
            guiGraphics.fill(getX() + getWidth(), getY(), getX() + getWidth(), getY() + getHeight(),
                    BiomeBeatsColor.DARK_GREY.getHex());
        }
        int height = 3;
        for (int i = 0; i < entryGroups.size(); i++) {
            MusicListEntryGroup entryGroup = entryGroups.get(i);
            entryGroup.renderAll(guiGraphics, mousePos, partialTicks);
            height += entryGroup.getHeight();
            if (i != entryGroups.size() - 1) {
                guiGraphics.fill(0, height, getWidth(), height + 1, BiomeBeatsColor.BLACK.getHex());
                height += 5;
            }
        }
    }

    public void setCheckedMusicTracks(Collection<? extends MusicTrack> musicTracks) {
        for (var child : entryGroups) {
            child.setCheckedMusicTracks(musicTracks);
        }
    }

    private List<MusicGroup> sortMusicGroups(Collection<MusicGroup> musicGroups) {
        return musicGroups.stream()
                .sorted((m1, m2) -> {
                    if (m1.getName().contains("Custom")) return -1;
                    if (m2.getName().contains("Custom")) return 1;

                    if (m1.getName().contains("Minecraft") != m2.getName().contains("Minecraft")) {
                        if (m1.getName().contains("Minecraft")) return -1;
                        if (m2.getName().contains("Minecraft")) return 1;
                    }

                    return m1.getName().compareTo(m2.getName());
                })
                .toList();
    }

    public void sortAndFilterMusicTracks(String filter, Collection<? extends MusicTrack> checkedMusicTracks,
                                         Collection<String> collapsedMusicGroups) {
        List<MusicGroup> sortedMusic = new ArrayList<>();

        for (MusicGroup musicGroup : sortedMusicGroups) {
            sortedMusic.add(new MusicGroup(musicGroup.getName(), musicGroup.getMusicTracks().stream()
                    .filter(m -> !collapsedMusicGroups.contains(musicGroup.getName()) && m.getName().toLowerCase().contains(filter))
                    .sorted(Comparator.comparing((MusicTrack t) -> !checkedMusicTracks.contains(t))
                            .thenComparing(MusicTrack::getName))
                    .toList()));
        }

        updateVisibleMusicTracks(sortedMusic, collapsedMusicGroups);
        setCheckedMusicTracks(checkedMusicTracks);
        mouseScrolled(new PointD(0, 0), 0, 0);
    }

    private void updateVisibleMusicTracks(Collection<MusicGroup> musicGroups,
                                          Collection<String> collapsedMusicGroups) {
        entryGroups.clear();
        for (MusicGroup musicGroup : musicGroups) {
            entryGroups.add(new MusicListEntryGroup(this, 0, 0, getWidth(), Component.literal(musicGroup.getName()), musicGroup,
                    collapsedMusicGroups.contains(musicGroup.getName())));
        }

        int childrenWidth = isScrollbarVisible() ? getWidth() - SCROLLBAR_WIDTH : getWidth();
        entryGroups.forEach(entry -> entry.setWidth(childrenWidth));
        updateY();
    }

    void updateY() {
        int height = 3;
        for (int i = 0; i < entryGroups.size(); i++) {
            MusicListEntryGroup entryGroup = entryGroups.get(i);
            entryGroup.setY(height);
            height += entryGroup.getHeight();
            if (i != entryGroups.size() - 1) {
                height += 5;
            }
        }
    }

    @Override
    public boolean mouseClickedAll(PointD mousePos, int button) {
        for (UiElement child : getChildren()) {
            if (child.isMouseOver(mousePos.translate(-getX(), -getY()).toIntPoint())
                    && child.mouseClickedAll(mousePos.translate(-getX(), -getY()), button)) {
                setFocusedElement(child);
                setDraggingElement(child);
                return true;
            }
        }

        for (MusicListEntryGroup child : entryGroups) {
            if (child.isMouseOver(mousePos.translate(-getX(), -getY() + getScrollAmount()).toIntPoint())
                    && child.mouseClickedAll(mousePos.translate(-getX(), -getY() + getScrollAmount()), button)) {
                setFocusedElement(child);
                setDraggingElement(child);
                return true;
            }
        }

        if (isMouseOver(mousePos) && mouseClicked(mousePos, button)) {
            setFocusedElement(this);
            setDraggingElement(this);
            return true;
        }

        setFocusedElement(null);
        setDraggingElement(null);
        return false;
    }

    @Override
    public void renderTooltipsInContent(GuiGraphics guiGraphics, Point mousePos, Point absolutePos) {
        for (MusicListEntryGroup child : entryGroups) {
            child.renderTooltips(guiGraphics, mousePos, absolutePos);
        }
    }

    public interface OnMusicTrackToggle {
        void onMusicTrackToggle(MusicTrack musicTrack, boolean newValue);
    }

    public interface OnGroupToggle {
        void onGroupToggle(String group, boolean newValue);
    }
}
