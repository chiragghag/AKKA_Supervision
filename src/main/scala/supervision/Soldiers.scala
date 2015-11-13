package supervision

import akka.actor._
import akka.actor.SupervisorStrategy.{Escalate, Restart}
import scala.collection.immutable._
import scala.concurrent.forkjoin.ThreadLocalRandom
import Soldiers._
import scala.util.Random


object Soldiers {
  def props(sol:Int): Props =
    Props(classOf[Soldiers],sol)

  // Encodes the original position of a sub-expression in its parent expression
  // Example: (4 / 2) has position Left in the original expression (4 / 2) * 3
  trait String
  case object Win extends String
   case object Fight extends String
  case object Die extends String
  case object Continue extends String
  case class parentResult(sol:Int)
  case class MedicFight(sol:Int)

  class Death extends Exception
  class Continue extends Exception("continue")
  class Winner extends Exception("Winner")
}

// This actor has the sole purpose of calculating a given expression and
// return the result to its parent. It takes an additional argument,
// myPosition, which is used to signal the parent which side of its
// expression has been calculated.
class Soldiers(sol:Int)
  extends Actor with ActorLogging {

/*  override val supervisorStrategy = OneForOneStrategy(loggingEnabled = false) {
    case _: Continue =>
      log.warning("Soldier {} continues",sol)
      Restart
      case _: Death =>
        Escalate
  }*/

  // The value of these variables will be reinitialized after every restart.
  // The only stable data the actor has during restarts is those embedded in
  // the Props when it was created. In this case expr, and myPosition.
  var results  = Map.empty[String, Int]
  var expected = Set("Win", "Continue", "Die")
   var output = ""

  override def preStart(): Unit = sol match {
    case _ =>
      context.self ! Fight
  }
     //  log.warning("inside child for {}",sol)
 /*  val rnd=new Random
     output =expected.toVector(rnd.nextInt(expected.size))
 if(output.equalsIgnoreCase("Win"))        
         context.self ! Win
         else if(output.equalsIgnoreCase("Continue"))
         context.self ! Continue
         else
         context.self ! Die    
       
  }*/

  def receive = {
   /* case Die=>
     log.warning("soldier {} dead",sol)
     throw new Death
     case Continue=>
     log.warning("soldier {} injured",sol)
     throw new Continue
     case Win=>
     log.warning("soldier {} won",sol)
      context.parent ! parentResult(sol)*/
    // throw new Winner
      
      case Fight =>
       log.warning("soldier no.{} is fighting",sol)
   val rnd=new Random
     output =expected.toVector(rnd.nextInt(expected.size))
 if(output.equalsIgnoreCase("Win")) {
   log.warning("soldier no.{} wins and keeps fighting",sol)
     // context.parent ! parentResult(sol)
      context.self ! Fight
 }       
         
         else if(output.equalsIgnoreCase("Continue")){
        log.warning("soldier no.{} injured but can be saved :) ",sol)
     throw new Continue
     }
        // context.self ! Continue
         else
         {  log.warning("soldier no.{} badly injured, cant be saved :( ",sol)
           
     throw new Death
     }
         //context.self ! Die 
     
      
  
  }



}