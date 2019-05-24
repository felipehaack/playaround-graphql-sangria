import sbt.Keys._
import sbt._

object Dependencies extends AutoPlugin {
  val autoImport = this

  val resolvers = Seq(
    Resolver.jcenterRepo
  )
  
  val sangria = "org.sangria-graphql" %% "sangria" % "1.4.2"
}

object ProjectSettings extends AutoPlugin {
  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    organization := "com.fhs.graphql",
    scalaVersion := "2.12.8",
    resolvers ++= Dependencies.resolvers
  )
}
