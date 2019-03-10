# PixelmonOverlay
This plugin lets you send preconfigurated announcements through the Pixelmon's Notice Overlay (topbar).

![](https://github.com/happyzleaf/PixelmonOverlayBroadcaster/blob/master/screenshot.png?raw=true)

PlaceholderAPI is supported but optional. However, you won't be able to retrieve any information without it, not even the player's name.

Usage
-
1. Launch the server with the plugin.
2. Modify the messages in ` /config/pixeloverlaybroadcaster.conf` according to the [these](#config) values.
3. ???
4. Profit.

Config
-
`broadcastInterval` (defaults to 300 seconds) sets the interval after which the announcement will change.

`silenceInterval` (defaults to 300 seconds) sets the time to wait between two announcements. Set to 0 to disable.

`announcements` contains the ordered list of all the (guess what?) announcements. They will be shown according to their position in the list.

Node|Values|Meaning
---|---|---
`layout`|`LEFT_AND_RIGHT`, `LEFT` or `RIGHT`.|Specifies the position of the sprite.
`lines`|List of strings. |Sets the lines to be shown to the player. [Formatting codes](https://minecraft.gamepedia.com/g00/Formatting_codes?i10c.encReferrer=aHR0cHM6Ly93d3cuZ29vZ2xlLml0Lw%3D%3D&i10c.ua=1&i10c.dv=14) and [placeholders](https://github.com/rojo8399/PlaceholderAPI/wiki/Placeholders) are supported.
`duration`|Decimal number. Example `20`|How long this announcement will last for. If not present, `broadcastInterval` will be used instead.
`type`|`PokemonSprite`, `Pokemon3D`, `ItemSprite`, `Item3D`|Specifies the type of the sprite, read below for more info.
`spec`|[Pokémon's spec](https://pixelmonmod.com/wiki/index.php?title=Pokemon_spec). Example: `pikachu s`|Required by `PokemonSprite` and `Pokemon3D`, specifies the pokémon's info.
`itemStack`|The ItemStack.|Required by `ItemStack`, specifies the itemstack.

Restrictions
-
I fixed a lot of stuff, but also implemented new bugs😅. There are some problems with the colors of the window, I'll fix them for Pixelmon 7.0.4.
However, you can now use any kind of item and pokémon, as long as the client knows how to render them, and `scale` has been removed, you can change their size through the growth's pokémon spec, like `gr:giant` to make them bigger, and `gr:microscopic` to make them smaller.

You can use the character `&` to style your lines with colors and formattings, while to use the character `&` normally you have to escape it by placing `\\` before. For example, `&1Pikachu \\& Solgaleo` will print a blue (`&1`) string saying `Pikachu & Solgaleo`.

API
-
I split the plugin to api and implementation, and now other plugins can use the Pixelmon Notice Overlay to show off messages and custom announcements.

Example:
```java
OverlayService service = Sponge.getServiceManager().provideUnchecked(OverlayService.class);

Overlay overlay = service.create([...]);
service.show(overlay);
```
This might seem confusing, I'll update the thread with the link to the docs as soon as I finish writing them.

Releases
-
For the releases, head off to the [ore page](https://ore.spongepowered.org/happyzlife/PixelmonOverlay).
