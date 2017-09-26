package controllers

import javax.inject.Inject

import play.api.i18n.{I18nSupport, Lang, MessagesApi}
import play.api.mvc.{Action, Controller}

class InternationalLang @Inject ()(val messagesApi: MessagesApi) extends Controller with I18nSupport
{

  def switchToLanguage(language: String, uri: String) = Action { implicit request =>
    Redirect(uri).withLang(Lang(language))
  }

}

