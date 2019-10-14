package arrow.core

import arrow.Kind2
import arrow.core.extensions.functor
import arrow.core.extensions.monoid
import arrow.core.extensions.show
import arrow.core.extensions.traverse
import arrow.test.UnitSpec
import arrow.test.generators.sortedMapK
import arrow.test.laws.MonoidLaws
import arrow.test.laws.ShowLaws
import arrow.test.laws.TraverseLaws
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen

class SortedMapKTest : UnitSpec() {

  val EQ: Eq<Kind2<ForSortedMapK, String, Int>> = object : Eq<Kind2<ForSortedMapK, String, Int>> {
    override fun Kind2<ForSortedMapK, String, Int>.eqv(b: Kind2<ForSortedMapK, String, Int>): Boolean =
      fix()["key"] == b.fix()["key"]
  }

  init {

    testLaws(
      ShowLaws.laws(SortedMapK.show(), EQ) { sortedMapOf("key" to 1).k() },
      MonoidLaws.laws(SortedMapK.monoid<String, Int>(Int.monoid()), Gen.sortedMapK(Gen.string(), Gen.int()), EQ),
      TraverseLaws.laws(
        SortedMapK.traverse<String>(),
        SortedMapK.functor<String>(),
        { a: Int -> sortedMapOf("key" to a).k() },
        EQ))
  }
}
