# BukkitInFabricM

**BukkitInFabricM** is utility mod that allows you to create Bukkit plugins in Fabric mods _(I hope it's obvious that you
need any connection with Bukkit in Fabric or vice versa for successful work of this mod)_.

It is recommended to use **Toki** wrapper rather than other existing solution in case if you only need mixins in
Bukkit. Also, this mod was not tested on such solutions, but it probably works fine.

## See also:
* [**Toki**](https://github.com/TetraTau/Toki) — Paper fork with mixins supports
* [**Toki installer**](https://github.com/TetraTau/toki-installer) — mixins supports wrapper for any existing cores
like **Purple**, **Pufferfish**, etc. and also any Minecraft versions

_My other repositories:_

* [**FastNBT**](https://github.com/LeonidMem/FastNBT) — as fast as possible Bukkit library to work with NBT.
* [**ORMM**](https://github.com/LeonidMem/ORMM) — light-weight ORM library to work with SQLite and MySQL.

## Supported versions:
* `1.14 — 1.19.4` requires `BukkitInFabricM 0.1.1`
* `1.20+` requires `BukkitInFabricM 0.1.2`
* But Fabric loader `>= 0.11.0`

## Importing
```groovy
repositories {
  maven { url 'https://mvn.smashup.ru/releases' }
}

dependencies {
  implementation 'ru.leonidm:BukkitInFabricM:0.1.2'
}
```

## Usage
1. Import this mod in `build.gradle` and add it in `mods` directory of your server.
2. Update `depends` in `mod.json` like `{"depends":{"bukkitinfabricm":">=0.1.2"}}`.
3. Create main class for the plugin that should extend `JavaPlugin` class just like in Bukkit.
4. Create `plugin.yml` that must be included in the output JAR just like in Bukkit.
5. ???
6. Profit!

## FAQ:
* **Q:** Where are configs of such plugins are stored?

  **A:** In the directory `./mods/{pluginName}`.
* **Q:** Are dependencies supported?

  **A:** Yes! Such plugins can depend on usual ones and even vice versa.
