package io.github.maki99999.biomebeats.gui;

import io.github.maki99999.biomebeats.Constants;
import io.github.maki99999.biomebeats.music.MusicGroup;
import io.github.maki99999.biomebeats.music.MusicTrack;
import io.github.maki99999.biomebeats.music.PreviewListener;
import io.github.maki99999.biomebeats.util.BiomeBeatsColor;
import io.github.maki99999.biomebeats.util.Rect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static io.github.maki99999.biomebeats.util.DrawUtils.drawScrollingString;

public class MusicList extends AbstractScrollWidget implements Renderable, ContainerEventHandler {
    private static final int SCROLL_BAR_WIDTH = 8;
    private static final int CHILDREN_HEIGHT = 16;
    private static final int CHILDREN_SPACING = 4;

    private final Minecraft minecraft;
    private final List<EntryGroup> children = new ArrayList<>();
    private final OnMusicTrackToggle onMusicTrackToggle;
    private final Collection<MusicGroup> musicGroups;
    private final Rect bounds;

    @Nullable
    private GuiEventListener focusedChild = null;
    private boolean isDragging;

    public MusicList(Minecraft minecraft, Rect bounds, Component message, Collection<MusicGroup> musicGroups,
                     OnMusicTrackToggle onMusicTrackToggle) {
        super(bounds.x(), bounds.y(), bounds.w() - SCROLL_BAR_WIDTH, bounds.h(), message);

        this.minecraft = minecraft;
        this.onMusicTrackToggle = onMusicTrackToggle;
        this.musicGroups = musicGroups;
        this.bounds = bounds;

        sortAndFilterMusicTracks("", List.of());
        setVisibility(false);
    }

    private void UpdateY() {
        int height = 3;
        for (int i = 0; i < children.size(); i++) {
            EntryGroup entryGroup = children.get(i);
            entryGroup.setY(getY() + height);
            height += entryGroup.getHeight();
            if (i != children.size() - 1) {
                height += 5;
            }
        }
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        if (!visible) return false;

        boolean clickedInArea = super.mouseClicked(x, y, button);
        boolean clickedChild = false;

        for (EntryGroup child : children) {
            if (child.mouseClicked(x, y + scrollAmount(), button)) {
                clickedChild = true;
            }
        }
        return clickedInArea || clickedChild;
    }

    @Override
    protected void renderBackground(@NotNull GuiGraphics guiGraphics) {
        guiGraphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), BiomeBeatsColor.DARK_GREY.getHex());
        if (scrollbarVisible()) {
            guiGraphics.fill(getX() + getWidth(), getY(), getX() + getWidth() + SCROLL_BAR_WIDTH, getY() + getHeight(),
                    BiomeBeatsColor.DARK_GREY.getHex());
        }
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {}

    @Override
    protected int getInnerHeight() {
        int height = -10;
        for (EntryGroup entryGroup : children) {
            height += entryGroup.getHeight() + 5;
        }
        return height;
    }

    @Override
    protected double scrollRate() {
        return 30.0;
    }

    @Override
    protected void renderContents(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        int height = 3;
        for (int i = 0; i < children.size(); i++) {
            EntryGroup entryGroup = children.get(i);
            entryGroup.render(guiGraphics, mouseX, mouseY + (int) scrollAmount(), partialTicks);
            height += entryGroup.getHeight();
            if (i != children.size() - 1) {
                guiGraphics.fill(getX(), getY() + height, getX() + getWidth(), getY() + height + 1,
                        BiomeBeatsColor.BLACK.getHex());
                height += 5;
            }
        }
    }

    @Override
    public @NotNull List<? extends GuiEventListener> children() {
        return children;
    }

    @Override
    public boolean isDragging() {
        return isDragging;
    }

    @Override
    public void setDragging(boolean dragging) {
        isDragging = dragging;
    }

    @Nullable
    @Override
    public GuiEventListener getFocused() {
        return focusedChild;
    }

    @Override
    public void setFocused(@Nullable GuiEventListener guiEventListener) {
        if (this.focusedChild != null) {
            this.focusedChild.setFocused(false);
        }

        if (guiEventListener != null) {
            guiEventListener.setFocused(true);
        }

        this.focusedChild = guiEventListener;
    }

    public void onClose() {
        for (var child : children) {
            child.onClose();
        }
    }

    public void setVisibility(boolean visible) {
        this.visible = visible;
    }

    public void setCheckedMusicTracks(Collection<? extends MusicTrack> musicTracks) {
        for (var child : children) {
            child.setCheckedMusicTracks(musicTracks);
        }
        setScrollAmount(0);
    }

    public void sortAndFilterMusicTracks(String filter, Collection<? extends MusicTrack> checkedMusicTracks) {
        var sortedMusicGroups = musicGroups.stream()
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

        List<MusicGroup> sortedMusic = new ArrayList<>();

        for (MusicGroup musicGroup : sortedMusicGroups) {
            sortedMusic.add(new MusicGroup(musicGroup.getName(), musicGroup.getMusicTracks().stream()
                    .filter(m -> m.getName().toLowerCase().contains(filter))
                    .sorted(Comparator.comparing(MusicTrack::getName))
                    .toList()));
        }

        updateVisibleMusicTracks(sortedMusic);
        setCheckedMusicTracks(checkedMusicTracks);
    }

    private void updateVisibleMusicTracks(Collection<MusicGroup> musicGroups) {
        children.clear();
        for (MusicGroup musicGroup : musicGroups) {
            children.add(new EntryGroup(bounds.x(), 0, width, Component.literal(musicGroup.getName()), musicGroup));
        }

        width = scrollbarVisible() ? bounds.w() - SCROLL_BAR_WIDTH : bounds.w();
        for (AbstractWidget entry : children) {
            entry.setWidth(width);
        }
        UpdateY();
    }

    private class EntryGroup extends AbstractWidget {
        private final List<Entry> children = new ArrayList<>();
        private final MusicGroup musicGroup;
        private final ImageButton toggleButton;

        public EntryGroup(int x, int y, int w, Component message, MusicGroup musicGroup) {
            super(x, y, w, 0, message);
            this.musicGroup = musicGroup;
            toggleButton = new ImageButton(x + w - 24, y + 1, BaseTextureUv.ACCORDION_OPEN_UV, p -> {
                //TODO
                System.out.println("pressed category " + musicGroup.getName());
            }, null);

            for (MusicTrack musicTrack : musicGroup.getMusicTracks()) {
                children.add(new Entry(musicTrack, new Rect(x, 0, width, CHILDREN_HEIGHT)));
            }

            setHeight((children.size() + 1) * CHILDREN_HEIGHT + (children.size() - 1) * CHILDREN_SPACING + 8);
        }

        public void onClose() {
            for (var child : children) {
                child.onClose();
            }
        }

        @Override
        protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
            drawScrollingString(guiGraphics, MusicList.this.minecraft.font, Component.literal(musicGroup.getName()),
                    new Rect(getX() + 16, getY() + 4, getWidth() - 48, 8),
                    (int) -MusicList.this.scrollAmount(), BiomeBeatsColor.WHITE.getHex());
            toggleButton.render(guiGraphics, mouseX, mouseY, (int) -MusicList.this.scrollAmount());

            for (Entry c : children) {
                c.render(guiGraphics, mouseX, mouseY, partialTicks);
            }
        }

        @Override
        public void setY(int y) {
            super.setY(y);
            toggleButton.setY(y);

            for (int i = 0; i < children.size(); i++) {
                children.get(i).setY(y + (i + 1) * (CHILDREN_HEIGHT + CHILDREN_SPACING));
            }
        }

        @Override
        public void setWidth(int width) {
            super.setWidth(width);
            toggleButton.setX(getX() + width - 24);

            for (AbstractWidget entry : children) {
                entry.setWidth(width);
            }
        }

        @Override
        public boolean mouseClicked(double x, double y, int button) {
            if (toggleButton.mouseClicked(x, y, button)) {
                return true;
            }

            for (Entry child : children) {
                if (child.mouseClicked(x, y, button)) {
                    return true;
                }
            }

            return false;
        }

        @Override
        protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {}

        public void setCheckedMusicTracks(Collection<? extends MusicTrack> musicTracks) {
            for (var child : children) {
                child.setCheckedState(musicTracks.contains(child.getMusicTrack()));
            }
        }

        private class Entry extends AbstractWidget implements PreviewListener {
            private final MusicTrack musicTrack;
            private final WidgetTooltipHolder tooltip = new WidgetTooltipHolder();
            private final TwoStateImageButton previewButton;
            private final ImageButton editButton;
            private final TwoStateImageButton checkbox;

            public Entry(MusicTrack musicTrack, Rect bounds) {
                super(bounds.x(), bounds.y(), bounds.w(), bounds.h(), Component.literal(musicTrack.getName()));
                this.musicTrack = musicTrack;

                tooltip.set(Tooltip.create(Component.literal(musicTrack.getPathName())));
                checkbox = new TwoStateImageButton(getX() + 1, getY(),
                        new LayeredImageButton(getX() + 1, getY(), BaseTextureUv.CHECKBOX_CHECKED_UV, null, null),
                        new LayeredImageButton(getX() + 1, getY(), BaseTextureUv.BUTTON_BASE_INVERTED_UV, null, null),
                        (c, newValue) -> MusicList.this.onMusicTrackToggle.onMusicTrackToggle(musicTrack, newValue),
                        null, null);

                previewButton = new TwoStateImageButton(
                        getX() + width - BaseTextureUv.PLAY_UV.w() - 1, getY(),
                        new LayeredImageButton(getX() + width - BaseTextureUv.PLAY_UV.w() - 1, getY(),
                                BaseTextureUv.PAUSE_UV, null, null),
                        new LayeredImageButton(getX() + width - BaseTextureUv.PLAY_UV.w() - 1, getY(),
                                BaseTextureUv.PLAY_UV, null, null),
                        (btn, newValue) -> {
                            if (newValue)
                                Constants.MUSIC_MANAGER.playPreviewTrack(musicTrack);
                            else
                                Constants.MUSIC_MANAGER.stopPreviewTrack();

                            System.out.println("clicked 'preview' on " + musicTrack.getName());
                        }, Tooltip.create(Component.translatable("menu.biomebeats.preview")),
                        null
                );
                editButton = new LayeredImageButton(
                        getX() + width - previewButton.getWidth() - BaseTextureUv.EDIT_UV.w() - 1, getY(),
                        BaseTextureUv.EDIT_UV, (click) -> {
                    //TODO
                    System.out.println("clicked 'edit' on " + getMessage());
                }, Tooltip.create(Component.translatable("menu.biomebeats.edit")));

                Constants.MUSIC_MANAGER.addPreviewListener(this);
            }

            public void onClose() {
                Constants.MUSIC_MANAGER.removePreviewListener(this);
            }

            @Override
            protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
                guiGraphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(),
                        BiomeBeatsColor.LIGHT_GREY.getHex());

                var textRect = new Rect(getX() + 19, getY(), editButton.getX() - (getX() + 22), getHeight());
                drawScrollingString(guiGraphics, MusicList.this.minecraft.font, getMessage(), textRect,
                        (int) -MusicList.this.scrollAmount(), BiomeBeatsColor.WHITE.getHex());
                tooltip.refreshTooltipForNextRenderPass(isHoveringText(textRect, guiGraphics, mouseX, mouseY,
                                (int) -MusicList.this.scrollAmount()),
                        false, new ScreenRectangle(textRect.x(), textRect.y(), textRect.w(), textRect.h()));

                checkbox.render(guiGraphics, mouseX, mouseY, (int) -MusicList.this.scrollAmount());
                editButton.render(guiGraphics, mouseX, mouseY, (int) -MusicList.this.scrollAmount());
                previewButton.render(guiGraphics, mouseX, mouseY, (int) -MusicList.this.scrollAmount());
            }

            private boolean isHoveringText(Rect textRect, @NotNull GuiGraphics guiGraphics, int mouseX, int mouseY,
                                           int mouseYScissorOffset) {
                return guiGraphics.containsPointInScissor(mouseX, mouseY + mouseYScissorOffset)
                        && mouseX >= textRect.x1()
                        && mouseY >= textRect.y1()
                        && mouseX < textRect.x2()
                        && mouseY < textRect.y2();
            }

            @Override
            public void setY(int y) {
                super.setY(y);
                checkbox.setY(y);
                editButton.setY(y);
                previewButton.setY(y);
            }

            @Override
            public void setWidth(int width) {
                super.setWidth(width);

                previewButton.setX(getX() + width - BaseTextureUv.PLAY_UV.w() - 1);
                editButton.setX(getX() + width - previewButton.getWidth() - BaseTextureUv.EDIT_UV.w() - 1);
            }

            @Override
            protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {}

            @Override
            public boolean mouseClicked(double x, double y, int button) {
                return checkbox.mouseClicked(x, y, button)
                        || editButton.mouseClicked(x, y, button)
                        || previewButton.mouseClicked(x, y, button);
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
        }
    }

    public interface OnMusicTrackToggle {
        void onMusicTrackToggle(MusicTrack musicTrack, boolean newValue);
    }
}
