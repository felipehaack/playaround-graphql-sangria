package com.fhs.graphql

import com.fhs.graphql.scope.{GraphqlContext, GraphqlDefinition}
import sangria.ast.Document
import sangria.execution._
import sangria.macros._
import sangria.schema._

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

object MainApp extends App with GraphqlDefinition {

  import com.fhs.graphql.util.JsonSangria._

  val schema = Schema(queryType)

  def printQuery(
                  document: Document
                ): Unit = {
    val context = new GraphqlContext
    val resultF = Executor.execute(schema, document, context)
    val resultAsJson = Await.result(resultF, Duration.Inf)
    println(PlayJsonResultMarshaller.renderPretty(resultAsJson))
  }

  val firstLevelQuery =
    graphql"""
      {
        profile(id: 10) {
          customerId
        }
      }
      """

  printQuery(firstLevelQuery)

  val secondLevelQuery =
    graphql"""
      {
        profile(id: 10) {
          customerId
          order {
             market
          }
        }
      }
      """

  printQuery(secondLevelQuery)

  val queryProfileById =
    graphql"""
      {
        profile(id: 10) {
          customerId
          name
          insight
          order {
            market
            insurance
            loan
          }
        }
      }
      """

  printQuery(queryProfileById)

  val queryProfiles =
    graphql"""
      {
        profiles {
          customerId
          name
          insight
          order {
            market
            insurance
            loan
          }
        }
      }
      """

  printQuery(queryProfiles)
}
