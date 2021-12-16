package com.yiorno.antispam;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.apache.lucene.search.spell.LevensteinDistance;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class AntiSpam extends JavaPlugin implements Listener {

    Map<Player, String> map = new HashMap<>();
    ArrayList<Player> spam = new ArrayList<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
    
    @EventHandler
    public void onChat(AsyncPlayerChatEvent e){

        Player p = e.getPlayer();

        //挨拶は除外
        if( (e.getMessage().contains("こん")) || (e.getMessage().contains("hello"))
                || (e.getMessage().contains("hi")) ){
            return;
        }


        if(!(map.containsKey(e.getPlayer()))){
            map.put(p, e.getMessage());
            return;
        }

        String lastMsg = map.get(p);
        int score = getSimilarScore(e.getMessage(), lastMsg);


        if(score>80){

            if(!(spam.contains(p))){
                spam.add(p);
                return;
            }

            p.sendMessage(ChatColor.RED + "似た文は連続で送れません！");
            e.setCancelled(true);


        } else {

            map.put(p, e.getMessage());

            if(!(spam.contains(p))){
                return;
            }

            spam.remove(p);

        }
    }

    private static int getSimilarScore(String s1, String s2){
        LevensteinDistance dis =  new LevensteinDistance();
        return (int) (dis.getDistance(s1, s2) * 100);
    }

}
