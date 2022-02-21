package com.yiorno.antispam;

import org.apache.lucene.search.spell.JaroWinklerDistance;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.apache.lucene.search.spell.LevensteinDistance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class AntiSpam extends JavaPlugin implements Listener {

    static Map<Player, String> map = new HashMap<>();
    static ArrayList<Player> spam = new ArrayList<>();
    String prefix = ChatColor.WHITE + "<" + ChatColor.AQUA +
            "もふちゃん" + ChatColor.WHITE + "> ";

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

        String msg = e.getMessage();

        //スパム判定
        if(isSpam(p, msg)) {

            p.sendMessage(ChatColor.RED + "似た文は連続で送れません！");
            e.setCancelled(true);
            return;

        }

        //チュートリアル判定
        if(isTraveler(p)){

            //挨拶は除外
            if(isGreeting(msg)){
                return;
            }

            //パスワードを聞いているか
            if(isAboutPass(msg)){

                p.sendMessage(prefix + "パスワードを聞くことはできません！！＾＾＃");
                getServer().getLogger().info(p.getName() + "がパスワードを聞こうとしました");
                e.setCancelled(true);

                for(Player staff : Bukkit.getOnlinePlayers()){

                    if(staff.hasPermission("mofucraft.staff")){
                        staff.sendMessage(prefix + p.getName() + " がパスワードを聞こうとしましたヨ！");
                    }

                }

                return;
            }

            p.sendMessage(prefix + "チュートリアルクリアまで挨拶以外のチャットをすることはできません！");
            e.setCancelled(true);
        }

    }

    private static boolean isSpam(Player p, String msg){

        //挨拶は除外
        if(isGreeting(msg)){
            return false;
        }


        if(!(map.containsKey(p))){
            map.put(p, msg);
            return false;
        }

        String lastMsg = map.get(p);
        int score = getSimilarScore2(msg, lastMsg);


        //スパム判定と加点
        if(score>80){

            //スパムリストにのっているかどうか
            if(!(spam.contains(p))){
                spam.add(p);
                return false;
            } else {
                return true;
            }

        } else {

            map.put(p, msg);

            if(!(spam.contains(p))){
                return false;
            }

            //スパムリストから削除
            spam.remove(p);
        }

        return false;
    }

    private static int getSimilarScore(String s1, String s2){

        LevensteinDistance dis =  new LevensteinDistance();
        return (int) (dis.getDistance(s1, s2) * 100);
    }

    private static int getSimilarScore2(String s1, String s2){

        JaroWinklerDistance dis =  new JaroWinklerDistance();
        return (int) (dis.getDistance(s1, s2) * 100);
    }


    public static boolean isTraveler(Player p){

        return (p.hasPermission("tutorial.yet")) && !(p.hasPermission("mofucraft.staff"));
    }

    public static boolean isAboutPass(String msg){

        return (msg.contains("pasu")) || (msg.contains("pass"))
                || (msg.contains("ぱす")) || (msg.contains("パス"));
    }

    public static boolean isGreeting(String msg){

        return ((msg.contains("こん")) || (msg.contains("hello"))
                || (msg.contains("hi")) || (msg.contains("yoro"))
                || (msg.contains("kon")) || (msg.contains("よろ"))
                || (msg.contains("oha")) || (msg.contains("おは"))) &&
                msg.length() < 15;
    }
}
