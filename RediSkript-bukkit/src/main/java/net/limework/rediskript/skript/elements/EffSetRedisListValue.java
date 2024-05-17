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

public class EffSetRedisListValue extends Effect {

    static {
        Skript.registerEffect(EffSetRedisListValue.class, "set entry with index %number% in redis (list|array) %string% to %string%");
    }


    private Expression<Number> listIndex;
    private Expression<String> listKey;
    private Expression<String> listValue;


    @Override
    protected void execute(Event event) {

        RediSkript plugin = RediSkript.getAPI();

        Number listIndexNumber = this.listIndex.getSingle(event);
        if (listIndexNumber == null) {
            Bukkit.getLogger().warning(ChatColor.translateAlternateColorCodes('&', "&2[&aRediSkript&a] &cRedis list index was empty. Please check your code."));
            return;
        }
        Integer listIndex = listIndexNumber.intValue();
        String listKey = this.listKey.getSingle(event);
        String listValue = this.listValue.getSingle(event);
        if (listKey == null) {
            Bukkit.getLogger().warning(ChatColor.translateAlternateColorCodes('&', "&2[&aRediSkript&a] &cRedis list key was empty. Please check your code."));
            return;
        }
        plugin.getRC().setListValue(listKey, listIndex, listValue);
    }

    @Override
    public String toString(Event event, boolean debug) {
        return "set entry with index " + listIndex.toString(event, debug) + " in redis list " + listKey.toString(event, debug) + " to " + listValue.toString(event, debug);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(final Expression<?>[] expressions, final int matchedPattern, final Kleenean isDelayed, final SkriptParser.ParseResult parser) {
        this.listIndex = (Expression<Number>) expressions[0];
        this.listKey = (Expression<String>) expressions[1];
        this.listValue = (Expression<String>) expressions[2];
        return true;
    }

}