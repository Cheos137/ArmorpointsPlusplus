[github]: https://github.com/Cheos137/ArmorpointsPlusplus/issues
[curseforge]: https://www.curseforge.com/minecraft/mc-mods/armorpoints

## v2.0.0 BETA 1

To be honest... when viewed from the code side, v1 was pretty crap.<br>
No compatibility whatsoever, a couple of bugs which I only noticed by making v2 and a system in the background that was not scalable whatsoever.

But now... v2 is in beta. That means I have re-implemented all the features from v1.<br>
There are also some more, cool features to come. :)

The curseforge update will update with the release of `release candidate` versions.

## What has changed?

Well... it doesn't seem like much from the outside...<br>
The biggest difference may be that the two textures used in v1 are now in one combined file.<br>
Also, highlighted absorption borders are now everso slightly brighter (which feels a LOT better than before)<br>
You may also notice that resistance borders now work with the vanilla armor bar and that other mods (like mekanism) do no longer render over each other.

If you take a look at the config, you can see that some of the values have changed name.<br>
A new possible value has been added to `suffix`. `SI`, which is the new default takes the place of `SCI`,<br>
while `SCI`'s functionality has been altered to always show the armor value in scientific notation.<br>
`showAbsorption` is an entirely new config option which allows you to turn off absorption borders individually from health stacking.<br>
There is one thing to note: `showAbsorption` will only work if health stacking is enabled - it will NOT work with the vanilla health bar.

This update **WILL BREAK** your config files!

What you probably won't see is that on the inside basically everything has changed.<br>
It's a much more clean approach than in v1 (as i said, v1 was pretty awful).

## Which versions (ports) will receive a v2 beta?

Beta will only exist for FORGE 1.16.5 for now, but there will probably a beta for FABRIC 1.16.5 as well, anytime soon (maybe 1-2 weeks from now).

Other versions that are eligible for a port (i.e. 1.8 and 1.12.2) will NOT receive any beta versions;<br>
But there will be release candidate versions for them (as well as for 1.16.5).<br>
The release candidate versions will hit live all at the same time (after FORGE 1.16.5 is out of beta).

## Beta testing

There are probably many bugs.<br>
But you see... I will never be able to find all of them.<br>
So, if you have some time to spare, I would really appreciate it if you tried to sniff out some of them.

Found one? Report it [here][github] using the `Bug report` template.

Do you have any ideas for improvement or new features?<br>
Create an issue using the `Feature request` template [here][github].

Any questions? You can reach me in the comment section on [CurseForge][curseforge] or [GitHub][github] by using the `General question / etc` template.

Discord? Not right now, but I will set one up, if there is demand.


<hr>
2021, Cheos
