package github.duykhanh09103;

import net.md_5.bungee.api.chat.TranslatableComponent;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class randomGameWorld_command implements CommandExecutor, TabExecutor {
    private final item_randomizer plugin;
    public randomGameWorld_command(item_randomizer plugin) {
        this.plugin = plugin;
    }
    public static Map<Player,String> playingPlayer = new HashMap<>();
    public static Map<Player,Boolean> playerState =  new HashMap<>();
    Map<Player, Location> playerLocationBeforePLaying= new HashMap<>();
    public static Boolean isRunning = false;
    BossBar bossBar = Bukkit.createBossBar("RandItem", BarColor.YELLOW, BarStyle.SEGMENTED_10);
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
        if(strings[0].equalsIgnoreCase("help")) {
                //log warning
                FileConfiguration config = plugin.getConfig();
                boolean confirm = config.getBoolean("ConfirmReadWarning");
                if (!confirm) {
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4WARNING DO NOT USE THIS COMMAND IN /SMP/SURVIVAL WORLD\n" +
                            "YOU WILL LOSE ALL STUFF AND PROGRESS!\n" +
                            "THIS WILL BE DISABLE AFTER YOU USE &r /RandGameWorld confirm"));
                }
                //send help command
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&r/RandGameWorld start : start the game!\n" +
                                "/RandGameWorld stop : stop the game!\n" +
                                "/RandGameWorld help : Show this message!\n"+
                                "&6 For changing timer please use the &r /ItemRand setTimer"));
                return true;
            }

            if (strings[0].equalsIgnoreCase("start")) {
                playingPlayer.clear();
                playerState.clear();
                playerLocationBeforePLaying.clear();
                int timer = (int) plugin.config.get("Timer");
                FileConfiguration config = plugin.getConfig();
                boolean confirm = config.getBoolean("ConfirmReadWarning");
                int maxPlayer = 4;
                Player[] allPlayerArray = Bukkit.getOnlinePlayers().toArray(Player[]::new);
                if (!confirm) {
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4WARNING DO NOT USE THIS COMMAND IN /SMP/SURVIVAL WORLD\n" +
                            "YOU WILL LOSE ALL STUFF AND PROGRESS!\n" +
                            "TRY AGAIN AFTER TYPING &r /RandGameWorld confirm"));
                    return true;
                }
                if(randomizer_command.isTaskRunning){
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&4 WARNING, THE RANDITEM IS RUNNING PLEASE STOP RANDITEM BEFORE USING THIS"));
                    return true;
                }
                if(isRunning){
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&4 WARNING, THE WORLD IS RUNNING PLEASE STOP IT BEFORE USING THE COMMAND AGAIN"));
                    return true;
                }

                if(allPlayerArray.length>maxPlayer){
                    for(Player allplayer:Bukkit.getOnlinePlayers()){
                        allplayer.sendMessage(ChatColor.translateAlternateColorCodes('&',"&6 Sorry but the plugin currently only support max "+maxPlayer+" Player! so only &r these &6 people gonna get to play!"));
                    }
                }
                int MinX = Math.min(allPlayerArray.length, maxPlayer);
                for(int i = 0;i<MinX;i++){
                    int playerNumber = i+1;
                    for(Player allplayer:Bukkit.getOnlinePlayers()){allplayer.sendMessage("Playing Player:");}
                    for(Player allplayer:Bukkit.getOnlinePlayers()){allplayer.sendMessage(ChatColor.translateAlternateColorCodes('&',"&a Player "+playerNumber+" :&r "+allPlayerArray[i].getName()));}
                    playingPlayer.put(allPlayerArray[i], "Player"+playerNumber);
                    playerLocationBeforePLaying.put(allPlayerArray[i],allPlayerArray[i].getLocation());
                    playerState.put(allPlayerArray[i],true);
                    allPlayerArray[i].setGameMode(GameMode.SURVIVAL);
                    for(PotionEffect effects : allPlayerArray[i].getActivePotionEffects()){allPlayerArray[i].removePotionEffect(effects.getType());}
                    //will change later bcuz getMaxHealth is deprecated
                    allPlayerArray[i].setHealth(allPlayerArray[i].getMaxHealth());
                    allPlayerArray[i].setSaturation(20);
                    allPlayerArray[i].setFoodLevel(20);
                    //does not need this for now as invent does not sync edit:nvm it does what
                    allPlayerArray[i].getInventory().clear();
                }
                try {
                    FileUtils.copyDirectory(new File(plugin.getDataFolder() + "/Void_World"), new File("Void_World"));
                    World world = new WorldCreator("Void_World").createWorld();

                        for(Player player:playingPlayer.keySet()){
                              Location loc = switch(playingPlayer.get(player)){
                              default -> new Location(Bukkit.getWorld("Void_World"),0.500,10.500,0.500);
                              case "Player1" -> new Location(Bukkit.getWorld("Void_World"),0.500,0.500,0.500);
                              case "Player2" -> new Location(Bukkit.getWorld("Void_World"),10.500,0.500,0.500);
                              case "Player3" -> new Location(Bukkit.getWorld("Void_World"),0.500,0.500,10.500);
                              case "Player4" -> new Location(Bukkit.getWorld("Void_World"),10.500,0.500,10.500);
                              };
                              player.teleport(loc);
                        }

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                bossBar.setProgress(1.0);
                isRunning=true;

                for(Player player:playingPlayer.keySet()){
                    bossBar.addPlayer(player);
                    player.sendMessage(ChatColor.YELLOW + "ItemRand" + ChatColor.WHITE +" :"+"Starting! The delay time is " +timer+"s");
                }

                new BukkitRunnable() {
                    int countdown = timer;
                    @Override
                    public void run() {
                        if(countdown>0){
                            bossBar.setTitle("Next Items: "+countdown+"s");
                            bossBar.setProgress((double) countdown / timer);
                            countdown--;
                        }
                        else {
                            for (Player player:playingPlayer.keySet()) {
                                Material randomMaterial = Material.values()[new Random().nextInt(Material.values().length)];
                                if(randomMaterial.isItem()){
                                    ItemStack randomItem = new ItemStack(randomMaterial) ;
                                    player.getInventory().addItem(randomItem);
                                    player.sendMessage(ChatColor.YELLOW + "ItemRand" + ChatColor.WHITE + " : You have received " + new TranslatableComponent(randomItem.getTranslationKey()).toPlainText());
                                }
                            }
                            countdown = timer;
                        }

                    }

                }.runTaskTimer(plugin, 0, 20L);
                new BukkitRunnable(){
                    @Override
                    public void run(){
                        if(playerState.size() == 1){
                            bossBar.removeAll();
                            for(Player winningPlayer : playerState.keySet()){
                                for(Player allplayer:Bukkit.getOnlinePlayers()){allplayer.sendMessage(ChatColor.translateAlternateColorCodes('&',"&6&kCongrats &r "+winningPlayer.getName()+" &6YOU WONNNNNNNNN"));}
                            }
                            for(Player player:playingPlayer.keySet()){
                                player.setGameMode(GameMode.SURVIVAL);
                                player.teleport(playerLocationBeforePLaying.get(player));
                            }
                            for(Player player:Bukkit.getOnlinePlayers()){
                                if(player.getWorld()==Bukkit.getWorld("Void_World")){
                                    Location location = Bukkit.getWorld("world").getSpawnLocation();
                                    player.teleport(location);
                                }
                            }
                            try {
                                isRunning= false;
                                playingPlayer.clear();
                                playerState.clear();
                                playerLocationBeforePLaying.clear();
                                Bukkit.unloadWorld("Void_World",false);
                                FileUtils.deleteDirectory(new File("Void_World"));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            Bukkit.getScheduler().cancelTasks(plugin);
                        }
                    }
                }.runTaskTimer(plugin,0,1L);

          return true;
            }

            if (strings[0].equalsIgnoreCase("stop")) {
               if(!isRunning){
                   commandSender.sendMessage(ChatColor.YELLOW + "ItemRand" + ChatColor.WHITE +" :"+ChatColor.RED+"Error: Nothing running right now! ");
                   return true;
               }
               if(randomizer_command.isTaskRunning){
                   commandSender.sendMessage(ChatColor.YELLOW + "ItemRand" + ChatColor.WHITE +" :"+ChatColor.RED+"Error: RandItem is running right now! ");
                    return true;
               }
               for(Player player :playingPlayer.keySet()){
                   player.setGameMode(GameMode.SURVIVAL);
                   player.teleport(playerLocationBeforePLaying.get(player));
               }
                for(Player player:Bukkit.getOnlinePlayers()){
                    if(player.getWorld()==Bukkit.getWorld("Void_World")){
                        Location location = Bukkit.getWorld("world").getSpawnLocation();
                        player.teleport(location);
                    }
                }
                try {
                    isRunning=false;
                    bossBar.removeAll();
                    playingPlayer.clear();
                    playerState.clear();
                    playerLocationBeforePLaying.clear();
                    Bukkit.unloadWorld("Void_World",false);
                    FileUtils.deleteDirectory(new File("Void_World"));
                    Bukkit.getScheduler().cancelTasks(plugin);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }
            if (strings[0].equalsIgnoreCase("confirm")) {
                FileConfiguration config = plugin.getConfig();
                boolean confirmConfig = config.getBoolean("ConfirmReadWarning");
                if(confirmConfig){
                    commandSender.sendMessage(ChatColor.YELLOW + "ItemRand" + ChatColor.WHITE +" :"+ChatColor.RED+"Error: Already confirmed! ");
                    return true;
                }
                config.set("ConfirmReadWarning",true);
                plugin.saveConfig();
                commandSender.sendMessage(ChatColor.YELLOW + "ItemRand" + ChatColor.WHITE + " :" + ChatColor.GREEN + " Successfully confirm that you read the warning " );
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return List.of();
    }
}
