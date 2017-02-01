package io.rampant.sbt.docker

import java.io.File

import tugboat.{Build, Push}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DockerAPI(host: Option[String], logger: sbt.Logger) {
	lazy val client = tugboat.Docker(hostStr = host.getOrElse(DockerAPI.defaultHostStr))

	def build(context: File, repo: Option[String], name: String, tags: Seq[String]): Future[Seq[String]] = {
		val baseName = repo.map(_ + "/") + name
		val imageTags = tags.map(tag => s"$baseName:$tag")

		val zBuild = client.images.Build(path = context)
		val accumulator = (b: client.images.Build, t: String) => b.tag(t)
		imageTags
			.foldLeft(zBuild)(accumulator)
			.stream({
				case Build.Progress(prog) => logger.info(prog)
				case Build.Status(status) => logger.info(status)
				case Build.Error(err, _, _) => throw new Exception(err)
			})._2.map(_ => imageTags)
	}

	def push(tags: Seq[String], logger: sbt.Logger): Future[Unit] = {
		client.images.list() flatMap { result =>
			Future.sequence {
				result
					.filter(image => image.repoTags.intersect(tags).nonEmpty)
					.map(i => client.images.get(i.id))
					.map(image => image.push.stream {
						case Push.Status(status) => logger.info(status)
						case Push.Error(err, _) => throw new Exception(err)
					}._2)
			} map(_ => Unit)
		}
	}
}

object DockerAPI {
	val defaultHostStr = "unix:///var/run/docker.sock"
}
