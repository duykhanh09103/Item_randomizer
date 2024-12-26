package github.duykhanh09103;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.Optional;


public class player_event implements Listener {

    private final item_randomizer plugin ;

    public player_event(item_randomizer plugin) {
        this.plugin = plugin;
    }

    //for player left(delete the key so it can proper register next time)
    @EventHandler
    public void onPLayerQuit(PlayerQuitEvent event){
        Map<Player,Boolean> playerState = randomGameWorld_command.playerState;
        Map<String, Boolean> map = randomizer_command.map;
        Player player = event.getPlayer();
        if(map.containsKey(player)){
            map.remove(player.getName());
            return;
        }
        if(playerState.containsKey(player)){
            playerState.remove(player);
            Location worldSpawn = Bukkit.getWorld("world").getSpawnLocation();
            player.teleport(worldSpawn);
        }

    };

    @EventHandler
    public void onPlayerDead(PlayerDeathEvent event){
        Player player = event.getEntity().getPlayer();
        Map<Player,String> map = randomGameWorld_command.playingPlayer;
        Map<Player,Boolean> playerState = randomGameWorld_command.playerState;
        if(!map.containsKey(player)){
          return;
        }
        if(playerState.size() == 1){
            return;
        }
        //delay the event for 1 sec cuz red screen
        new BukkitRunnable(){
            @Override
            public void run(){
                player.spigot().respawn();
                playerState.remove(player);
                player.setGameMode(GameMode.SPECTATOR);
                Optional<Player> alivePlayer = playerState.keySet().stream().findFirst();
                if(alivePlayer.isPresent()){
                    player.teleport(alivePlayer.get());
                }
            }
        }.runTaskLater(plugin,20L);



    };



}
