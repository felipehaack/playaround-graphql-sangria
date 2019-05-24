package com.fhs.graphql.scope.profile

import com.fhs.graphql.scope.insight.InsightKind
import com.fhs.graphql.scope.order.Order

case class Profile(
                    customerId: Long,
                    name: String,
                    insight: InsightKind.Value,
                    order: Option[Order] = None,
                    //either: Either[Cat, Dog], @TODO - implement union
                  )

object Profile {

}

