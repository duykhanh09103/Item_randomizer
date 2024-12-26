package github.duykhanh09103;


import net.lingala.zip4j.ZipFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import java.io.File;

public final class item_randomizer extends JavaPlugin {
    FileConfiguration config = getConfig();
    private final File worldFile = new File(getDataFolder(),"Void_World.zip");
    @Override
    public void onEnable() {
        config.addDefault("Timer",10);
        config.addDefault("ConfirmReadWarning",false);
        config.options().copyDefaults(true);
        saveConfig();
        if(!worldFile.exists()){
            try {
                saveResource("Void_World.zip",false);
                ZipFile WorldZipFile = new ZipFile(worldFile);
                WorldZipFile.extractAll(String.valueOf(getDataFolder()));
                worldFile.delete();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        };
        this.getCommand("ItemRand").setExecutor(new randomizer_command(this));
        this.getCommand("RandGameWorld").setExecutor(new randomGameWorld_command(this));
        getServer().getPluginManager().registerEvents(new player_event(this), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
