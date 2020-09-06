package utils

import redis.clients.jedis.JedisPubSub

class Subscriber extends JedisPubSub{

  override def onMessage(channel: String, message: String): Unit = {
    println("onMessage "+channel+" "+message)
  }

  override def onSubscribe(channel: String, subscribedChannels: Int): Unit = {
    println("onSubscribe "+channel+" "+subscribedChannels)
  }

  override def onUnsubscribe(channel: String, subscribedChannels: Int): Unit = {
    println("onUnsubscribe "+channel+" "+subscribedChannels)
  }


}
