package com.fhs.graphql.scope.profile

import com.fhs.graphql.scope.insight.InsightKind
import com.fhs.graphql.scope.order.Order

import scala.util.Random

class ProfileService {
  def getById(
               id: Long
             ): Profile = {
    val order = Order(
      market = "market",
      insurance = Some("insurance"),
      loan = None
    )

    Profile(
      customerId = id,
      name = "name",
      insight = InsightKind.Enticement,
      order = Some(order)
    )
  }

  def getAll(): List[Profile] = {
    (for (i <- 0 until Random.nextInt(10)) yield getById(i)).toList
  }
}