package net.limework.rediskript.managers;

import net.limework.rediskript.RediSkript;
import net.limework.rediskript.events.RedisMessageEvent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.scheduler.BukkitTask;
import org.json.JSONArray;
import org.json.JSONObject;
import redis.clients.jedis.BinaryJedisPubSub;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class RedisController extends BinaryJedisPubSub implements Runnable {


    //Jedis Pool to be used by every another class.
    private final JedisPool jedisPool;

    private byte[][] channelsInByte;

    private final AtomicBoolean isConnectionBroken;
    private final AtomicBoolean isConnecting;
    private final RediSkript plugin;
    private final BukkitTask ConnectionTask;


    public RedisController(RediSkript plugin) {
        this.plugin = plugin;
        Configuration config = plugin.getConfig();
        JedisPoolConfig JConfig = new JedisPoolConfig();
        int maxConnections = config.getInt("Redis.MaxConnections");

        //do not allow less than 2 max connections as that causes issues
        if (maxConnections < 2) {
            maxConnections = 2;
        }

        JConfig.setMaxTotal(maxConnections);
        JConfig.setMaxIdle(maxConnections);
        JConfig.setMinIdle(1);
        JConfig.setBlockWhenExhausted(true);
        final String password = config.getString("Redis.Password", "");
        if (password.isEmpty()) {
            this.jedisPool = new JedisPool(JConfig,
                    config.getString("Redis.Host", "127.0.0.1"),
                    config.getInt("Redis.Port", 6379),
                    config.getInt("Redis.TimeOut", 9000),
                    config.getBoolean("Redis.useTLS", false));
        } else {
            this.jedisPool = new JedisPool(JConfig,
                    config.getString("Redis.Host", "127.0.0.1"),
                    config.getInt("Redis.Port", 6379),
                    config.getInt("Redis.TimeOut", 9000),
                    password,
                    config.getBoolean("Redis.useTLS", false));
        }

        setupChannels(config);
        isConnectionBroken = new AtomicBoolean(true);
        isConnecting = new AtomicBoolean(false);
        //Start the main task on async thread
        ConnectionTask = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, this, 0, 20 * 5);
    }

    @Override
    public void run() {
        if (!isConnectionBroken.get() || isConnecting.get()) {
            return;
        }
        plugin.sendLogs("Connecting to Redis server...");
        isConnecting.set(true);
        try (Jedis jedis = jedisPool.getResource()) {
            isConnectionBroken.set(false);
            plugin.sendLogs("&aConnection to Redis server has established! Success!");
            jedis.subscribe(this, channelsInByte);
        } catch (Exception e) {
            isConnecting.set(false);
            isConnectionBroken.set(true);
            plugin.sendErrorLogs("Connection to Redis server has failed! Please check your details in the configuration.");
            e.printStackTrace();
        }
    }

    public void shutdown() {
        ConnectionTask.cancel();
        if (this.isSubscribed()) {
            try {
                this.unsubscribe();
            } catch (Exception e) {
                plugin.sendErrorLogs("Something went wrong during unsubscribing...");
                e.printStackTrace();
            }
        }
        jedisPool.close();
    }

    @Override
    public void onMessage(byte[] channel, byte[] message) {
        String channelString = new String(channel, StandardCharsets.UTF_8);
        String receivedMessage = null;
        try {
            receivedMessage = new String(message, StandardCharsets.UTF_8);
            // check if the msg starts with "{" and ends with "}"
            if (!receivedMessage.startsWith("{") || !receivedMessage.endsWith("}")) {
                RedisMessageEvent event;
                event = new RedisMessageEvent(channelString, receivedMessage, System.currentTimeMillis());
                if (plugin.isEnabled()) {
                    RedisMessageEvent finalEvent = event;
                    Bukkit.getScheduler().runTask(plugin, () -> plugin.getServer().getPluginManager().callEvent(finalEvent));
                }
            } else {
                JSONObject j = new JSONObject(receivedMessage);

                if (plugin.getConfig().getBoolean("RediVelocity.enabled")) {
                    if (j.get("event").equals("serverSwitch")) {
                        String messages = j.getString("target");
                        Long date = System.currentTimeMillis();
                        RedisMessageEvent event;
                        event = new RedisMessageEvent(channelString, "redisbungee:SERVER_CHANGE;" + messages, date);
                        event = new RedisMessageEvent(channelString, "redivelocity:serverSwitch;" + messages, date);
                        //if plugin is disabling, don't call events anymore
                        if (plugin.isEnabled()) {
                            RedisMessageEvent finalEvent = event;
                            Bukkit.getScheduler().runTask(plugin, () -> plugin.getServer().getPluginManager().callEvent(finalEvent));
                        }
                    } else if (j.get("action").equals("postLogin")) {
                        String messages = j.getString("target");
                        Long date = System.currentTimeMillis();
                        RedisMessageEvent event;
                        event = new RedisMessageEvent(channelString, "redisbungee:JOIN;" + messages, date);
                        event = new RedisMessageEvent(channelString, "redivelocity:postLogin;" + messages, date);
                        //if plugin is disabling, don't call events anymore
                        if (plugin.isEnabled()) {
                            RedisMessageEvent finalEvent = event;
                            Bukkit.getScheduler().runTask(plugin, () -> plugin.getServer().getPluginManager().callEvent(finalEvent));
                        }
                    } else if (j.get("action").equals("disconnect")) {
                        String messages = j.getString("target");
                        Long date = System.currentTimeMillis();
                        RedisMessageEvent event;
                        event = new RedisMessageEvent(channelString, "redisbungee:LEAVE;" + messages, date);
                        event = new RedisMessageEvent(channelString, "redivelocity:disconnect;" + messages, date);
                        //if plugin is disabling, don't call events anymore
                        if (plugin.isEnabled()) {
                            RedisMessageEvent finalEvent = event;
                            Bukkit.getScheduler().runTask(plugin, () -> plugin.getServer().getPluginManager().callEvent(finalEvent));
                        }
                    }

                    if (j.get("action").equals("Skript")) {
                        JSONArray messages = j.getJSONArray("Messages");
                        RedisMessageEvent event;
                        for (int i = 0; i < messages.length(); i++) {
                            event = new RedisMessageEvent(channelString, messages.get(i).toString(), j.getLong("Date"));
                            //if plugin is disabling, don't call events anymore
                            if (plugin.isEnabled()) {
                                RedisMessageEvent finalEvent = event;
                                Bukkit.getScheduler().runTask(plugin, () -> plugin.getServer().getPluginManager().callEvent(finalEvent));
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            plugin.sendErrorLogs("&cI got a message that was empty from channel " + channelString + " please check your code that you used to send the message. Message content:");
            plugin.sendErrorLogs(receivedMessage);
            e.printStackTrace();
        }
    }

    public void sendMessage(String[] message, String channel) {
        JSONObject json = new JSONObject();
        json.put("Messages", new JSONArray(message));
        json.put("action", "Skript");
        json.put("Date", System.currentTimeMillis()); //for unique string every time & PING calculations
        finishSendMessage(json, channel);
    }

    public void setHashField(String hashName, String fieldName, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            String type = jedis.type(hashName);
            if (!type.equals("hash")) {
                if (type.equals("none")) {
                    // The key doesn't exist, create a new hash
                    jedis.hset(hashName, fieldName, value);
                } else {
                    // The key exists but doesn't hold a hash, handle this situation
                    // For example, log an error message:
                    System.err.println("Error: Key " + hashName + " doesn't hold a hash. It holds a " + type + ".");
                    return;
                }
            } else {
                // The key exists and holds a hash, set the field to the value
                jedis.hset(hashName, fieldName, value);
            }
        }
    }

    // Delete a field in a Redis hash
    public void deleteHashField(String hashName, String fieldName) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.hdel(hashName, fieldName);
        }
    }

    // Delete a Redis hash
    public void deleteHash(String hashName) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(hashName);
        }
    }

    // Add a value to a Redis list
    public void addToList(String listName, String[] values) {
        try (Jedis jedis = jedisPool.getResource()) {
            for (String value : values) {
                jedis.rpush(listName, value);
            }
        }
    }

    // Set a value at an index in a Redis list
    public void setListValue(String listName, int index, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            long listLength = jedis.llen(listName);
            if (index >= listLength) {
                System.err.println("Error: Index " + index + " does not exist in the list " + listName + ".");
                return;
            }
            jedis.lset(listName, index, value);
        }
    }

    // Remove a value by its index from a Redis list
    public void removeFromList(String listName, int index) {
        try (Jedis jedis = jedisPool.getResource()) {
            long listLength = jedis.llen(listName);
            if (index >= listLength) {
                System.err.println("Error: Index " + index + " does not exist in the list " + listName + ".");
                return;
            }
            String tempKey = UUID.randomUUID().toString();
            jedis.lset(listName, index, tempKey);
            jedis.lrem(listName, 0, tempKey);
        }
    }

    // Delete a Redis list
    public void deleteList(String listName) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(listName);
        }
    }

    public void setString(String key, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(key, value);
        }
    }

    // Function to get a Redis string
    public String getString(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(key);
        }
    }

    // Function to delete a Redis string
    public void deleteString(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(key);
        }
    }

    public String getHashField(String hashName, String fieldName) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hget(hashName, fieldName);
        }
    }

    public Set<String> getAllHashFields(String hashName) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hkeys(hashName);
        }
    }

    public List<String> getAllHashValues(String hashName) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hvals(hashName);
        }
    }

    public List<String> getList(String listName) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.lrange(listName, 0, -1);
        }
    }

    // Function to get the field names by its value in a Redis hash
    public List<String> getHashFieldNamesByValue(String hashName, String value) {
        List<String> fieldNames = new ArrayList<>();
        try (Jedis jedis = jedisPool.getResource()) {
            Set<String> keys = jedis.keys(hashName);
            for (String key : keys) {
                Map<String, String> fieldsAndValues = jedis.hgetAll(key);
                for (Map.Entry<String, String> entry : fieldsAndValues.entrySet()) {
                    if (entry.getValue().equals(value)) {
                        fieldNames.add(entry.getKey());
                    }
                }
            }
        }
        return fieldNames;
    }

    public void finishSendMessage(JSONObject json, String channel) {
        try {
            byte[] message;
            message = json.toString().getBytes(StandardCharsets.UTF_8);

            //sending a redis message blocks main thread if there's no more connections available
            //so to avoid issues, it's best to do it always on separate thread
            if (plugin.isEnabled()) {
                plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                    try (Jedis j = jedisPool.getResource()) {
                        j.publish(channel.getBytes(StandardCharsets.UTF_8), message);
                    } catch (Exception e) {
                        plugin.sendErrorLogs("Error sending redis message!");
                        e.printStackTrace();
                    }
                });
            } else {
                //execute sending of redis message on the main thread if plugin is disabling
                //so it can still process the sending
                try (Jedis j = jedisPool.getResource()) {
                    j.publish(channel.getBytes(StandardCharsets.UTF_8), message);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        } catch (JedisConnectionException exception) {
            exception.printStackTrace();
        }
    }

    private void setupChannels(Configuration config) {
        List<String> channels = config.getStringList("Channels");
        channelsInByte = new byte[channels.size()][1];
        for (int x = 0; x < channels.size(); x++) {
            channelsInByte[x] = channels.get(x).getBytes(StandardCharsets.UTF_8);
        }
    }

    public Boolean isRedisConnectionOffline() {
        return isConnectionBroken.get();
    }

    // for skript reflect :)
    public JedisPool getJedisPool() {
        return jedisPool;
    }
    //
}
