package net.limework.rediskript.skript.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import net.limework.rediskript.RediSkript;
import org.bukkit.event.Event;

import javax.annotation.Nullable;

public class ExprGetAllFieldValuesOfHash extends SimpleExpression<String> {

    static {
        Skript.registerExpression(ExprGetAllFieldValuesOfHash.class, String.class, ExpressionType.SIMPLE, "all field values of redis hash %string%");
    }

    private Expression<String> redisHashKey;

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        redisHashKey = (Expression<String>) exprs[0];
        return true;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "all field values of redis hash " + redisHashKey.toString(event, debug);
    }

    @Override
    @Nullable
    protected String[] get(Event event) {
        RediSkript plugin = RediSkript.getAPI();

        String redisHashName = redisHashKey.getSingle(event);
        if (redisHashName != null) {
            return plugin.getRC().getAllHashValues(redisHashName).toArray(new String[0]);
        }
        return null;
    }
}