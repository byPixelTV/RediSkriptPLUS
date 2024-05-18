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

public class EffSetRedisString extends Effect {

    static {
        Skript.registerEffect(EffSetRedisListValue.class, "set redis string %string% to %string%");
    }


    private Expression<String> stringName;
    private Expression<String> stringValue;


    @Override
    protected void execute(Event event) {

        RediSkript plugin = RediSkript.getAPI();

        String name = this.stringName.getSingle(event);
        if (name == null) {
            Bukkit.getLogger().warning(ChatColor.translateAlternateColorCodes('&', "&2[&aRediSkript&a] &cRedis string name was empty. Please check your code."));
            return;
        }
        String value = this.stringValue.getSingle(event);
        if (value == null) {
            Bukkit.getLogger().warning(ChatColor.translateAlternateColorCodes('&', "&2[&aRediSkript&a] &cRedis string value was empty. Please check your code."));
            return;
        }
        plugin.getRC().setString(name, value);
    }

    @Override
    public String toString(Event event, boolean debug) {
        return "set redis string " + stringName.toString(event, debug) + " to " + stringValue.toString(event, debug);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(final Expression<?>[] expressions, final int matchedPattern, final Kleenean isDelayed, final SkriptParser.ParseResult parser) {
        this.stringName = (Expression<String>) expressions[0];
        this.stringValue = (Expression<String>) expressions[1];
        return true;
    }

}