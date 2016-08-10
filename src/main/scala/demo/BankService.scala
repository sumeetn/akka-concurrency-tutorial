package demo

import akka.actor.{Props, AbstractLoggingActor}
import demo.AccountOperation._
import demo.BankService._

/**
  * Created by sumeet on 09/08/16.
  */
trait Command

trait State

object BankService {

  case class WithdrawRequest(accountNumber: String, amount: Int) extends Command

  case class DepositRequest(accountNumber: String, amount: Int) extends Command

  case class TransferRequest(fromAccount: String, toAccount: String, amount: Int) extends Command

  case class BalanceRequest(accountNumber: String) extends Command

  case class OpenAccountRequest(accountNumber: String) extends Command


  def props: Props = {
    Props(new BankService)
  }

}

class BankService extends AbstractLoggingActor {

  override def receive = {
    case WithdrawRequest(accountNumber: String, amount: Int) => processCommand(accountNumber, Withdraw(amount))
    case DepositRequest(accountNumber: String, amount: Int) => processCommand(accountNumber, Deposit(amount))
    case BalanceRequest(accountNumber: String) => processCommand(accountNumber, AccountBalance)
    case OpenAccountRequest(accountNumber: String) => processCommand(accountNumber, Create)
    case TransferRequest(from:String,to:String,amt:Int)=> processCommand(from,Transfer(to,amt))
  }

  def processCommand(accountNumber: String, command: Command) = {

    val actorRef = context.child(accountNumber)

    actorRef match {
      case Some(actor) => actor forward command

      case None => {
        val childActor = context.actorOf(AccountOperation.props(accountNumber), accountNumber)
        childActor forward command
      }
    }

  }
}