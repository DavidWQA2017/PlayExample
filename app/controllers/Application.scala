package controllers


import javax.inject.Inject
import play.api.mvc.Controller
import play.modules.reactivemongo._


import models.{CD, Game}
import play.api._
import play.api.http.Writeable
import play.api.mvc._
import play.mvc.Http

import scala.concurrent.ExecutionContext.Implicits
import play.api.i18n.{I18nSupport, MessagesApi}

class Application @Inject() (val messagesApi: MessagesApi) extends Controller with I18nSupport
{

  def index = Action {implicit request =>
    Ok{request.flash.get("success").getOrElse("Welcome!")}
    //Ok(views.html.index("Your new application is ready."))
  }

  def dynamic (id: Long) = Action {
    Ok("hello i am a dynamic URI")
  }

  def show (page: String) = Action {
    Ok(page)
  }

  def optional (page: Int) = Action {
    Ok(page.toString)
  }

  def optional2 (page: Option[String]) = Action {
    Ok(page.toString)
  }

  def reverse(name: String) = Action {
    Ok("Hello " + name + "!")
  }

  def bob = Action {
    Redirect(routes.Application.reverse("bob"))
  }

  def redirect = Action {
    Redirect("https://www.google.co.uk")
  }

  def bingRedirect = Action
  {
    Redirect("https://www.bing.co.uk")
  }

  def bingRedirectMessage = Action
  {
    Ok("why would you do that?")
  }

  def aToDo = TODO

  def cookieCreate = Action
  {
    Ok("i am a single cookie").withCookies(Cookie("theme", "blue"))
  }

  def cookieDelete = Action
  {
    Ok("all the cookies are gone").discardingCookies((DiscardingCookie("theme")))
  }

  def changeCookieName = Action { implicit request =>
    val cookieVal = request.cookies.get("theme").get.value
    Ok("Discarded cookie").discardingCookies(DiscardingCookie("theme")).withCookies(Cookie("stuff" , cookieVal))
  }

  def newSession = Action { implicit request =>
    Ok("hello new user").withSession("connected" -> "user01")
  }

  def readSessionInfo = Action { implicit request =>
    request.session.get("connected").map{
    user => Ok("Hello " + user)
    }.getOrElse({Unauthorized("oh no, user is not connected")})
  }

  def discardSession = Action { implicit request =>
    request.session.get("connected").map{
      user => Ok("Bye " + user).withNewSession
    }.getOrElse({Unauthorized("oh no, user is not connected")})
  }

  def createFlashScope = Action { implicit request =>
    Redirect("/").flashing( "success" -> "A flash scope has been created")
  }

  ////////////////////////////////////////////////FORM SUBMISSION EXAMPLE CODE//////////////////////////////////////////

  def listCDs = Action { implicit request =>
    // we return a view file which expects two arguments passed to it
    // The first argument is the Seq[models.CD] which contains all the CDs we want to display
    // The second argument is the Form[models.CD], where we pass the form we have created
    Ok(views.html.listCDs(CD.cds, CD.createCDForm))
  }


  def createCD = Action { implicit request =>
    // we create a value to which we assign the form and bind the values that were submitted and are in the response
    val formValidationResult = CD.createCDForm.bindFromRequest
    // we then fold over the form where fold is a method that belongs to Form and what it does is
    // takes in two functions, where the first one has to handle the form with errors
    // and the second one has to handle the successful form submission
    // you could look at fold like this .fold({ Function for error}, { Function for success })
    // the first function of folding is the one where the data passed for the form was incorrect
    // therefore our form with the errors on it is binded to the formWithErrors
    // And we then return a BadRequest to signify that it wasn't correct and for the BadRequest we then pass
    // the same view file, as the view file requires an argument of Seq[models.CD] we pass that from the models.CD object
    // as well as the form with errors, where passing the form with errors is going to display the errors on the page
    formValidationResult.fold({ formWithErrors =>
      BadRequest(views.html.listCDs(CD.cds, formWithErrors))
    }, { cd =>
      cd
      // the second case is the good one were the data is of the correct type therefore the cd will hold the value
      // of models.CD, which we then add to the list of the CDs that we have inside the models.CD object
      // and lastly we return a Redirect response where we take the person to the same page but list all the CDs out
      // as we're using the reverse route of .listCDs, therefore after adding a models.CD and submitting the form
      // we will see the models.CD come up on the page
      CD.cds.append(cd)
      Redirect(routes.Application.listCDs)
    })
  }



  ////////////////////////////////////////////////////////Game Store Code///////////////////////////////////////////////
  def gameStoreHome = Action { implicit request =>
    // we return a view file which expects two arguments passed to it
    // The first argument is the Seq[models.CD] which contains all the CDs we want to display
    // The second argument is the Form[models.CD], where we pass the form we have created
    Ok(views.html.GameStoreHome(Game.Games, Game.createGameForm))
  }

}