
import akka._
import akka.actor.{ActorRef, ActorSystem, _}
import akka.stream._
import akka.stream.scaladsl._
import model.Tweet

import scala.concurrent._

object NegativeTrumpTweets {

  def main(args: Array[String]) {

    def readTweets(actor: ActorRef) = {
      TwitterStream.start(tweet => actor ! tweet)("en")("trump")
    }

    // Akka streams initialisation
    implicit val system: ActorSystem = ActorSystem("negative-trump-tweets")
    implicit val materializer: ActorMaterializer = ActorMaterializer()

    // Sources
    // Feed stream with messages sent to an actor
    val tweets: Source[Tweet, Unit] = Source
      .actorRef[Tweet](bufferSize = 1000, OverflowStrategy.dropHead)
      .mapMaterializedValue(readTweets)

    // Sinks
    val printTweet: Sink[Tweet, Future[Done]] = Sink.foreach[Tweet](println _)

    // Flows
    val getNegativeTweets = Fusing.aggressive(
      Flow[Tweet]
        .filter(_.body.toLowerCase.contains("hate"))
    )

    // Build graph (execution plan)
    val graph: RunnableGraph[Unit] = tweets
      .via(getNegativeTweets)
      .to(printTweet)

    // Run graph
    graph.run()
  }

}