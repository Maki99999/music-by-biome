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

public class MusicList extends ScrollArea implements Renderable, ContainerEventHandler {
    private static final int CHILDREN_SPACING = 4;

    private final Minecraft minecraft;
    private final List<EntryGroup> children = new ArrayList<>();
    private final OnMusicTrackToggle onMusicTrackToggle;
    private final OnGroupToggle onGroupToggle;
    private final Collection<MusicGroup> musicGroups;
    private final Rect bounds;

    @Nullable
    private GuiEventListener focusedChild = null;
    private boolean isDragging;

    public MusicList(Minecraft minecraft, Rect bounds, Component message, Collection<MusicGroup> musicGroups,
                     OnMusicTrackToggle onMusicTrackToggle, OnGroupToggle onGroupToggle) {
        super(bounds, message);

        this.minecraft = minecraft;
        this.onMusicTrackToggle = onMusicTrackToggle;
        this.musicGroups = musicGroups;
        this.bounds = bounds;
        this.onGroupToggle = onGroupToggle;

        sortAndFilterMusicTracks("", List.of(), List.of());
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

        boolean clickedScrollbar = updateScrolling(x, y, button);
        boolean clickedChild = false;

        var childrenCopy = new ArrayList<>(children);

        for (EntryGroup child : childrenCopy) {
            if (child.mouseClicked(x, y + scrollAmount(), button)) {
                clickedChild = true;
            }
        }
        return clickedScrollbar || clickedChild;
    }

    @Override
    protected void renderBackground(@NotNull GuiGraphics guiGraphics) {
        guiGraphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), BiomeBeatsColor.DARK_GREY.getHex());
        if (scrollbarVisible()) {
            guiGraphics.fill(getX() + getWidth(), getY(), getX() + getWidth(), getY() + getHeight(),
                    BiomeBeatsColor.DARK_GREY.getHex());
        }
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {}

    @Override
    protected int contentHeight() {
        int height = -2;
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
    }

    public void sortAndFilterMusicTracks(String filter, Collection<? extends MusicTrack> checkedMusicTracks,
                                         Collection<String> collapsedMusicGroups) {
        List<MusicGroup> sortedMusicGroups = musicGroups.stream()
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
                    .filter(m -> !collapsedMusicGroups.contains(musicGroup.getName()) && m.getName().toLowerCase().contains(filter))
                    .sorted(Comparator.comparing((MusicTrack t) -> !checkedMusicTracks.contains(t))
                            .thenComparing(MusicTrack::getName))
                    .toList()));
        }

        updateVisibleMusicTracks(sortedMusic, collapsedMusicGroups);
        setCheckedMusicTracks(checkedMusicTracks);
        mouseScrolled(0, 0, 0, 0);
    }

    private void updateVisibleMusicTracks(Collection<MusicGroup> musicGroups,
                                          Collection<String> collapsedMusicGroups) {
        children.clear();
        for (MusicGroup musicGroup : musicGroups) {
            children.add(new EntryGroup(bounds.x(), 0, width, Component.literal(musicGroup.getName()), musicGroup,
                    collapsedMusicGroups.contains(musicGroup.getName())));
        }

        int childrenWidth = scrollbarVisible() ? width - SCROLLBAR_WIDTH : width;
        for (AbstractWidget entry : children) {
            entry.setWidth(childrenWidth);
        }
        UpdateY();
    }

    private class EntryGroup extends AbstractWidget {
        private static final int CHILDREN_HEIGHT = 16;
        private static final int GROUP_HEADER_HEIGHT = 16;

        private final List<Entry> children = new ArrayList<>();
        private final MusicGroup musicGroup;
        private final TwoStateImageButton collapseButton;

        public EntryGroup(int x, int y, int w, Component message, MusicGroup musicGroup, boolean isCollapsed) {
            super(x, y, w, 0, message);
            this.musicGroup = musicGroup;
            collapseButton = new TwoStateImageButton(x + w - 24, y + 1,
                    new ImageButton(x + w - 24, y + 1, BaseTextureUv.ACCORDION_OPEN_UV, null, null),
                    new ImageButton(x + w - 24, y + 1, BaseTextureUv.ACCORDION_CLOSE_UV, null, null),
                    (btn, newValue) -> onGroupToggle.onGroupToggle(musicGroup.getName(), newValue),
                    Tooltip.create(Component.translatable("menu.biomebeats.expand_collapse")), null
            );
            collapseButton.setState(isCollapsed);

            for (MusicTrack musicTrack : musicGroup.getMusicTracks()) {
                children.add(new Entry(musicTrack, new Rect(x + 1, 0, width - 2, CHILDREN_HEIGHT)));
            }
            UpdateHeight();
        }

        public void UpdateHeight() {
            setHeight(children.stream().mapToInt(AbstractWidget::getHeight).sum() + (children.size() + 1) * CHILDREN_SPACING + GROUP_HEADER_HEIGHT);
            MusicList.this.UpdateY();
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
            collapseButton.render(guiGraphics, mouseX, mouseY, (int) -MusicList.this.scrollAmount());

            for (Entry c : children) {
                c.render(guiGraphics, mouseX, mouseY, partialTicks);
            }
        }

        @Override
        public void setY(int y) {
            super.setY(y);
            collapseButton.setY(y);

            int childY = y + GROUP_HEADER_HEIGHT + CHILDREN_SPACING;
            for (Entry child : children) {
                child.setY(childY);
                childY += child.getHeight() + CHILDREN_SPACING;
            }
        }

        @Override
        public void setWidth(int width) {
            super.setWidth(width);
            collapseButton.setX(getX() + width - 24);

            for (AbstractWidget entry : children) {
                entry.setWidth(width - 2);
            }
        }

        @Override
        public boolean mouseClicked(double x, double y, int button) {
            if (collapseButton.mouseClicked(x, y, button)) {
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
            private final EditBox volumeModifierEditBox;

            private boolean editing = false;

            public Entry(MusicTrack musicTrack, Rect bounds) {
                super(bounds.x(), bounds.y(), bounds.w(), bounds.h(), Component.literal(musicTrack.getName()));
                this.musicTrack = musicTrack;

                tooltip.set(Tooltip.create(Component.literal(musicTrack.getPathName())));
                checkbox = new TwoStateImageButton(getX(), getY(),
                        new LayeredImageButton(getX(), getY(), BaseTextureUv.CHECKBOX_CHECKED_UV, null, null),
                        new LayeredImageButton(getX(), getY(), BaseTextureUv.BUTTON_BASE_INVERTED_UV, null, null),
                        (c, newValue) -> MusicList.this.onMusicTrackToggle.onMusicTrackToggle(musicTrack, newValue),
                        null, null);

                previewButton = new TwoStateImageButton(
                        getX() + width - BaseTextureUv.PLAY_UV.w(), getY(),
                        new LayeredImageButton(getX() + width - BaseTextureUv.PLAY_UV.w(), getY(),
                                BaseTextureUv.STOP_UV, null,
                                Tooltip.create(Component.translatable("menu.biomebeats.stop"))),
                        new LayeredImageButton(getX() + width - BaseTextureUv.PLAY_UV.w(), getY(),
                                BaseTextureUv.PLAY_UV, null,
                                Tooltip.create(Component.translatable("menu.biomebeats.play"))),
                        (btn, newValue) -> {
                            if (newValue)
                                Constants.MUSIC_MANAGER.playPreviewTrack(musicTrack);
                            else
                                Constants.MUSIC_MANAGER.stopPreviewTrack();

                            System.out.println("clicked 'preview' on " + musicTrack.getName());
                        }, null, null);
                editButton = new LayeredImageButton(
                        getX() + width - previewButton.getWidth() - BaseTextureUv.EDIT_UV.w(), getY(),
                        BaseTextureUv.EDIT_UV, btn -> onEdit(), Tooltip.create(Component.translatable("menu.biomebeats.edit")));

                Constants.MUSIC_MANAGER.addPreviewListener(this);

                volumeModifierEditBox = new EditBox(minecraft.font, getX() + width - 60, getY() + CHILDREN_HEIGHT,
                        60, CHILDREN_HEIGHT, Component.translatable("menu.biomebeats.volume_multiplier"));  //TODO doesn't work yet
                volumeModifierEditBox.setHint(Component.literal("0"));
                volumeModifierEditBox.setResponder(this::onVolumeModifierChange);
                volumeModifierEditBox.setFilter(s -> s.matches("^\\d{0,2}\\.?\\d{0,4}$"));
            }

            public boolean isEditing() {
                return editing;
            }

            public void setEditing(boolean editing) {
                this.editing = editing;
                setHeight(CHILDREN_HEIGHT * (editing ? 2 : 1));
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
                for (Entry sibling : EntryGroup.this.children) {
                    sibling.setEditing(sibling == this && !isEditing());
                }
                EntryGroup.this.UpdateHeight();
            }


            public void onClose() {
                Constants.MUSIC_MANAGER.removePreviewListener(this);
            }

            @Override
            protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
                guiGraphics.fill(getX(), getY(), getX() + getWidth(), getY() + CHILDREN_HEIGHT,
                        BiomeBeatsColor.LIGHT_GREY.getHex());

                Rect textRect = new Rect(getX() + 18, getY(), editButton.getX() - (getX() + 22), CHILDREN_HEIGHT);
                drawScrollingString(guiGraphics, MusicList.this.minecraft.font, getMessage(), textRect,
                        (int) -MusicList.this.scrollAmount(), BiomeBeatsColor.WHITE.getHex());
                tooltip.refreshTooltipForNextRenderPass(isHoveringText(textRect, guiGraphics, mouseX, mouseY,
                                (int) -MusicList.this.scrollAmount()),
                        false, new ScreenRectangle(textRect.x(), textRect.y(), textRect.w(), textRect.h()));

                checkbox.render(guiGraphics, mouseX, mouseY, (int) -MusicList.this.scrollAmount());
                editButton.render(guiGraphics, mouseX, mouseY, (int) -MusicList.this.scrollAmount());
                previewButton.render(guiGraphics, mouseX, mouseY, (int) -MusicList.this.scrollAmount());

                if (isEditing()) {
                    renderAddon(guiGraphics, mouseX, mouseY, partialTicks);
                }
            }

            private void renderAddon(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
                guiGraphics.fill(getX(), getY() + CHILDREN_HEIGHT, getX() + getWidth(),
                        getY() + getHeight(), BiomeBeatsColor.LIGHT_GREY.getHex());

                Rect symbolBounds = Rect.fromCoordinates(getX() + 12, getY() + CHILDREN_HEIGHT,
                        getX() + 12 + minecraft.font.width("└"), getY() + getHeight());
                drawScrollingString(guiGraphics,
                        MusicList.this.minecraft.font,
                        Component.literal("└"),
                        symbolBounds,
                        (int) -MusicList.this.scrollAmount(),
                        BiomeBeatsColor.WHITE.getHex()
                );
                Rect textRect = Rect.fromCoordinates(symbolBounds.x2() + 1, getY() + CHILDREN_HEIGHT,
                        volumeModifierEditBox.getX() - 2, getY() + getHeight());
                drawScrollingString(guiGraphics,
                        MusicList.this.minecraft.font,
                        Component.translatable("menu.biomebeats.volume_multiplier"),
                        textRect,
                        (int) -MusicList.this.scrollAmount(),
                        BiomeBeatsColor.WHITE.getHex()
                );

                volumeModifierEditBox.render(guiGraphics, mouseX, mouseY, partialTicks);
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
                volumeModifierEditBox.setY(y + CHILDREN_HEIGHT);
            }

            @Override
            public void setWidth(int width) {
                super.setWidth(width);

                previewButton.setX(getX() + width - BaseTextureUv.PLAY_UV.w());
                editButton.setX(getX() + width - previewButton.getWidth() - BaseTextureUv.EDIT_UV.w());
                volumeModifierEditBox.setX(getX() + width - 60);
            }

            @Override
            protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {}

            @Override
            public boolean mouseClicked(double x, double y, int button) {
                if (editing && volumeModifierEditBox.mouseClicked(x, y, button)) {
                    volumeModifierEditBox.setFocused(true);
                    return true;
                } else {
                    volumeModifierEditBox.setFocused(false);
                }

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

            @Override
            public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
                if (volumeModifierEditBox.isFocused() && volumeModifierEditBox.keyPressed(keyCode, scanCode, modifiers)) {
                    return true;
                }
                return super.keyPressed(keyCode, scanCode, modifiers);
            }

            @Override
            public boolean charTyped(char c, int modifiers) {
                if (volumeModifierEditBox.isFocused() && volumeModifierEditBox.charTyped(c, modifiers)) {
                    return true;
                }
                return super.charTyped(c, modifiers);
            }

            @Override
            public boolean mouseReleased(double x, double y, int button) {
                return volumeModifierEditBox.mouseReleased(x, y, button) || super.mouseReleased(x, y, button);
            }
        }
    }

    public interface OnMusicTrackToggle {
        void onMusicTrackToggle(MusicTrack musicTrack, boolean newValue);
    }

    public interface OnGroupToggle {
        void onGroupToggle(String group, boolean newValue);
    }
}
