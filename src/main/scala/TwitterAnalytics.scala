
import akka._
import akka.actor.{ActorRef, ActorSystem, _}
import akka.stream._
import akka.stream.scaladsl._
import model.Tweet

import scala.concurrent._

object TwitterAnalytics {

  def main(args: Array[String]) {

    def pushTweets(actor: ActorRef) = {
      TwitterStream.start(tweet => actor ! tweet)("en")("trump")
    }

    // Akka streams initialisation
    implicit val system: ActorSystem = ActorSystem("reactive-twitter")
    implicit val materializer: ActorMaterializer = ActorMaterializer()

    // Sources
    // Feed stream with messages sent to an actor
    val tweets: Source[Tweet, Unit] = Source
      .actorRef[Tweet](bufferSize = 1000, OverflowStrategy.dropHead)
      .mapMaterializedValue(pushTweets)

    // Sinks
    val printTweet: Sink[Tweet, Future[Done]] = Sink.foreach[Tweet](println _)

    val printStr: Sink[String, Future[Done]] = Sink.foreach[String](println _)

    val countTweets: Sink[Int, Future[Int]] = Sink.fold[Int, Int](0)(_ + _)

    // Flows
    val getNegativeTweets = Fusing.aggressive(
      Flow[Tweet]
        .filter(_.body.toLowerCase.contains("hate"))
    )

    // Build graph (execution plan)
    val graph: RunnableGraph[Unit] = tweets
      .via(getNegativeTweets)
      .to(printTweet)
    //      .map(_ => 1)
    //      .toMat(countTweets)(Keep.right)


    // Run graph
    // and await how many times hate is mentioned in trump tweets
    graph.run()
    //      .onComplete( hateful => hateful match {
    //      case Success(r) => println(r)
    //      case Failure(t) => println(t)
    //    })

    val g: RunnableGraph[NotUsed] = RunnableGraph.fromGraph(GraphDSL.create() {
      implicit builder: GraphDSL.Builder[NotUsed] =>

        import GraphDSL.Implicits._
        val in = Source.single(1)
        val out = Sink.foreach(println _)

        val bcast = builder.add(Broadcast[Int](2))
        val merge = builder.add(Merge[Int](2))

        val f1, f2, f3, f4 = Flow[Int].map(_ + 10)

        in ~> f1 ~> bcast ~> f2 ~> merge ~> f3 ~> out
        bcast ~> f4 ~> merge

        ClosedShape
    })
  }

}