// @date: 2015-01-14
// @author: Andrey Tyukin
//
// Incomplete information: digital clock example
//
// To run this script: download scala somewhere
// (e.g. sudo apt-get install scala
//  if you don't care too much about the version,
//  or better: download latest version from
//  http://www.scala-lang.org/download/
//  then unzip, add /bin subdirectory to $PATH)
// Then run with:
//
// you@host$ scala digitalClock.scala

// States of leds on a digital display
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

// Just for test purposes: transform led pattern to
// ascii-art number
def number(s: String) = {
  def led(index: Int) = 
    if (s(index) == '1') "_||_||_"(index)
    else ' '

  " %c \n%c%c%c\n%c%c%c".format(
            led(0), 
    led(1), led(3), led(2), 
    led(4), led(6), led(5)
  )
}

for (i <- 0 to 9) println(number(ledStrings(i)))

// for debug-purposes: draw half a number
def halfNumber(s: String) = {
  def led(index: Int) = 
    if (s(index) == '1') "_||_"(index)
    else ' '

  " %c \n%c%c%c".format(
            led(0), 
    led(1), led(3), led(2)
  )
}

// counts decimal digits in a number
def numDigits(n: Int): Int = 
  if (n == 0) 0 else (1 + numDigits(n / 10))

// helper method for extracting decimal digits
// from a number
def digits(numDigits: Int, number: Int): List[Int] = {
  if (numDigits == 0) Nil
  else (number % 10) :: digits(numDigits - 1, number / 10)
}

// The whole exercise contains two kinds of 'knowledge'
// The first kind of knowledge is 'geometrical',
// the second kind of knowledge is in the sense of 
// Aumann.
//
// This method generates the knowledge partition 
// of the set {start, ..., end} for a player, it
// requires a function that tells us what the player
// can see. Therefore, it transforms the geometrical
// information into a simple partition of integers.
def partition(
  start: Int, end: Int, 
  visibility: String => String
) = {
  // determine number of digits (1 or 2)
  var nd = numDigits(end)

  // group numbers by the visible parts
  val p = (start to end).groupBy { k =>
    val ds = digits(nd, k)
    ds.map { digit => visibility(ledStrings(digit)) }
  }

  // `group` returns maps, 
  // we need only the right hand side
  p.values.map{_.toSet}.toSet
}


// partitions ranges of numbers for top/bottom player
def upperPartition(start: Int, end: Int) = 
  partition(start, end, _.take(4))
def lowerPartition(start: Int, end: Int) =
  partition(start, end, _.drop(3))

// The knowledge operator for a single \Omega_i
def knowledge(partition: Set[Set[Int]])(a: Set[Int]) = {
  val subsetsOfA = partition.filter{x => x subsetOf a}
  subsetsOfA.flatten
}

// Knowledge operator for the whole product space
// \Omega = \prod_i \Omega_i
// implemented in terms of the previous knowledge 
// operator for a single slice
def knowledge(
  partitions: List[Set[Set[Int]]]
)(a: List[Set[Int]]): List[Set[Int]] = {
  for ((p, aComponent) <- partitions zip a) yield {
    knowledge(p)(aComponent)
  }
}

// lists of partitions for Chronos and Hora
val upperPartitions = List(23, 5, 9).map{ n => 
  upperPartition(0, n)
}

val lowerPartitions = List(23, 5, 9).map{ n =>
  lowerPartition(0, n)
}

// helper method for writing the sets with nice
// parentheses and in some sane order
def intSetToString(set: Set[Int], pad: Int = -1) = {
  val fmtStr = if (pad == -1) {
    val max = set.max
    val nd = math.max(1, numDigits(max))
    "%%0%dd".format(nd)
  } else {
    "%%0%dd".format(pad)
  }
  set.map{k => fmtStr.format(k)}.
    toList.sorted.mkString("{",",","}")
}

def prodToString(prod: List[Set[Int]]) = {
  (prod map { s => intSetToString(s) }).mkString(" x ")
}

def partitionToString(partition: Set[Set[Int]]) = {
  val nd = numDigits(partition.flatten.max)
  (partition map {s => intSetToString(s, nd)}).toList.
    sorted.mkString("{",",","}")
}

// print it, just for debug purposes
println("Chronos partitions:")
for (p <- upperPartitions) {
  println(partitionToString(p))
}

println("Hora partitions:")
for (p <- lowerPartitions) {
  println(partitionToString(p))
}

// The events 'chronos knows the number' and 
// 'hora knows the number' are unions of the elements 
// of the partitions that contain exactly one element.
//
// For example, if the partition is {{5,6}, {1}, {0}},
// then the event of 'knowing what the number is' is
// the union of {1} and {0} = {0,1}
def knowingTheNumberEvent(partition: Set[Set[Int]]) = {
  partition.filter{_.size == 1}.flatten
}

val upperKnowsTime = 
  upperPartitions map knowingTheNumberEvent

val lowerKnowsTime = 
  lowerPartitions map knowingTheNumberEvent

println("Chronos knows time: ")
println(prodToString(upperKnowsTime))

println("Hora knows time: ")
println(prodToString(lowerKnowsTime))

// instantiate the both knowledge operators
val k_u = knowledge(upperPartitions)(_)
val k_l = knowledge(lowerPartitions)(_)

println("Chronos knows Hora knows time: ")
println(prodToString(k_u(lowerKnowsTime)))
println("Hora knows Chronos knows time: ")
println(prodToString(k_l(upperKnowsTime)))

// the event that both know time is intersection 
// of the two events
val bothKnowTime = 
  (upperKnowsTime zip lowerKnowsTime).map {
    case (x, y) => x intersect y
  }

// now keep iterating until the sequence stabilizes
// (it is noetherian, so the algorithm terminates)
val ks = List(k_u, k_l)
var commonKnowledge = bothKnowTime
while (!ks.forall{k => k(commonKnowledge) == commonKnowledge}){
  println(prodToString(commonKnowledge))
  for (k <- ks) commonKnowledge = k(commonKnowledge)
}

println("State of common knowledge that both know time:")
println(prodToString(commonKnowledge))