package supervision

import akka.actor.SupervisorStrategy.{Restart, Stop}
import akka.actor.{ActorRef, ActorLogging, Actor, OneForOneStrategy, Status}

// A very simple service that accepts arithmetic expressions and tries to
// evaluate them. Since the calculation is dangerous (at least for the sake
// of this example) it is delegated to a worker actor of type
// FlakyExpressionCalculator.
class Medic extends Actor with ActorLogging {
   var output = ""
   var expected = Set("Win", "Continue", "Die")
  import Soldiers.Continue
   import Soldiers.Death
    import Soldiers.Winner
  import Soldiers.parentResult
  import Soldiers.MedicFight
  import scala.util.Random

  // Map of workers to the original actors requesting the calculation
  var pendingWorkers = Map[ActorRef, ActorRef]()

  override val supervisorStrategy = OneForOneStrategy(loggingEnabled = false) {
    case _: Continue  =>
      log.warning("Medic healed a soldier")
      Restart
    case _: Death =>
      log.error("Medic killed a soldier")
      var res=1
      notifyConsumerSuccess(worker = sender,result = res)
      Stop
    /*   case _: Winner =>
      log.error("soldier Won")
      notifyConsumerFailure(worker = sender,failure = e)
      Restart*/
    case e =>
      log.error("Unexpected failure: {}")
      notifyConsumerFailure(worker = sender,failure = e)
      Stop
  }

  def notifyConsumerFailure(worker: ActorRef, failure: Throwable): Unit = {
    // Status.Failure is a message type provided by the Akka library. The
    // reason why it is used because it is recognized by the "ask" pattern
    // and the Future returned by ask will fail with the provided exception.
    pendingWorkers.get(worker) foreach { _ ! Status.Failure(failure) }
    pendingWorkers -= worker
  }

  def notifyConsumerSuccess(worker: ActorRef, result: Int): Unit = {
    pendingWorkers.get(worker) foreach { _ ! "success" }
    pendingWorkers -= worker
  }

  def receive = {
    case e: Int =>
      // We delegate the dangerous task of calculation to a worker, passing the
      // expression as a constructor argument to the actor.
      var b=0;
      for( b <- 1 to e){
      log.warning("soldier no {} deployed",b)
      val worker = context.actorOf(Soldiers.props(b))
    //  worker ! Soldiers.Fight
      pendingWorkers += worker -> sender
      }
   //  context.self ! MedicFight
    case parentResult(sol:Int) =>
      log.warning("soldier {} won so stopped by parent",sol)
      notifyConsumerSuccess(worker = sender, result = sol)
      
    case MedicFight =>
      
       log.warning("medic is fighting")
   val rnd=new Random
     output =expected.toVector(rnd.nextInt(expected.size))
 if(output.equalsIgnoreCase("Win")) {
   log.warning("medic wins and keeps fighting")
     // context.parent ! parentResult(sol)
      context.self ! MedicFight
 }       
         
         else if(output.equalsIgnoreCase("Continue")){
        log.warning("medic injured but can be saved :) ")
     throw new Continue
     }
        // context.self ! Continue
         else
         {  log.warning("medic badly injured, cant be saved :( ")
           
     throw new Death
     }
          
          
  }

}