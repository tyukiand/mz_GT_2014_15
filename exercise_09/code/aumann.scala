import scala.collection.immutable.Set

/**
 * Represents a set of elements of type `A`
 * either enumerating all the elements explicitly,
 * or representing them as products of simpler sets.
 */
sealed trait FormalSet[A] {
  def union(other: FormalSet[A]): FormalSet[A]
  def intersect(other: FormalSet[A]): FormalSet[A]
  def contains(a: A): Boolean
  def subsetOf(other: FormalSet[A]) = {
    enumerate forall other.contains
  }
  def enumerate: Set[A]
  def size: Int
}

case class ExplicitSet[A](set: Set[A])
extends FormalSet[A] {
  def union(other: FormalSet[A]) = 
    ExplicitSet(set union other.enumerate)
  def intersect(other: FormalSet[A]) =
    ExplicitSet(set intersect other.enumerate)
  def enumerate = set
  val size = set.size
}

case class ProductSet[A](sets: List[FormalSet[A]])
extends FormalSet[List[A]] {
  def union(other: FormalSet[List[A]]) = 
    ExplicitSet(enumerate union other.enumerate)
  def intersect(other: FormalSet[List[A]]) = other match {
    case e: ExplicitSet[A @unchecked] => 
      e intersect this
    case p: ProductSet[A @unchecked] => {
      require(sets.size == p.sets.size)
      val newSets = (sets zip p.sets).map { 
        case (x, y) => x intersect y
      }
      ProductSet(newSets)
    }
  }
  def enumerate: Set[List[A]] = {
    if (sets.isEmpty) Set(Nil)
    else {
      val head = sets.head
      val tail = ProductSet(sets.tail)
      for (h <- head.enumerate; t <- tail.enumerate) 
        yield h :: t
    }
  }
  def contains(a: List[A]) = {
    require(a.size == sets.size)
    (a zip sets) forall { case (e, s) => s contains e}
  }
  def subsetOf(other: FormalSet[List[A]]) = other match {
    case e: ExplicitSet[A @unchecked] => super.subsetOf(e)
    case p: ProductSet[A @unchecked] => {
      (sets zip p.sets).forall{ case (x, y) => x subsetOf y }
    }
  }
  val size = sets.map{_.size}.product
}

// helper method for writing down sets in a somewhat
// more readable format
def setToString(set: Set[_]) = {
  set.map{_.toString}.toList.sorted.mkString("{",",","}")
}

trait AtomicSigmaAlgebra[A] {
  def containingSet(a: A): FormalSet[A]
  def exactlyKnownState: FormalSet[A]
  def knowledgeOperator(event: FormalSet[A]): FormalSet[A]

  def preimageSigmaAlgebra[B](
    f: A => B,
    imgAlg: AtomicSigmaAlgebra[B]
  ): AtomicSigmaAlgebra[A]
}

case class Powerset[A](base: Set[A])
extends AtomicSigmaAlgebra[A] {
  def containingSet(a: A) = ExplicitSet(Set(a))
  def exactlyKnownState = ExplicitSet(base)
  def knowledgeOperator(event: FormalSet[A]) = event
  def preimageSigmaAlgebra[B](
    f: A => B,
    imgAlg: AtomicSigmaAlgebra[B]
  ) = {
    val atoms = 
      base.groupBy{
        x => imgAlg.containingSet(f(x))
      }.values.map{s => ExplicitSet(s)}.toSet
    ExplicitSigmaAlgebra(atoms)
  }
}

case class ExplicitSigmaAlgebra[A](
  atoms: Set[FormalSet[A]]
) extends AtomicSigmaAlgebra[A] {
  private val elementToSet = 
    (for (atom <- atom; x <- atom.enumerate) 
     yield (x, atom)).toMap
  def containingSet(a: A) = elementToSet(a)
  lazy val exactlyKnownState = 
    atoms.filter(_.size == 1).
      foldLeft(ExplicitSet(Set[A]())){_ union _}
  def knowledgeOperator(a: FormalSet[A]) = {
    val subsets = atoms filter { _ subsetOf a }
    subsets.foldLeft(ExplicitSet(Set[A]())){_ union _}
  }
  def preimageSigmaAlgebra[B](
    f: A => B,
    imgAlg: AtomicSigmaAlgebra[B]
  ) = {
    // expects `f` to be measurable, otherwise
    // the result is undefined
    val newAtoms = atoms.groupBy{
      // this gets the containing set of the image for
      // a single element of the atom `a`
      a => imgAlg.containingSet(f(a.enumerate.first))
    }.values.map{
      // this just glues the old atoms into bigger ones
      _.foldLeft(ExplicitSet(Set[A]())){_ union _}
    }
    ExplicitSigmaAlgebra(newAtoms)
  }
}

case class ProductSigmaAlgebra[A](
  components: List[AtomicSigmaAlgebra[A]]
) extends AtomicSigmaAlgebra[List[A]] {
  def containingSet(a: List[A]) = ProductSet(
    (a zip components).map {
      case (x, y) => y.containingSet(x)
    })
  def exactlyKnownState: FormalSet[List[A]] = ProductSet(
    components map _.exactlyKnownState
  )
  def knowledgeOperator(event: FormalSet[List[A]]) = {
    event match {
      case e: ExplicitSet[List[A] @unchecked] => {
        ??? // ugly, not needed right now
      }
      case p: ProductSet[A @unchecked] => {
        ProductSet((p.sets zip components).map{
          case (x, y) => y.knowledgeOperator(x)
        })
      }
    }
  }

  def preimageSigmaAlgebra[B](
    f: A => B,
    imgAlg: AtomicSigmaAlgebra[B]
  ): AtomicSigmaAlgebra[A] = {
    ??? // even more horrible, not needed here also, but
    // theoretically possible to implement
  }
}

// Some use-case specific data
val ledStrings = Array(
  "1110111",
  "0010010",
  "1011101",
  "1011011",
  "0111010",
  "1101011",
  "1101111",
  "1010010",
  "1111111",
  "1111011"
)

// helper method for extracting decimal digits
// from a number
def digits(numDigits: Int, number: Int): List[Int] = {
  if (numDigits == 0) Nil
  else (number % 10) :: digits(numDigits - 1, number / 10)
}

/** 
 * Time unit is essentially just Int, but with 
 * additional number of digits for nicer layout
 * and a method that can draw a 'top' and 'bottom' part
 */
case class TimeUnit(i: Int, numDigits: Int) {
  override def toString = "%%0%dd".format(numDigits).format(i)
  
  def top = digits(numDigits, i).map{
    d => ledStrings(d).take(4)
  }

  def bottom = digits(numDigits, i).map{
    d => ledStrings(d).drop(3)
  }  
}

// Ok, let's launch the whole machinery now
val hours = Powerset((0 until 24).map{i => TimeUnit(i, 2)}.toSet)
val tens = Powerset((0 until 6).map{i => TimeUnit(i, 1)}.toSet)
val minutes = Powerset((0 until 10).map{i => TimeUnit(i, 1)}.toSet)
val vision = Powerset(Set[List[String]]())

val chronos = hours.preimageSigmaAlgebra(n => n.top, vision)
val hora = hours.preimageSigmaAlgebra(n => n.bottom, vision)

println("Chronos algebra:" + chronos)
println("Hora algebra:" + chronos)