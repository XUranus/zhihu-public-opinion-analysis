package udal

import redis.clients.jedis.JedisPubSub

class RedisSubscriber(val webSocketActor: WebSocketActor) extends JedisPubSub{

  override def onMessage(channel: String, message: String): Unit = {
    println("Redis onMessage "+channel+" "+message)
    webSocketActor.send(message)
  }

  override def onSubscribe(channel: String, subscribedChannels: Int): Unit = {
    println("Redis onSubscribe "+channel+" "+subscribedChannels)
  }

  override def onUnsubscribe(channel: String, subscribedChannels: Int): Unit = {
    println("Redis onUnsubscribe "+channel+" "+subscribedChannels)
  }


}
