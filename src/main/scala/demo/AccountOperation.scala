package demo

import akka.actor.{AbstractLoggingActor, Props}
import akka.pattern.ask
import akka.util.Timeout
import demo.AccountOperation._
import demo.BankService.{WithdrawRequest, DepositRequest}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.{Failure, Success}

/**
  * Created by sumeet on 09/08/16.
  */
object AccountOperation {

  case class Balance(amount: Int) extends State

  object AccountBalance extends Command

  case class Withdraw(amount: Int) extends Command

  case class Transfer(toAccount: String, amount: Int) extends Command

  case class Deposit(amount: Int) extends Command

  object Create extends Command


  def props(id: String): Props = {
    Props(new AccountOperation(id))
  }

}

class AccountOperation(accountNumber: String) extends AbstractLoggingActor {

  protected var state: State = Balance(0)

  val created: Receive = {
    case Deposit(amount: Int) => {
      val existingBalance = state.asInstanceOf[Balance].amount
      state = state.asInstanceOf[Balance].copy(amount = existingBalance + amount)
      sender() ! "success"
    }
    case Withdraw(amount: Int) => {
      val existingBalance = state.asInstanceOf[Balance].amount
      if (existingBalance > amount) {
        state = state.asInstanceOf[Balance].copy(amount = (existingBalance - amount))
        sender() ! "success"
      }
      else sender() ! "failed"
    }

    case AccountBalance => {
      println(accountNumber + " =>  " + state.asInstanceOf[Balance].amount.toString)
      sender() ! state.asInstanceOf[Balance].amount.toString
    }

    case Transfer(destAccount: String, amount: Int) => {
      implicit val timeout = Timeout(2 seconds)
      val temp = context.actorSelection("/user/bank")
      val actRef = sender()
      println(temp)
      val status = (temp ? WithdrawRequest(accountNumber, amount)).mapTo[String]
      status.onComplete({
        case Success("success") => {

          val bal = (temp ? DepositRequest(destAccount, amount)).mapTo[String]

          bal.onComplete({
            case Success(msg) => {

              actRef ! "transfer success"
            }
            case Failure(msg) => sender() ! "transfer fail"
          })
        }
        case Failure(msg) => println("withdraw failed")
      })
    }

    case _ => println("case not found in created")
  }

  override def receive = {
    case Create => state = Balance(0); sender() ! "success"; context.become(created)
  }

}
