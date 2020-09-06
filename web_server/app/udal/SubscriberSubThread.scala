package udal

import redis.clients.jedis.{Jedis, JedisPool}
import utils.ConfigLoader.{REDIS_HOST, REDIS_PORT}

class SubscriberSubThread(val webSocketActor: WebSocketActor) extends Thread{

  override def run(): Unit = {
    val pool = new JedisPool(REDIS_HOST, REDIS_PORT)
    val redis: Jedis = pool.getResource
    val subscriber = new RedisSubscriber(webSocketActor)
    redis.subscribe(subscriber, RedisKeyManager.dataNumChannel)
    redis.close()
  }

}
