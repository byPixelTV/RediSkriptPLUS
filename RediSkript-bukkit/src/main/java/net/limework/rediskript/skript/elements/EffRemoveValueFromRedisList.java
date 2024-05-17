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

public class EffRemoveValueFromRedisList extends Effect {

    static {
        Skript.registerEffect(EffRemoveValueFromRedisList.class, "delete entry with index %number% from redis (list|array) %string%");
    }


    private Expression<Number> removeIndex;
    private Expression<String> listKey;


    @Override
    protected void execute(Event event) {

        RediSkript plugin = RediSkript.getAPI();

        Number removeIndexNumber = this.removeIndex.getSingle(event);
        if (removeIndexNumber == null) {
            Bukkit.getLogger().warning(ChatColor.translateAlternateColorCodes('&', "&2[&aRediSkript&a] &cRedis list index was empty. Please check your code."));
            return;
        }
        Integer removeIndex = removeIndexNumber.intValue();
        String listKey = this.listKey.getSingle(event);
        if (listKey == null) {
            Bukkit.getLogger().warning(ChatColor.translateAlternateColorCodes('&', "&2[&aRediSkript&a] &cRedis list key was empty. Please check your code."));
            return;
        }
        plugin.getRC().removeFromList(listKey, removeIndex);
    }

    @Override
    public String toString(Event event, boolean debug) {
        return "delete entry with index " + removeIndex.toString(event, debug) + " from redis list " + listKey.toString(event, debug);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(final Expression<?>[] expressions, final int matchedPattern, final Kleenean isDelayed, final SkriptParser.ParseResult parser) {
        this.removeIndex = (Expression<Number>) expressions[0];
        this.listKey = (Expression<String>) expressions[1];
        return true;
    }

}