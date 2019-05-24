package com.fhs.graphql.scope.insight

import com.fhs.graphql.util.JsonUtil

object InsightKind extends Enumeration {

  import JsonUtil._

  implicit val insightFormat = enumValueFormat(InsightKind)

  val Coachmark = Value(0, "coachmark")
  val Enticement = Value(1, "enticement")
}