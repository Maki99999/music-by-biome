# BiomeBeats

**BiomeBeats** is a Minecraft mod designed to enhance your gameplay experience by seamlessly integrating custom music tracks into the game. This mod adds support for custom music tracks that play randomly based on the type of biome the player is currently exploring, making your adventures more immersive and enjoyable.

## Features

- **Custom Music:** Add your own music tracks.
- **Biome-Driven Soundscapes:** BiomeBeats introduces a dynamic music system that tailors the in-game music to your surroundings. Each biome within the Minecraft universe is paired with its own distinct selection of music tracks.
- **Customizable Configuration:** The mod comes with a configuration file that allows you to customize the music experience to your liking. You can easily enable or disable specific biomes for music playback and seamlessly integrate your personal custom music library.
- **Ideal for Modpack Creators:** This mod primarily caters to modpack creators, empowering you to effortlessly incorporate your own music selections that perfectly match your modpack.

## Compatibility

- **Client-Sided:** The mod is completely client-sided and is compatible with all servers.
- **Other Mods:** The mod is designed to work smoothly with the majority of other Minecraft mods.
- **Non-Vanilla Biomes:** BiomeBeats uses Minecraft's biome tags to determine which track to play. This means the mod is fully compatible with non-vanilla biomes as long as they incorporate biome tags.

## Configuration

For the mod to work you need a resource pack that holds all your music files.

### Creating a New Resource Pack

1. **Open the Resource Pack Folder:** Start by opening your Minecraft resource pack folder. If you don't know where it's located, you can find it in the game's settings.
2. **Create a New Folder:** Within the resource pack folder, create a new folder. This will be the root folder for your resource pack.
3. **Configure `pack.mcmeta`:** Inside the newly created folder, create a text file and rename it to `pack.mcmeta`. Paste the following JSON code into it:
        ```json
        {
          "pack": {
            "pack_format": 15,
            "description": "Music for the BiomeBeats mod"
          }
        }
        ```
### Organizing Your Sound Files

1. **Create Nested Folders:** Now, organize your sound files with the following folder structure:
    ```
    ├── assets
    │   └── musicbybiome
    │       └── sounds
    │           └── ... (your .ogg music files)
    └── pack.mcmeta
    ```
2. **Copy Your Music Files:** Copy your music files into the `sounds` folder. It's important to note that Minecraft only supports `.ogg` sound files. You may need to use a file converter if your music is in a different format. Also, make sure to rename your music files as `custom0.ogg`, `custom1.ogg`, `custom2.ogg`, and so on. These names are essential for the mod to recognize them.

### Configuring Biome-Based Music

1. **Edit the Configuration File:** In your Minecraft installation folder, you'll find a `musicbybiome-client.toml` config file. This file is generated after running Minecraft at least once with the mod installed. Open it using a text editor.
2. **Assign Music to Biome Tags:** The mod uses biome tags to determine which music to play. In the config file, you'll find entries for various tags, including menu, rain, and night music. To assign music to these tags, refer to the table on the [Minecraft Wiki](https://minecraft.fandom.com/wiki/Tag#Biomes) to identify the tags associated with different biomes. Also included are a few tags added by Forge ([Forge biome tags](https://github.com/MinecraftForge/MinecraftForge/tree/1.20.x/src/generated/resources/data/forge/tags/worldgen/biome)).
   For example, configure the file like this:
    ```toml
    #menu music
    menu_songs = ["custom0", "custom2", "custom3"]
    #night music (high priority)
    night_songs = ["custom11", "custom99"]
    #music for the tag 'is_desert'
    songs_is_desert = ["custom4", "custom33"]
    #music for the tag 'is_plains'
    songs_is_plains = ["custom4"]
    ```
   Customize this section to specify which songs you want to hear for each tag according to your preferences. If a biome has multiple songs assigned to it, it will randomly choose one of them.

## Support

If you find a bug, please [create an issue ticket](https://github.com/Maki99999/music-by-biome/issues).

## License

This mod is available under the **MIT License**.
