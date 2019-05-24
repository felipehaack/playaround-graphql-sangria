package com.fhs.graphql.scope.order

case class Order(
                  market: String,
                  insurance: Option[String] = None,
                  loan: Option[String] = None
                )
