package models

import play.api.data.Forms._
import play.api.data._

import scala.collection.mutable.ArrayBuffer

// We create a case class and define the fields it will have
case class CD(name: String, price: Int)

// We then create a companion object to accompany it, where we store our Form and a list of CDs
object CD {

  // remember to use the imports when defining forms, import play.api.data._ and import play.api.data.Forms._
  // we then define a value  to which we assign a Form, inside the parentheses of the Form
  // we then use mapping to map the name to a constraint
  // in this particular case name will need some string passed to it, and an empty one wouldn't work
  // for the price we say that it has to be of numerical value, where the minimum value is going to be 0
  // and the maximum value will be 100
  // we then follow the closing parentse of mapping by having the apply unapply methods
  // which are needed for the data to be mapped to the model fields
  val createCDForm = Form(
    mapping(
      "name" -> nonEmptyText,
      "price" -> number(min = 0, max = 100)
    )(CD.apply)(CD.unapply)
  )


  // this is where we define a collection of CDs so that we could display some of them on the page
  // keep in mind if you were to stop this application the added models.CD's would be wiped and it
  // would one again start with these three


  val cds = ArrayBuffer(
    CD("models.CD 1", 123),
    CD("models.CD 2", 456),
    CD("models.CD 3", 789),
    CD("models.CD 4", 254)
  )
}

