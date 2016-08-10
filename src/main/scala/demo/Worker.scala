package demo

import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import demo.BankService.{BalanceRequest, DepositRequest, OpenAccountRequest, TransferRequest}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.{Failure, Success}

/**
  * Created by sumeet on 09/08/16.
  */
object Worker extends App {
  implicit val timeout = Timeout(10 seconds)
  val system = ActorSystem("system")

  val bank = system.actorOf(Props[BankService], "bank")
  bank ! OpenAccountRequest("abc")
  bank ! OpenAccountRequest("pqr")
  bank ! DepositRequest("abc", 1000)
  bank ! DepositRequest("pqr", 500)


  (1 to 50).foreach(_ => {
    bank ! TransferRequest("abc", "pqr", 12)
  })

  (1 to 100).foreach(_ => {
    bank ! TransferRequest("pqr", "abc", 7)
  })



  Thread.sleep(100)
  val respafter = bank ? BalanceRequest("abc")


  respafter.onComplete({
    case Success(msg) => println("balance after abc " + msg)
    case Failure(msg) => println(msg)

  })
  val respafter1 = bank ? BalanceRequest("pqr")


  respafter1.onComplete({
    case Success(msg) => println("balance after pqr " + msg)
    case Failure(msg) => println(msg)

  })

}
