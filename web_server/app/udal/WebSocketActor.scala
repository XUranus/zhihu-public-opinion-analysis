package udal

import akka.actor._

object WebSocketActor {
  def props(out: ActorRef): Props = Props(new WebSocketActor(out))
}

class WebSocketActor(out: ActorRef) extends Actor {

  def receive: Receive = {
    case msg: String =>
      println("[WebSocket] GET "+msg)
      send(msg)
  }

  def send(content:String): Unit = {out ! content}

  override def preStart(): Unit = {
    new SubscriberSubThread(this).start()
    println("[WebSocket] Init")
  }

  override def postStop():Unit = {
    println("[WebSocket] Closed")
  }

}