## How to add custom texture sheets

1. Create your texture sheet - it should have the same icon layout as the default texture sheet
2. Create a resource pack
3. Put your texture sheet (with a 2:1 width to height ratio) into `assets/armorpointspp/textures/gui`
4. It should be named something like `icons_custom.png`
5. Edit the value of `textureSheet` in the armorpoints++ config's general section to the name of your texture sheet (i.e. `icons_custom` for the above example)

### Texture sheets outside the armorpointspp namespace
When you don't want to or technically can not put a texture sheet into the armorpointspp resource namespace, armorpoints++ has support for that case.
You can put your texture sheet into any namespace (for example `assets/minecraft/textures/gui/icons_custom.png`).
The only difference is, that in the armorpoints++ config, you **must** specify the **full** resource location (i.e. `minecraft:textures/gui/icons_custom.png` for the above example)

<hr>
11.2021, Cheos
