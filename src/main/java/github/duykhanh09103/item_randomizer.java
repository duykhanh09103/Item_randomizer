package github.duykhanh09103;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;

public final class item_randomizer extends JavaPlugin {
    FileConfiguration config = getConfig();

    @Override
    public void onEnable() {
        this.getCommand("ItemRand").setExecutor(new randomizer_command(this));
        config.addDefault("Timer",10);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
