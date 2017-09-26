package controllers

import play.api.http.DefaultHttpFilters
import javax.inject.Inject

class Filters @Inject() (log: LoggingFilter) extends DefaultHttpFilters(log)
