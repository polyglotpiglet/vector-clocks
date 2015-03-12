package com.ojha.timestamps

import com.ojha.timestamps.datamodel._
import org.scalatest.{FunSuite, Matchers}

/**
 * Created by alexandra on 07/03/15.
 */
class LamportEvaluatorSpec extends FunSuite with Matchers {

  def getTimeStamps(process: ResolvedCatProcess) = {
    process.events.map {
      e => e._2 match {
        case CatLamportTimeStamp(x) => x
      }
    }
  }

  test("simple chain of local events in one process (fig1)") {

    // given
    val process = new CatProcess(1, List[Event](
      LocalEvent,
      LocalEvent,
      LocalEvent,
      LocalEvent
    ))


    // when
    val evaluatedProcesses: Seq[ResolvedCatProcess] = new LamportEvaluator().lamportify(List(process))

    // then

    val expected = Array(1,2,3,4)
    evaluatedProcesses.size should be (1)


    getTimeStamps(evaluatedProcesses(0)) should be(expected)
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
    val evaluatedProcs = new LamportEvaluator().lamportify(List(process0, process1))

    // them
    evaluatedProcs.size should be(2)

    getTimeStamps(evaluatedProcs(0)) should be(Array(1))
    getTimeStamps(evaluatedProcs(1))  should be(Array(2))
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
    val evaluatedProcesses = new LamportEvaluator().lamportify(List(process0, process1, process2))

    // then

    getTimeStamps(evaluatedProcesses(0)) should be(Array(1,2,3,5,6))
    getTimeStamps(evaluatedProcesses(1)) should be(Array(2,3,4))
    getTimeStamps(evaluatedProcesses(2)) should be(Array(1,2,7))

    evaluatedProcesses.foreach(p => println(p))

  }

  test("q17") {

    // given
    val process1 = new CatProcess(1, List[Event](
      Send(1),
      Receive(3),
      Receive(5),
      LocalEvent,
      Send(8)
    ))

    val process2 = new CatProcess(2, List[Event](
      LocalEvent,
      Send(3),
      Receive(2),
      Send(4),
      Send(5),
      LocalEvent,
      Send(7),
      Receive(10)
    ))


    val process3 = new CatProcess(3, List[Event](
      Receive(1),
      Send(2),
      Send(6),
      LocalEvent,
      Receive(8),
      Send(9),
      Receive(4)
    ))

    val process4 = new CatProcess(4, List[Event](
      LocalEvent,
      Receive(7),
      Receive(6),
      Receive(9),
      Send(10)
    ))


    // when
    val evaluatedProcesses = new LamportEvaluator().lamportify(List(process1, process2, process3, process4))

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
