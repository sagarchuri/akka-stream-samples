import akka.actor.{Actor, ActorSystem, Props}
import akka.stream._
import akka.stream.scaladsl._

object Main extends App {

  implicit val system = ActorSystem("QuickStart")
  implicit val materializer = ActorMaterializer()

  def flowToPrintln = {
    val source = Source(1 to 1000)

    val sink = Sink.foreach[Int](i => println(i))

    source.to(sink).run()
  }

  def flowToActor = {
    val source = Source(1 to 1000)

    val actor = system.actorOf(Props(new Actor {
      override def receive = {
        case msg => println(s"actor received: $msg")
      }
    }))

    val actorSink = Sink.actorRef(actor, "on stream completed");

    source to actorSink run
  }

  flowToActor
}
