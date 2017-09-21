name := "PlayExample1"
 
version := "1.0" 
      
lazy val `playexample1` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
      
scalaVersion := "2.11.11"

libraryDependencies ++= Seq(cache, jdbc , cache , ws , specs2 % Test )



unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )

// only for Play 2.4.x
libraryDependencies ++= Seq("org.reactivemongo" %% "play2-reactivemongo" % "0.12.5-play25")

//enable injection routes
//routesGenerator := InjectedRoutesGenerator
