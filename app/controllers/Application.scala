package controllers

import play.api._
import play.api.http.Writeable
import play.api.mvc._
import play.mvc.Http

import scala.concurrent.ExecutionContext.Implicits

class Application extends Controller
{

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
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


}