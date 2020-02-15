

https://github.com/biship/RimworldConflictChecker/releases

https://rimworldwiki.com/wiki/Modding
https://rimworldwiki.com/wiki/Modding_Tutorials/XML_file_structure
https://rimworldwiki.com/wiki/Modding_Tutorials/Hello_World
https://rimworldwiki.com/wiki/Modding_Tutorials/Compatibility


https://bitbucket.org/Redjordan95/rimworldsavemanager

# Download mods from the steam workshop

https://developer.valvesoftware.com/wiki/SteamCMD

./steamcmd.sh +login anonymous +force_install_dir mods +workshop_download_item 294100 1551147619 +quit
./steamcmd.sh +force_install_dir mods +workshop_download_item 294100 1551147619 +quit

pbs:
    - anonymous access does not allow to download mods from the workshop
    - login requires a Steam Guard code which makes it difficult to automate
    - 64bits binaries of the steamcmd does not seem to exist => docker run cm2network/steamcmd

https://github.com/dgibbs64/SteamCMD-Commands-List/blob/master/steamcmd_commands.txt

```
docker run -v "$PWD/mods":/home/steam/steamcmd/mods/ cm2network/steamcmd bash -c './steamcmd.sh +login user password [CODE] +force_install_dir mods +workshop_download_item 294100 1551147619 +quit'
```

docker run -it --volumes-from steamstore -v "$PWD/mods":/home/steam/steamcmd/mods/ cm2network/steamcmd bash -c './steamcmd.sh +force_install_dir mods +workshop_download_item 294100 1551147619 +quit'

# Run a saved game directly

https://github.com/fluffy-mods/ModManager-SaveLoader

# multiplayer compat

https://docs.google.com/spreadsheets/d/1jaDxV8F7bcz4E9zeIRmZGKuaX7d0kvWWq28aKckISaY/export?format=csv&id=1jaDxV8F7bcz4E9zeIRmZGKuaX7d0kvWWq28aKckISaY&gid=0
