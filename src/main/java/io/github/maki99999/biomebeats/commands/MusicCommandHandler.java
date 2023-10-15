package io.github.maki99999.biomebeats.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.maki99999.biomebeats.BiomeBeats;
import io.github.maki99999.biomebeats.music.MinecraftMixinAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BiomeBeats.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MusicCommandHandler {

    static LiteralArgumentBuilder<CommandSourceStack> nextSong = Commands.literal("skipSong")
            .executes(ctx -> {
                ((MinecraftMixinAccessor) Minecraft.getInstance()).skipSong();
                ctx.getSource().sendSuccess(() -> Component.literal("Skipped current song."), true);
                return 1;
            });

    static LiteralArgumentBuilder<CommandSourceStack> songName = Commands.literal("songName")
            .executes(ctx -> {
                String songName = ((MinecraftMixinAccessor) Minecraft.getInstance()).getCurrentSongName();

                final String msg = songName == null ? "No song is playing at the moment." : "Current song: " + songName;

                ctx.getSource().sendSuccess(() -> Component.literal(msg), true);
                return 1;
            });


    @SubscribeEvent
    public static void registerCommands(RegisterClientCommandsEvent event) {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("biomebeats")
                .requires(cs -> cs.hasPermission(0))
                .then(nextSong)
                .then(songName);
        event.getDispatcher().register(root);
    }
}
