
import akka._
import akka.actor.{ActorRef, ActorSystem, _}
import akka.stream._
import akka.stream.scaladsl._

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

  //  Source(1 to 10).via(Flow[Int].map(_*10)).to(Sink.foreach(println(_))).run()

    //basic transformation
    /*
     val text =
      """|Lorem Ipsum is simply dummy text of the printing and typesetting industry.
         |Lorem Ipsum has been the industry's standard dummy text ever since the 1500s,
         |when an unknown printer took a galley of type and scrambled it to make a type
         |specimen book.""".stripMargin

    Source.fromIterator(() => text.split("\\s").iterator).
      map(_.toUpperCase).
      runForeach(println).
      onComplete(_ => system.shutdown())
     */
  }

}