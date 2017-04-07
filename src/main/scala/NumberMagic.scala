
import akka._
import akka.actor.{ActorRef, ActorSystem, _}
import akka.stream._
import akka.stream.scaladsl._
import model.Tweet

import scala.concurrent._

object NumberMagic {

  def main(args: Array[String]) {

    // Akka streams initialisation
    implicit val system: ActorSystem = ActorSystem("number-magic")
    implicit val materializer: ActorMaterializer = ActorMaterializer()

    // Sources
    val in = Source.single(1)

    // Sinks
    val out = Sink.foreach(println _)

    // Manual graph
    val g: RunnableGraph[NotUsed] = RunnableGraph.fromGraph(GraphDSL.create() {
      implicit builder: GraphDSL.Builder[NotUsed] =>
        import GraphDSL.Implicits._

        val bcast = builder.add(Broadcast[Int](2))
        val merge = builder.add(Merge[Int](2))

        val doubler = Flow[Int].map(_ * 2)
        val tripler = Flow[Int].map(_ * 3)
        val addOne = Flow[Int].map(_ + 1)

        in ~> addOne ~> bcast // fan out
        bcast ~> doubler ~> merge
        bcast ~> tripler ~> merge
        merge ~> addOne ~> out // fan in

        ClosedShape
    })

    g.run()
  }

}