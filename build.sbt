name := "sbt-docker-api"
version := "1.0"

sbtPlugin := true
scalaVersion := "2.10.6"

resolvers += "softprops-maven" at "http://dl.bintray.com/content/softprops/maven"
libraryDependencies += "me.lessis" %% "tugboat" % "0.2.0"

