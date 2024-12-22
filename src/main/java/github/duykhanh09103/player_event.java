package github.duykhanh09103;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;


public class player_event implements Listener {

    //for player left(delete the key so it can proper register next time)
    @EventHandler
    public void onPLayerQuit(PlayerQuitEvent event){
        Map<String, Boolean> map = randomizer_command.map;
        Player player = event.getPlayer();
        map.remove(player.getName());
    };



}
