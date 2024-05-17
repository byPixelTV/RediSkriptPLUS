

## RediSkript 
Allows you to communicate between your Minecraft servers with use of Redis and Skript, it's very fast and easy to use.

Skript: https://github.com/SkriptLang/Skript

RediSkript spigot page: https://www.spigotmc.org/resources/rediskript-communicate-between-servers-with-ease.85067/s

Minecraft server version supported: **1.8.8+** (On 1.8, may need to use a fork of Skript)

You can transfer any data in the form of text between your servers, you can program it to execute a set of instructions on the server depending on the redis message, etc. This can be used for making scripts that sync data between all servers and much more!

It is developed and maintained by Govindas & the team of Govindas Limework developers.

There is only one command: **/reloadredis** it fully reloads the configuration, you can reload IP, password, channels and everything else.

You only need to have matching configuration in every server for communication and a Redis server to connect to. I recommend using a VPS for hosting redis server, but there also are free redis hosting options available.

### Redis Message
```
on redis message:
  if redis channel is "world":
    broadcast "%redis message% %redis channel% %redis message date%"

command /sendredis <text> <text>:
  usage: /sendredis <message> <channel>
  trigger:
    send redis message arg 1 to channel arg 2
    send redis message "hello world!" to channel "world"
```
### Managing variables
```
set variables "test::1", "test::2", "test::3" in channel "global" to 100
#then use this in any server that listens to "global" redis channel and was online when the above line was executed:
send "%{test::*}%" #outputs 100, 100 and 100

add 100 to variables "test::1" and "test::2" in channel "global"
remove 10 from variable "test::1" in channel "global"
delete variables "test::*" in channel "global"

set variable "test::%uuid of player%" in channel "playerdata" to tool of player
#then you can in any server that is listening to "playerdata" channel and was online when the above line was executed:
give {test::%uuid of player%} to player
```
Syntax:
```
variable[s] %strings% in [redis] [channel] %string%
```

### Configuration
plugins/RediSkript/config.yml
```
Redis:
  #a secure password that cannot be cracked, please change it!
  #it is also recommended to firewall your redis server with iptables so it can only be accessed by specific IP addresses
  Password: "yHy0d2zdBlRmaSPj3CiBwEv5V3XxBTLTrCsGW7ntBnzhfxPxXJS6Q1aTtR6DSfAtCZr2VxWnsungXHTcF94a4bsWEpGAvjL9XMU"
  #hostname of your redis server, you can use free redis hosting (search for it online) if you do not have the ability to host your own redis server
  #redis server is very lightweight, takes under 30 MB of RAM usually
  Host: "127.0.0.1"
  #must be 2 or higher, if you set to lower, the addon will automatically use 2 as a minimum
  #do not edit MaxConnections if you do not know what you're doing
  #it is only useful to increase this number to account for PING between distant servers and when you are sending a lot of messages constantly
  MaxConnections: 2
  #the default Redis port
  Port: 6379
  #time out in milliseconds, how long it should take before it decides that it is unable to connect when sending a message
  #9000 = 9 seconds
  TimeOut: 9000
  #also known as SSL, only use this if you're running Redis 6.0.6 or higher, older versions will not work correctly
  #it encrypts your traffic and makes data exchange between distant servers secure
  useTLS: false
  #EncryptMessages may be useful if you cannot use TLS due to use of older version of Redis or if you're paranoid about privacy and want to double encrypt your messages
  #however this will not encrypt the initial authentication password, only the messages sent (use TLS for initial authentication password encryption)

  #the encryption configuration must be the same across all servers in order to communicate

  #use 16 characters long key for AES-128 encryption
  #32 characters long key for AES-256 encryption (recommended)
  #the AES implementation used in RediSkript uses SIV mode, which makes the same key resistant to cracking for a big count of messages without the need of changing the key very often
  EncryptMessages: true
  #EncryptionKey and MacKey must be different
  EncryptionKey: "32CHARACTERS KEY"
  MacKey: "32CHARACTERS KEY"


#the channels from which this server can receive messages
#you can always send messages to all channels!
#you can add as many channels as you wish!

#ideal setup is having one global channel and having one channel that represents server name, so you know who to send messages to
#then a few other utility channels up to your needs
Channels:
  - "global"
  - "servername"
  - "Channel3"
  ```

## YourKit

YourKit supports open source projects with innovative and intelligent tools for monitoring and profiling Java and .NET applications. YourKit is the creator of [YourKit Java Profiler](https://www.yourkit.com/java/profiler/), [YourKit .NET Profiler](https://www.yourkit.com/.net/profiler/) and [YourKit YouMonitor](https://www.yourkit.com/youmonitor/).

![YourKit](https://www.yourkit.com/images/yklogo.png)
