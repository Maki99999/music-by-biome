![BiomeBeats logo](images/logo.png)

[![ko-fi](https://ko-fi.com/img/githubbutton_sm.svg)](https://ko-fi.com/W7W7166XAE)

This mod has the goal of improving Minecraft's music system.

## Features

The mod lets you add custom tracks, removes the silences between the songs, and gives you control over when each track plays.

### Custom Music

Adding a new song is as easy as dropping it into the mod’s music folder.

### Continuous Background Music

Normally, Minecraft has over 70 tracks, but because of the long pauses Minecraft has between the tracks, you'll only hear a few each hour. This mod cuts out those pauses, so you get a steady flow of background music.

### Control When a Track Should Play

You get to decide when specific songs play. For example, you could set underwater music to play outside of ocean biomes, or bring in spooky tracks at night. This lets you customize the game’s atmosphere to fit whatever vibe you’re going for.

The mod uses conditions to decide when specific music tracks should play. A condition could be something like "the player is in the plains biome", "the player is in a 'cold' biome", "the player is underwater" or "a boss health bar is visible." For each condition, you can pick which tracks will play when it’s met.

You can also create combined conditions by selecting multiple individual ones. A combined condition only counts as "fulfilled" when all its parts are met, giving you more control over when certain tracks play and letting you fine-tune the music to match specific situations. For example, you could set up a combined condition that triggers when the player is in a cold forest at night.

## Compatibility

- Forge, Neoforge, Fabric: There is a version for each of the three major mod loaders.
- Client-Sided: Fully compatible with all servers.
- Other mods:  Automatically detects music tracks, biomes, and biome tags from other mods.
- Minecraft Version: Currently supports Minecraft 1.21 only.

## Configuration

Configuration is managed in-game through the config menu. When in a world, press the "Open Config" key (default is "B") to access the configuration screen. The configuration is automatically saved after exiting the screen.

![Main Config Screen](images/main1.jpg)

The main screen is split into two parts. On the left, you can browse and select conditions by category using the tabs. When you select a condition, the right side displays its settings, where you can assign music tracks and more.

At the bottom left, there are two buttons:
- The "Reload" button reloads the config file and re-detects biomes and biome tags.
- The "Open Music Folder" button opens the folder where you can add music files. Once added, press reload to make the new files appear in the config screen.

### Categories of Conditions

There are four categories of conditions, each accessible via a tab on the left side of the screen. A search field also lets you quickly find specific conditions within each category.

- **Biome Conditions**: There is a condition for each biome, triggered when the player stays in that biome for at least three seconds. This includes modded biomes.
- **Biome Tag Conditions**: (Sub-)Biomes have tags that describe their attributes, like "cold," "dry," "snowy," or "overworld." These tags are helpful for setting up tracks to play in similar biomes or entire dimensions. Biome tags from mods are also included.
- **Other Conditions**:
  ![Config Screen with Other Conditions](images/main2.jpg)
  Most conditions in this category exist because Minecraft utilizes something similar to them. I also added a few other ones that I think are useful. The “No Other Music Is Playing” condition is a special fallback that’s always met (with the lowest priority) and can be used to still have background music when no other conditions are active.
- **Combined Conditions**: Useful for more specific situations, these conditions only activate when all their sub-conditions are met. For example, you could set music to play when the player is on a cold hill at night (using conditions "Is Cold", "Is Hill", "Is Night").

### Configuring a Condition: Priorities and Music Tracks

![Config Screen with a condition selected](images/main3.jpg)

When you select a condition, you can set its priority level and choose music tracks to play when the condition is met.

The priority comes into play when the mod selects a music track. The higher the priority number, the greater the priority. Conditions with a lower priority are ignored if higher-priority conditions are also met. For more details, see [this section](#how-the-mod-chooses-a-music-track).

You can select any number of music tracks, one of which will be chosen at random when the condition is met.  Tracks are organized with custom songs at the top, followed by Minecraft’s background music, then music discs, and finally tracks from other mods. The search field helps you quickly locate specific songs. To preview a track, simply click the button next to it.

### Adding and Editing a Combined Condition

![Config Screen with Combined Conditions](images/combined1.jpg)

The combined conditions tab has a slightly different layout. Here, you’ll find a button to add new combined conditions, and each existing combined condition has an edit button next to it. Aside from this, configuring priority levels and selecting music tracks works the same way as with other conditions.

![Config Screen with](images/combined2.jpg)

When you create or edit a combined condition, a new screen opens where you can configure its details. Here, you can set the condition's name, add a description, and choose the sub-conditions that must be met for the combined condition to activate.

## How the Mod Chooses a Music Track

The mod continuously checks which conditions are currently met. Each condition has a priority level and a list of assigned music tracks, both of which can be set in the config screen.
The mod will identify the highest priority among the met conditions and filters out any conditions that don’t match that priority. It then gathers all music tracks assigned to the remaining conditions and randomly selects one to play. Recently played tracks are tracked, so the same songs won’t repeat back-to-back.

## Current Limitations

- Custom music tracks can be added in the following formats: .wav, .mp3, .ogg (Vorbis), .flac, .aiff/.aif/.aifc, .au, .ape, .spx.
- The mod overrides Minecraft's default music system, so other mods that rely on it might encounter compatibility issues.

## Upcoming Features

I still have some ideas I want to implement in the future.

- Individual Track Volume Control: This would allow you to adjust the volume of each track separately, which is helpful if your custom tracks have different audio levels.
- UI Improvements: Small tweaks, like an indicator to show which conditions have music assigned and which ones are currently active.
- Backports for Earlier Minecraft Versions: Backporting the mod to older versions is a bit tricky with the multiloader setup I have, but doing it modloader by modloader and version by version should be manageable, just time-consuming.
- Additional Settings:
  - Bringing back random silences between songs, for those who like the pauses in vanilla Minecraft.
  - Adjusting fade-in and fade-out times between the tracks.
- "Winamp": A mini-player you can have in a corner of the screen (or somewhere else) where you can see the current song, its duration etc. It could also have controls to e.g. pause or skip songs.
- Condition Grouping: A way to group conditions, so you can assign music to all of them at once, rather than setting each condition manually.
- Support for More Audio Formats: I’m looking into a Java library that could make it possible to play nearly any audio format, though switching to it would require some extra work.

## Modpack creators

Feel free to use this mod in any modpack.

## Support

If you find a bug or if you have any suggestions, please [create a ticket](https://github.com/Maki99999/music-by-biome/issues).

## License

This mod is available under the **MIT License**.
