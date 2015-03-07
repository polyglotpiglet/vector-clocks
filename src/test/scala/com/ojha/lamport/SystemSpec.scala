package com.ojha.lamport

import com.ojha.lamport
import org.scalatest.{FunSuite, Matchers}

/**
 * Created by alexandra on 07/03/15.
 */
class SystemSpec extends FunSuite with Matchers {

  test("simple chain of local events in one process (fig1)") {

    // given
    val process = new Process(1, List[EventType](
      LocalEvent,
      LocalEvent,
      LocalEvent,
      LocalEvent
    ))

    val system = new System(List { process })

    // when
    val evaluatedProcesses: Seq[EvaluatedProcess] = system.evaluate()

    // then
    val expectedEvaluation = new EvaluatedProcess(1, List[Event](
      new Event(LocalEvent, 1),
      new Event(LocalEvent, 2),
      new Event(LocalEvent, 3),
      new Event(LocalEvent, 4)
    ))

    evaluatedProcesses.length should be(1)
    evaluatedProcesses(0) should be(expectedEvaluation)
  }

  test("sending 1 msg between 2 processes (fig2)") {

    // given
    val process1 = new Process(1, List[EventType](
      SendEvent(1)
    ))

    val process2 = new Process(2, List[EventType](
      ReceiveEvent(1)
    ))

    val system = new System(List[Process] (
      process1,
      process2
    ))

    // when
    val evaluatedProcesses: Seq[EvaluatedProcess] = system.evaluate()

    // then
    val process1Evaluated = new EvaluatedProcess(1, List[Event](
      new Event(SendEvent(1), 1)
    ))

    val process2Evaluated = new EvaluatedProcess(2, List[Event](
      new Event(ReceiveEvent(1), 2)
    ))

    evaluatedProcesses.length should be(2)

    evaluatedProcesses(0) should be(process1Evaluated)
    evaluatedProcesses(1) should be(process2Evaluated)
  }

  test("example cat(fig3)") {

    // given
    val process1 = new Process(1, List[EventType](
      LocalEvent,
      SendEvent(2),
      LocalEvent,
      ReceiveEvent(3),
      SendEvent(4)
    ))

    val process2 = new Process(2, List[EventType](
      ReceiveEvent(1),
      ReceiveEvent(2),
      SendEvent(3)
    ))


    val process3 = new Process(3, List[EventType](
      SendEvent(1),
      LocalEvent,
      ReceiveEvent(4)
    ))

    val system = new System(List[Process] (
      process1,
      process2,
      process3
    ))

    // when
    val evaluatedProcesses: Seq[EvaluatedProcess] = system.evaluate()
//
//    // then
//    val process1Evaluated = new EvaluatedProcess(1, List[Event](
//      new Event(SendEvent(1), 1)
//    ))
//
//    val process2Evaluated = new EvaluatedProcess(2, List[Event](
//      new Event(ReceiveEvent(1), 2)
//    ))

    evaluatedProcesses.length should be(3)

    println(evaluatedProcesses)
//
//    evaluatedProcesses(0) should be(process1Evaluated)
//    evaluatedProcesses(1) should be(process2Evaluated)
  }

}
