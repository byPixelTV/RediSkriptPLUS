package net.limework.rediskript.skript.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import net.limework.rediskript.RediSkript;
import org.bukkit.event.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ExprGetValueOfFieldInHash extends SimpleExpression<String> {

    static {
        Skript.registerExpression(ExprGetValueOfFieldInHash.class, String.class, ExpressionType.SIMPLE, "value of field %string% in redis (hash|value) %string%");
    }

    private Expression<String> redisFieldName;
    private Expression<String> redisHashKey;

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        redisFieldName = (Expression<String>) exprs[0];
        redisHashKey = (Expression<String>) exprs[1];
        return true;
    }

    @Override
    @Nonnull
    public String toString(@Nullable Event event, boolean debug) {
        return "all field values of redis hash " + redisHashKey.toString(event, debug);
    }

    @Override
    protected String[] get(Event event) {
        RediSkript plugin = RediSkript.getAPI();

        String hashName = redisHashKey.getSingle(event);
        String fieldName = redisFieldName.getSingle(event);

        if (hashName == null) {
            return null;
        }
        if (fieldName == null) {
            return null;
        }
        String hashValue = plugin.getRC().getHashField(hashName, fieldName);
        if (hashValue != null) {
            return new String[]{hashValue};
        } else {
            return null;
        }
    }
}