import akka.NotUsed
import akka.actor.{Actor, ActorSystem, Props}
import akka.stream._
import akka.stream.scaladsl._

object Main extends App {

  implicit val system = ActorSystem("QuickStart")
  implicit val materializer = ActorMaterializer()

  private val source = Source(1 to 1000)

  def justPrint = {
    val sink = Sink.foreach[Int](i => println(i))

    source.to(sink)
  }

  def justPrintActor: RunnableGraph[NotUsed] = {
    val actor = system.actorOf(Props(new Actor {
      def receive = {
        case msg => println(s"actor received: $msg")
      }
    }))

    val actorSink = Sink.actorRef(actor, "on stream completed");

    source to actorSink
  }

  def mapToDouble: RunnableGraph[NotUsed] = {
    val doubleFlow = Flow[Int].map(i => i * 2)
    val sink = Sink.foreach[Int](i => println(i))

    source via doubleFlow to sink
  }

  mapToDouble run
}
