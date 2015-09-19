package io.getquill.norm

import io.getquill._

class AdHocReductionSpec extends Spec {

  "filter.filter" - {
    "a.filter(b => c).filter(d => e)" in {
      val q = quote {
        qr1.filter(b => b.s == "s1").filter(d => d.s == "s2")
      }
      val n = quote {
        qr1.filter(b => b.s == "s1" && b.s == "s2")
      }
      AdHocReduction.unapply(q.ast) mustEqual Some(n.ast)
    }
  }

  "flatMap.*" - {
    "a.flatMap(b => c).map(d => e)" in {
      val q = quote {
        qr1.flatMap(b => qr2).map(d => d.s)
      }
      val n = quote {
        qr1.flatMap(b => qr2.map(d => d.s))
      }
      AdHocReduction.unapply(q.ast) mustEqual Some(n.ast)
    }
    "a.flatMap(b => c).filter(d => e)" in {
      val q = quote {
        qr1.flatMap(b => qr2).filter(d => d.s == "s2")
      }
      val n = quote {
        qr1.flatMap(b => qr2.filter(d => d.s == "s2"))
      }
      AdHocReduction.unapply(q.ast) mustEqual Some(n.ast)
    }
    "a.flatMap(b => c).sortBy(d => e)" in {
      val q = quote {
        qr1.flatMap(b => qr2).sortBy(d => d.s)
      }
      val n = quote {
        qr1.flatMap(b => qr2.sortBy(d => d.s))
      }
      AdHocReduction.unapply(q.ast) mustEqual Some(n.ast)
    }
  }
}