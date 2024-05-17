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

import javax.annotation.Nullable;

public class EffAddValueToRedisList extends Effect {
    static {
        Skript.registerEffect(EffAddValueToRedisList.class, "add %strings% to redis (list|array) %string%");
    }

    private Expression<String> addValues;
    private Expression<String> listKey;


    @Override
    protected void execute(Event event) {

        RediSkript plugin = RediSkript.getAPI();

        String[] addValues = this.addValues.getAll(event);
        if (addValues[0] == null) {
            addValues = new String[]{" "};
        }
        String listKey = this.listKey.getSingle(event);
        if (listKey == null) {
            Bukkit.getLogger().warning(ChatColor.translateAlternateColorCodes('&', "&2[&aRediSkript&a] &cRedis list key was empty. Please check your code."));
            return;
        }
        plugin.getRC().addToList(listKey, addValues);
    }

    @Override
    public String toString(Event event, boolean debug) {
        return "add " + addValues.toString(event, debug) + " to redis list " + listKey.toString(event, debug);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(final Expression<?>[] expressions, final int matchedPattern, final Kleenean isDelayed, final SkriptParser.ParseResult parser) {
        this.addValues = (Expression<String>) expressions[0];
        this.listKey = (Expression<String>) expressions[1];
        return true;
    }

}