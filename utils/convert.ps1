# Define the target zip file name
$zipFileName = "MusicByBiome.zip"

# Create the "assets/musicbybiome/sounds" folder structure
$assetsFolder = "MusicByBiome\assets\musicbybiome\sounds"
if (-not (Test-Path -Path $assetsFolder -PathType Container)) {
    New-Item -ItemType Directory -Path $assetsFolder -Force
}

# Get a list of all MP3 files in the current folder
$mp3Files = Get-ChildItem -Filter *.mp3

# Loop through each MP3 file and convert it to OGG
foreach ($mp3File in $mp3Files) {
    $oggFileName = Join-Path -Path $assetsFolder -ChildPath "$($mp3File.BaseName.ToLower()).ogg"
    Write-Host "Converting $($mp3File.Name) to $oggFileName"
    
    # Use FFmpeg to convert the MP3 file to OGG and strip metadata
    ffmpeg -i $mp3File.FullName -map_metadata -1 -vn -c:a libvorbis $oggFileName
}

# Create the "pack.mcmeta" file in the assets folder
$packMcmetaContent = @"
{
  "pack": {
    "pack_format": 15,
    "description": "Music for the MusicByBiome mod"
  }
}
"@
$packMcmetaPath = Join-Path -Path "MusicByBiome" -ChildPath "pack.mcmeta"
$packMcmetaContent | Set-Content -Path $packMcmetaPath -Encoding UTF8

# Zip the folder structure
#Compress-Archive -Path "MusicByBiome\assets", "MusicByBiome\pack.mcmeta" -DestinationPath $zipFileName -CompressionLevel "NoCompression" -Force

Write-Host "Conversion, pack.mcmeta creation, and zip file creation complete."