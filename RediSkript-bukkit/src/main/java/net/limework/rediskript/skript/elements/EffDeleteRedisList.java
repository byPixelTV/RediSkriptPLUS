package net.limework.rediskript.skript.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.limework.rediskript.RediSkript;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Event;

public class EffDeleteRedisList extends Effect {
    static {
        Skript.registerEffect(EffDeleteRedisList.class, "delete redis (list|array) %string%");
    }


    private Expression<String> listKey;


    @Override
    protected void execute(Event event) {

        RediSkript plugin = RediSkript.getAPI();

        String listKey = this.listKey.getSingle(event);
        if (listKey == null) {
            Bukkit.getLogger().warning(ChatColor.translateAlternateColorCodes('&', "&2[&aRediSkript&a] &cRedis list key was empty. Please check your code."));
            return;
        }
        plugin.getRC().deleteList(listKey);
    }

    @Override
    public String toString(Event event, boolean debug) {
        return "delete redis list " + listKey.toString(event, debug);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(final Expression<?>[] expressions, final int matchedPattern, final Kleenean isDelayed, final SkriptParser.ParseResult parser) {
        this.listKey = (Expression<String>) expressions[0];
        return true;
    }

}