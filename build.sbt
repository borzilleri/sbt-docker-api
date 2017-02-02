import bintray.Keys._

enablePlugins(GitVersioning)

name := "sbt-docker-api"
git.baseVersion := "1.0"
git.useGitDescribe := true

sbtPlugin := true
scalaVersion := "2.10.6"

resolvers += "softprops-maven" at "http://dl.bintray.com/content/softprops/maven"
libraryDependencies += "me.lessis" %% "tugboat" % "0.2.0"

bintrayPublishSettings.settings

publishMavenStyle := false
licenses += ("MIT", url("http://opensource.org/licenses/MIT"))
repository in bintray := "sbt-plugins"
bintrayOrganization := None

