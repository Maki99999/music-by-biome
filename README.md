# Music By Biome

**Music By Biome** is a Minecraft Forge mod that allows modpack creators to enhance their player experience by adding custom music that dynamically changes based on the biome the player is currently in.

## Features

- **Custom Music:** Add your own music tracks to your Minecraft modpack and assign them to specific biomes.
- **Dynamic Playback:** The mod ensures that the music seamlessly transitions as players move between different biomes.
- **Ideal for Modpack Creators:** Designed with modpack creators in mind, it integrates smoothly with other mods to provide a cohesive audio experience.

## Usage

### Installation

Download the latest version of the mod from [CurseForge](#) page.

### Configuration

1. Locate or create the audio files you want to use. Supported formats include `.ogg`.
2. Place the music files in a resource pack.
3. In the mod's configuration file, specify the path to the music files and the biomes where each track should play.
An example configuration can look like the following:

```json
{
  "music": [
    {
      "biome": "plains",
      "track": "custom/plains_music.ogg"
    },
    {
      "biome": "forest",
      "track": "custom/forest_music.mp3"
    },
    {
      "biome": "desert",
      "track": "custom/desert_music.ogg"
    }
  ]
}
```

## Compatibility

**Music By Biome** is designed to be compatible with a wide range of Minecraft mods and Forge versions. However, it's a good practice to test it alongside other mods to ensure smooth integration.

## Issues and Feedback

If you encounter any issues, have suggestions, or want to contribute to the development of **Music By Biome**, please visit the [GitHub repository](https://github.com/yourusername/MusicByBiome) and open an issue or pull request.

## License

This mod is available under the [MIT License](#).

## Support

For support or questions, you can create an issue ticket in [GitHub](#) or on the [Minecraft Forums](https://www.minecraftforum.net/).
