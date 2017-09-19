package controllers


import java.io.ByteArrayInputStream
import java.nio.charset.Charset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

import akka.stream.scaladsl.{FileIO, Source, StreamConverters}
import akka.util.ByteString
import play.api.mvc.Controller
import play.modules.reactivemongo._
import models.{CD, Game}
import play.api._
import play.api.http.{HttpEntity, Writeable}
import play.api.libs.json.{JsString, JsValue}
import play.api.mvc._
import play.mvc.Http

/////////streaming http responses/////////////
import akka.stream.Materializer
import akka.stream.scaladsl.Source
import play.api.http.ContentTypes
import play.api.libs.Comet
import play.api.mvc._


import scala.concurrent.ExecutionContext.Implicits
import play.api.i18n.{I18nSupport, MessagesApi}





import java.io.{ByteArrayOutputStream, File}
import javax.imageio.ImageIO
import javax.inject.Inject

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64
import models.CD
import play.api.i18n.{I18nSupport, MessagesApi}

import akka.stream.Materializer
import play.api.http.ContentTypes
import play.api.libs.Comet
import play.api.mvc._

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import akka.stream.scaladsl.Source
import play.api.libs.json._

import scala.concurrent.duration._

class Application @Inject() (val messagesApi: MessagesApi) (materializer: Materializer) extends Controller with I18nSupport
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
    Ok(views.html.GameStoreHome(Game.RandomlyGenerateShowGame(), Game.createGameForm))
  }



  ////////////////////////////////////////////////////////Streaming HTTP Responses//////////////////////////////////////


  def streamWithContentLength = Action{
    val file = new java.io.File(".\\public\\temp\\fileToServe.pdf")
    val path: java.nio.file.Path = file.toPath
    val source: Source[ByteString, _] = FileIO.fromPath(path)

    val contentLength = Some(file.length())

    Result(
      header = ResponseHeader(200, Map.empty),
      body = HttpEntity.Streamed(source, contentLength , Some("application/pdf"))
    )
  }

  def serveFile = Action{
    Ok.sendFile(
      content = new java.io.File("C:\\Users\\Administrator\\IdeaProjects\\PlayExample\\public\\temp\\fileToServe.pdf"))
    //,fileName = => "day3Handout.pdf"
  }

  def chunked = Action
  {
    val string: String = "some string to use"
    val inputStream = new ByteArrayInputStream(string.getBytes(Charset.forName("UTF-8")))
    val data = inputStream
    val dataContent: Source[ByteString, _] = StreamConverters.fromInputStream(() => data)
    Ok.chunked(dataContent)
  }

  def chunkedFromSource = Action {
    val source = Source.apply(List("kiki", "foo", "bar"))
    Ok.chunked(source)
  }

  def gotochunk = Action
  {
    Ok(views.html.chunking())
  }
  def cometWithString = Action {
    implicit val m = materializer
    def stringSource: Source[String, _] = Source(List("kiki", "foo", "bar"))
    Ok.chunked(stringSource via Comet.string("parent.cometMessage")).as (ContentTypes.HTML)
  }

  def cometWithJson = Action{
    implicit val m = materializer
    def jsonSource: Source[JsValue, _] = Source(List(JsString("jsonString")))
    Ok.chunked(jsonSource via Comet.json("parent.cometMessage")).as(ContentTypes.HTML)
  }

  def displayClock() = Action{
    Ok(views.html.ClockHtml())
  }
  def streamClock() = Action {
    Ok.chunked(stringSource via Comet.string("parent.clockChanged")).as(ContentTypes.HTML)
  }

  def stringSource: Source[String, _] = {
    val df: DateTimeFormatter = DateTimeFormatter.ofPattern("HH mm ss")
    val tickSource = Source.tick(0 millis, 100 millis, "TICK")
    val s = tickSource.map((tick) => df.format(ZonedDateTime.now()))
    s
  }

}