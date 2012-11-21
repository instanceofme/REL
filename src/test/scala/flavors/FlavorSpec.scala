package fr.splayce.rel.flavors

import org.specs2.mutable._

import fr.splayce.rel._
import util._


class FlavorSpec extends Specification {

  import Symbols._
  import Implicits.string2RE
  import FlavorSpec._

  "Default translation" should {

    val tr = { (re: RE) => simpleFlavor.express(re)._1 }
    val a  = RE("a")
    val aa = RE("aa")
    val c  = RE("c")

    "translate recursively" in {
      tr(a)                   must_== "b"
      tr(a %)                 must_== "b"
      tr(aa %)                must_== "(?:bb)"
      tr(a?)                  must_== "b?"
      tr(aa?)                 must_== "(?:bb)?"
      tr(a+)                  must_== "b+"
      tr(aa+)                 must_== "(?:bb)+"
      tr(a*)                  must_== "b*"
      tr(aa*)                 must_== "(?:bb)*"
      tr(a{2})                must_== "b{2}"
      tr(aa{2})               must_== "(?:bb){2}"
      tr(a(1 -> 3))           must_== "b{1,3}"
      tr(aa(1 ->3))           must_== "(?:bb){1,3}"
      tr(a > 3)               must_== "b{3,}"
      tr(aa > 3)              must_== "(?:bb){3,}"
      tr(a < 3)               must_== "b{0,3}"
      tr(aa < 3)              must_== "(?:bb){0,3}"
      tr(aa | c)              must_== "bb|d"
      tr(aa - c)              must_== "bbd"
      tr(aa ~ c)              must_== "(?:bb)d"
      tr(a \ "g")             must_== "(b)"
      tr(a.?=)                must_== "(?=b)"
      tr((c | (a{3}++)) - a)  must_== "d|(?:b{3})++b"
    }

    "compose translation" in {
      val rr: Flavor = simpleFlavor andThen reverseFlavor

      rr(a)                  .toString must_== (a)                  .toString
      rr(a %)                .toString must_== (a %)                .toString
      rr(a?)                 .toString must_== (a?)                 .toString
      rr(a+)                 .toString must_== (a+)                 .toString
      rr(a*)                 .toString must_== (a*)                 .toString
      rr(a{2})               .toString must_== (a{2})               .toString
      rr(a(1 -> 3))          .toString must_== (a(1 -> 3))          .toString
      rr(a > 3)              .toString must_== (a > 3)              .toString
      rr(a < 3)              .toString must_== (a < 3)              .toString
      rr(aa | c)             .toString must_== (aa | c)             .toString
      rr(aa - c)             .toString must_== (aa - c)             .toString
      rr(aa ~ c)             .toString must_== (aa ~ c)             .toString
      rr(a \ "g")            .toString must_== (a \ "g")            .toString
      rr(a.?=)               .toString must_== (a.?=)               .toString
      rr((c | (a{3}++)) - a) .toString must_== ((c | (a{3}++)) - a) .toString
    }

  }

}

object FlavorSpec {

  val simpleFlavor = Flavor {
    case Atom(a) if (a.toString == "a")  => Atom("b".r)
    case Atom(a) if (a.toString == "aa") => Atom("bb".r)
    case Atom(c) if (c.toString == "c")  => Atom("d".r)
  }

  val reverseFlavor = Flavor {
    case Atom(b) if (b.toString == "b")  => Atom("a".r)
    case Atom(b) if (b.toString == "bb") => Atom("aa".r)
    case Atom(d) if (d.toString == "d")  => Atom("c".r)
  }
}