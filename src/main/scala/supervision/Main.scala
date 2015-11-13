package supervision

import akka.actor.{Props, ActorSystem}
import akka.pattern.ask
import scala.concurrent.{Future, Await}
import scala.concurrent.duration._
import akka.util.Timeout

object Main {

  def main(args: Array[String]) {
    val system = ActorSystem("world-war-III")
    val calculatorService =
      system.actorOf(Props[Medic], "Doctor")

    def calculate(soldiers: Int): Future[String] = {
      implicit val timeout = Timeout(60.second)

      (calculatorService ? soldiers).mapTo[String]
    }

/*    // (3 + 5) / (2 * (1 + 1))
    val task = Divide(
      Add(Const(3), Const(5)),
      Add(
        Const(2),
        Add(Const(1), Const(1))
      )
    )*/
    
    var soldiers=2

    val result = Await.result(calculate(soldiers), 60.second)
    println(s"Got result: $result")

    system.shutdown()
    system.awaitTermination()
  }
}
