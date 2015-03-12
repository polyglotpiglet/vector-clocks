package com.ojha.timestamps

import com.ojha.timestamps.datamodel._
import org.scalatest.{FunSuite, Matchers}


/**
 * Created by alexandra on 07/03/15.
 */
class VectorClockEvaluatorSpec extends FunSuite with Matchers {

  def getTimeStamps(process: ResolvedCatProcess): Seq[Array[Int]] = {
    process.events.map {
      e => e._2 match {
        case CatVectorClock(x) => x
      }
    }
  }

  test("simple chain of local events in one process (fig1)") {

    // given
    val process = new CatProcess(0, List[Event](
      LocalEvent,
      LocalEvent,
      LocalEvent,
      LocalEvent
    ))

    // when
    val evaluatedProcesses: Seq[ResolvedCatProcess] = new VectorClockEvaluator().clockify(List(process))

    // then


    evaluatedProcesses.size should be (1)

    val expected = List(Array(1), Array(2), Array(3), Array(4))

    getTimeStamps(evaluatedProcesses(0)).zipWithIndex.map {case (x,i) => x should equal (expected(i))}
  }

  test("sending 1 msg between 2 processes (fig2)") {

    // given
    val process0 = new CatProcess(0, List[Event](
      Send(1)
    ))

    val process1 = new CatProcess(1, List[Event](
      Receive(1)
    ))

    // when
    val evaluatedProcs = new VectorClockEvaluator().clockify(List(process0, process1))

    // them
    evaluatedProcs.size should be(2)

    getTimeStamps(evaluatedProcs(0))(0) should be(Array(1,0))
    getTimeStamps(evaluatedProcs(1))(0)  should be(Array(1,1))
  }

  test("bigger example (fig3)") {

    // given
    val process0 = new CatProcess(0, List[Event](
      LocalEvent,
      Send(2),
      LocalEvent,
      Receive(3),
      Send(4)
    ))

    val process1 = new CatProcess(1, List[Event](
      Receive(1),
      Receive(2),
      Send(3)
    ))


    val process2 = new CatProcess(2, List[Event](
      Send(1),
      LocalEvent,
      Receive(4)
    ))


    // when
    val evaluatedProcesses = new VectorClockEvaluator().clockify(List(process0, process1, process2))

    // then

    val expectedCatProcess0 = Array(Array(1,0,0), Array(2,0,0), Array(3,0,0), Array(4,3,1), Array(5,3,1))
    val expectedCatProcess1 = Array(Array(0,1,1), Array(2,2,1), Array(2,3,1))
    val expectedCatProcess2 = Array(Array(0,0,1), Array(0,0,2), Array(5,3,3))


    getTimeStamps(evaluatedProcesses(0)).toArray should equal(expectedCatProcess0)
    getTimeStamps(evaluatedProcesses(1)).toArray should equal(expectedCatProcess1)
    getTimeStamps(evaluatedProcesses(2)).toArray should equal(expectedCatProcess2)

  }

  test("q17") {

    // given
    val process0 = new CatProcess(0, List[Event](
      Send(1),
      Receive(3),
      Receive(5),
      LocalEvent,
      Send(8)
    ))

    val process1 = new CatProcess(1, List[Event](
      LocalEvent,
      Send(3),
      Receive(2),
      Send(4),
      Send(5),
      LocalEvent,
      Send(7),
      Receive(10)
    ))


    val process2 = new CatProcess(2, List[Event](
      Receive(1),
      Send(2),
      Send(6),
      LocalEvent,
      Receive(8),
      Send(9),
      Receive(4)
    ))

    val process3 = new CatProcess(3, List[Event](
      LocalEvent,
      Receive(7),
      Receive(6),
      Receive(9),
      Send(10)
    ))


    // when
    val evaluatedProcesses = new VectorClockEvaluator().clockify(List(process0, process1, process2, process3))

    // then

    println(getTimeStamps(evaluatedProcesses(0)))
    println(getTimeStamps(evaluatedProcesses(1)))
    println(getTimeStamps(evaluatedProcesses(2)))
    println(getTimeStamps(evaluatedProcesses(3)))

    //    println(evaluatedProcesses(0).eventChain.map(e => { e.lamportTimestamp }))
    //    println(evaluatedProcesses(1).eventChain.map(e => { e.lamportTimestamp }))
    //    println(evaluatedProcesses(2).eventChain.map(e => { e.lamportTimestamp }))
    //    println(evaluatedProcesses(3).eventChain.map(e => { e.lamportTimestamp }))
  }
  //
  //  test("q15") {
  //
  //    // given
  //    val process1 = new ProcessEventChain(1, List[Event](
  //      Send(1),
  //      LocalEvent,
  //      Receive(7),
  //      Send(8),
  //      LocalEvent,
  //      LocalEvent,
  //      Receive(10)
  //    ))
  //
  //    val process2 = new ProcessEventChain(2, List[Event](
  //      Receive(3),
  //      Send(6),
  //      Send(7)
  //    ))
  //
  //
  //    val process3 = new ProcessEventChain(3, List[Event](
  //      Receive(2),
  //      Send(4),
  //      Send(3),
  //      Receive(1),
  //      Send(5),
  //      LocalEvent,
  //      Receive(8),
  //      Send(9),
  //      Receive(6)
  //    ))
  //
  //    val process4 = new ProcessEventChain(4, List[Event](
  //      Send(2),
  //      Receive(4),
  //      Receive(5),
  //      LocalEvent,
  //      Receive(9),
  //      Send(10)
  //    ))
  //
  //    val system = new VectorClockSystem(List[ProcessEventChain] (
  //      process1,
  //      process2,
  //      process3,
  //      process4
  //    ))
  //
  //    // when
  //    val evaluatedProcesses = system.evaluateTimeStamps()
  //
  //    // then
  //
  ////    println(evaluatedProcesses(0).eventChain.map(e => { e.lamportTimestamp }))
  ////    println(evaluatedProcesses(1).eventChain.map(e => { e.lamportTimestamp }))
  ////    println(evaluatedProcesses(2).eventChain.map(e => { e.lamportTimestamp }))
  ////    println(evaluatedProcesses(3).eventChain.map(e => { e.lamportTimestamp }))
  //  }

}
