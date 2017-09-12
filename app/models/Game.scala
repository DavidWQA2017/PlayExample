package models

import play.api.data.Forms._
import play.api.data._

import scala.collection.mutable.ArrayBuffer

// We create a case class and define the fields it will have
case class Game(name: String, price: Int, description: String)

// We then create a companion object to accompany it, where we store our Form and a list of CDs
object Game {

  // remember to use the imports when defining forms, import play.api.data._ and import play.api.data.Forms._
  // we then define a value  to which we assign a Form, inside the parentheses of the Form
  // we then use mapping to map the name to a constraint
  // in this particular case name will need some string passed to it, and an empty one wouldn't work
  // for the price we say that it has to be of numerical value, where the minimum value is going to be 0
  // and the maximum value will be 100
  // we then follow the closing parentse of mapping by having the apply unapply methods
  // which are needed for the data to be mapped to the model fields
  val createGameForm = Form(
    mapping(
      "name" -> nonEmptyText,
      "price" -> number(min = 0, max = 100),
      "description" -> nonEmptyText
    )(Game.apply)(Game.unapply)
  )

  // this is where we define a collection of CDs so that we could display some of them on the page
  // keep in mind if you were to stop this application the added models.CD's would be wiped and it
  // would one again start with these three


  val Games = ArrayBuffer(
    Game("Halo5", 20, "An unstoppable force threatens the galaxy, and the Master Chief is missing." +
      " An epic story and two new multiplayer modes mark the greatest evolution in Halo history."),
    Game("Battlefield1", 25, "Fight your way through epic battles ranging from tight urban combat in a besieged French"
      +" city to the heavily defended mountain forts in the Italian Alps, or frantic combats in the deserts of Arabia."),
    Game("ZeldaBW", 35, " Prepare for the biggest The Legend of Zelda adventure yet, with an open-air style that breaks"
      + " new boundaries while honouring the origins of the acclaimed series."),
    Game("ElderScrollsOnline", 15, "Experience an entirely new chapter in the Elder Scrolls series set a thousand years" +
      " before the events of Skyrim. Take up arms as one of the three factions, as the daedric prince Molag Bal attempts" +
      " to pull the world of Tamriel into the demonic realm. ")
  )

  val ShowGames = ArrayBuffer(RandomlyGenerateShowGame())

  def RandomlyGenerateShowGame(): ArrayBuffer[Game] =
  {
    var gamesAlreadyGenerated:ArrayBuffer[Game] = new ArrayBuffer[Game] += Games(0)
    while (gamesAlreadyGenerated.size != 4)
    {
      var index = scala.util.Random.nextInt(Games.size)
      var gameisNotInList = 0
      for (i <- 0 to gamesAlreadyGenerated.size -1)
      {
        if(Games(index).name != gamesAlreadyGenerated(i).name)
        {
          gameisNotInList += 1
        }
      }

      if(gameisNotInList == gamesAlreadyGenerated.size)
      {
        gamesAlreadyGenerated += Games(index)
      }
    }
    gamesAlreadyGenerated:ArrayBuffer[Game]
  }



}

