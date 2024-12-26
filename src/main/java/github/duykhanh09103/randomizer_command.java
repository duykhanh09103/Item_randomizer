package github.duykhanh09103;

import net.md_5.bungee.api.chat.TranslatableComponent;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;


public class randomizer_command implements CommandExecutor, TabExecutor {

    private final item_randomizer plugin ;
    public static boolean isTaskRunning = false;
    BossBar bossBar = Bukkit.createBossBar("RandItem", BarColor.YELLOW, BarStyle.SEGMENTED_10);
    public randomizer_command(item_randomizer plugin) {
        this.plugin = plugin;
    }
    public static Map<String, Boolean> map = new HashMap<>();
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
     if(sender instanceof Player) {
         Player player = (Player) sender;
         String[] allCommand = {"start", "stop", "setTimer", "help"};
         Bukkit.getLogger().info("command:" +command +" label: "+label + " args:" + Arrays.toString(args));
         if (args.length == 0 ||Arrays.stream(allCommand).noneMatch(args[0]::contains)) {
             player.sendMessage(ChatColor.YELLOW + "ItemRand" + ChatColor.WHITE + " :" + " Wrong usage! use /ItemRand help to show list of usage!");
             return true;
         };
         if(args[0].equalsIgnoreCase("help")){
             player.sendMessage(ChatColor.YELLOW + "ItemRand" + ChatColor.WHITE +" :"+"This plugin have 4 command in total!"+"\n"+"/ItemRand help : to display this message!"+"\n"+"/ItemRand start : to start the timer and bossbar for randomize items!"+"\n"+"/ItemRand stop : to stop ofc bro what do you expect?"+"\n"+"/ItemRand setTimer <time>: to set the time for how long (in seconds) until the next item ! Default is 10s");
         return true;
         }
         if(args[0].equalsIgnoreCase("start")){
             map.clear();
             int timer = (int) plugin.config.get("Timer");
             if(timer<1){
                 player.sendMessage(ChatColor.YELLOW + "ItemRand" + ChatColor.WHITE +" :"+ChatColor.RED+"Error: The timer cant be lower than 1! current timer: "+timer);
                 return true;
             }

             if(isTaskRunning){
                 player.sendMessage(ChatColor.YELLOW + "ItemRand" + ChatColor.WHITE +" :"+ChatColor.RED+"Error: The items randomizer is already running! please stop it first");
             return true;
             }
             if(randomGameWorld_command.isRunning){
                 player.sendMessage(ChatColor.YELLOW + "ItemRand" + ChatColor.WHITE +" :"+ChatColor.RED+"Error: The Random Game World is running! please wait until the game is dont or force stop it!");
                 return true;
             }
             bossBar.setProgress(1.0);
             isTaskRunning=true;

             for(Player allplayer:Bukkit.getOnlinePlayers()){
                 bossBar.addPlayer(allplayer);
                 player.sendMessage(ChatColor.YELLOW + "ItemRand" + ChatColor.WHITE +" :"+"Starting! The delay time is " +timer+"s");
                 map.put(player.getName(),true);
             }
             new BukkitRunnable(){
                 int countdown = timer;
                 @Override
                 public void run() {
                          if(countdown>0){
                              for(Player allplayer:Bukkit.getOnlinePlayers()){
                                  if(!map.containsKey(allplayer.getName())){
                                      bossBar.addPlayer(allplayer);
                                  }
                              }
                              bossBar.setTitle("Next Items: "+countdown+"s");
                              bossBar.setProgress((double) countdown / timer);
                              countdown--;
                          }
                          else {
                                  for (Player player : Bukkit.getOnlinePlayers()) {
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
           return true;
         }
         if(args[0].equalsIgnoreCase("stop")){
             if(randomGameWorld_command.isRunning){
                 player.sendMessage(ChatColor.YELLOW + "ItemRand" + ChatColor.WHITE +" :"+ChatColor.RED+"Error: Random Game World is running! ");
                 return true;
             }
             if(!isTaskRunning){
                 player.sendMessage(ChatColor.YELLOW + "ItemRand" + ChatColor.WHITE +" :"+ChatColor.RED+"Error: Nothing running right now! ");
             return true;
             }
             Bukkit.getScheduler().cancelTasks(plugin);
             bossBar.removeAll();
             map.clear();
             for(Player allplayer:Bukkit.getOnlinePlayers()){
                 allplayer.sendMessage(ChatColor.YELLOW + "ItemRand" + ChatColor.WHITE + " :"+ChatColor.GREEN+" Successfully stopping !");
             }
             isTaskRunning=false;
             return true;
         }
         if(args[0].equalsIgnoreCase("setTimer")){
             if (args.length < 2) {
                 player.sendMessage(ChatColor.YELLOW + "ItemRand" + ChatColor.WHITE + " :" + ChatColor.RED + " Error: No time value provided!");
                 return true;
             }

             String timeStr = args[1];
             if (!StringUtils.isNumeric(timeStr)) {
                 player.sendMessage(ChatColor.YELLOW + "ItemRand" + ChatColor.WHITE + " :" + ChatColor.RED + " Error: The time must be a number!");
                 return true;
             }

             int newTimer = Integer.parseInt(timeStr);
             if (newTimer < 1) {
                 player.sendMessage(ChatColor.YELLOW + "ItemRand" + ChatColor.WHITE + " :" + ChatColor.RED + " Error: The time must be greater than 0! You provided: " + newTimer);
                 return true;
             }

             plugin.getConfig().set("Timer", newTimer);
             plugin.saveConfig();
             player.sendMessage(ChatColor.YELLOW + "ItemRand" + ChatColor.WHITE + " :" + ChatColor.GREEN + " Successfully set the timer to " + ChatColor.WHITE + newTimer + " seconds!");
         }

     }
     else {
         Bukkit.getServer().getLogger().info("[ItemRand] Server/console cannot use command!");
     }

     return true;

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("start", "stop", "setTimer", "help"); // Provide options for the first argument
        }
        return new ArrayList<>(); // No further suggestions
    }
}
