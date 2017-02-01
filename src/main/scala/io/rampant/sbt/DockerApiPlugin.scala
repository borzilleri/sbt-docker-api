package io.rampant.sbt

import io.rampant.sbt.docker.DockerAPI
import sbt.Keys._
import sbt._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object DockerApiPlugin extends AutoPlugin {

	object autoImport {
		lazy val dockerApiHost = settingKey[Option[String]]("Hostname of docker engine, if not local.")
		lazy val dockerApiRepository = settingKey[Option[String]]("Repository images are tagged with and pushed to.")
		lazy val dockerApiImageName = settingKey[String]("Name used for built image.")
		lazy val dockerApiTags = settingKey[Seq[String]]("Tags applied to image.")
		lazy val dockerApiContext = settingKey[File]("Tar file passed to docker-build as the context.")

		lazy val build = TaskKey[Seq[String]]("docker-build", "Build docker image from provided context.")
		lazy val push = TaskKey[Unit]("docker-push", "Push built images to configured repository.")
	}

	import autoImport._

	override lazy val projectSettings = Seq(
		dockerApiHost := None,
		dockerApiRepository := None,
		dockerApiImageName := name.value,
		dockerApiTags := Seq(version.value),
		build := buildTask.value,
		push := pushTask
	)

	lazy val buildTask = Def.task {
		val api = new DockerAPI(dockerApiHost.value, streams.value.log)

		val futureResult = api.build(
			dockerApiContext.value,
			dockerApiRepository.value,
			dockerApiImageName.value,
			dockerApiTags.value
		)
		Await.result(futureResult, Duration.Inf)
	}

	lazy val pushTask = Def.task {
		val api = new DockerAPI(dockerApiHost.value, streams.value.log)

		val futureResult = api.push(dockerApiTags.value, streams.value.log)
		Await.result(futureResult, Duration.Inf)
	}


}
