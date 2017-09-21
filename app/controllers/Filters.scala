package controllers
import javax.inject.Inject

import akka.event.LoggingFilter
import play.api.http.DefaultHttpFilters
import javax.inject.Inject
import akka.stream.Materializer
import play.api.mvc.{Result, RequestHeader, Filter}
import play.api.Logger
import play.api.routing.Router.Tags
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext



class Filters @Inject() (log: LoggingFilter) extends DefaultHttpFilters(log)
{

}

class LoggingFilter @Inject() (implicit val mat: Materializer) extends Filter {
  def apply(nextFilter: RequestHeader => Future[Result])
           (requestHeader: RequestHeader): Future[Result] = {

    val startTime = System.currentTimeMillis

    nextFilter(requestHeader).map { result =>

      val action = requestHeader.tags(Tags.RouteController) +
        "." + requestHeader.tags(Tags.RouteActionMethod)
      val endTime = System.currentTimeMillis
      val requestTime = endTime - startTime

      Logger.info(s"${action} took ${requestTime}ms and returned ${result.header.status}")

      result.withHeaders("Request-Time" -> requestTime.toString)
    }
  }
}

