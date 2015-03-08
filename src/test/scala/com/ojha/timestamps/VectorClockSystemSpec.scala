//package com.ojha.timestamps
//
//import com.ojha.timestamps.datamodel._
//import org.scalatest.{FunSuite, Matchers}
//
///**
// * Created by alexandra on 07/03/15.
// */
//class VectorClockSystemSpec extends FunSuite with Matchers {
//
//  test("simple chain of local events in one process (fig1)") {
//
//    // given
//    val process = new ProcessEventChain(1, List[EventType](
//      LocalEvent,
//      LocalEvent,
//      LocalEvent,
//      LocalEvent
//    ))
//
//    val system = new LamportTimestampSystem(List { process })
//
//    // when
//    val evaluatedProcesses = system.evaluateTimeStamps()
//
//    // then
//    val expectedEvaluation = new EvaluatedProcess(1, List[Event](
//      new Event(LocalEvent, 1),
//      new Event(LocalEvent, 2),
//      new Event(LocalEvent, 3),
//      new Event(LocalEvent, 4)
//    ))
//
//    evaluatedProcesses.length should be(1)
//    evaluatedProcesses(0) should be(expectedEvaluation)
//  }
//
//  test("sending 1 msg between 2 processes (fig2)") {
//
//    // given
//    val process1 = new ProcessEventChain(1, List[EventType](
//      SendEvent(1)
//    ))
//
//    val process2 = new ProcessEventChain(2, List[EventType](
//      ReceiveEvent(1)
//    ))
//
//    val system = new LamportTimestampSystem(List[ProcessEventChain] (
//      process1,
//      process2
//    ))
//
//    // when
//    val evaluatedProcesses: Seq[EvaluatedProcess] = system.evaluateTimeStamps()
//
//    // then
//    val process1Evaluated = new EvaluatedProcess(1, List[Event](
//      new Event(SendEvent(1), 1)
//    ))
//
//    val process2Evaluated = new EvaluatedProcess(2, List[Event](
//      new Event(ReceiveEvent(1), 2)
//    ))
//
//    evaluatedProcesses.length should be(2)
//
//    evaluatedProcesses(0) should be(process1Evaluated)
//    evaluatedProcesses(1) should be(process2Evaluated)
//  }
//
//  test("bigger example (fig3)") {
//
//    // given
//    val process1 = new ProcessEventChain(1, List[EventType](
//      LocalEvent,
//      SendEvent(2),
//      LocalEvent,
//      ReceiveEvent(3),
//      SendEvent(4)
//    ))
//
//    val process2 = new ProcessEventChain(2, List[EventType](
//      ReceiveEvent(1),
//      ReceiveEvent(2),
//      SendEvent(3)
//    ))
//
//
//    val process3 = new ProcessEventChain(3, List[EventType](
//      SendEvent(1),
//      LocalEvent,
//      ReceiveEvent(4)
//    ))
//
//    val system = new LamportTimestampSystem(List[ProcessEventChain] (
//      process1,
//      process2,
//      process3
//    ))
//
//    // when
//    val evaluatedProcesses: Seq[EvaluatedProcess] = system.evaluateTimeStamps()
//
//    // then
//    evaluatedProcesses.length should be(3)
//
//    evaluatedProcesses(0).eventChain.map(e => { e.lamportTimestamp }) should equal(List(1,2,3,5,6))
//    evaluatedProcesses(1).eventChain.map(e => { e.lamportTimestamp }) should equal(List(2,3,4))
//    evaluatedProcesses(2).eventChain.map(e => { e.lamportTimestamp }) should equal(List(1,2,7))
//
//  }
//
//  test("q17") {
//
//    // given
//    val process1 = new ProcessEventChain(1, List[EventType](
//      SendEvent(1),
//      ReceiveEvent(3),
//      ReceiveEvent(5),
//      LocalEvent,
//      SendEvent(8)
//    ))
//
//    val process2 = new ProcessEventChain(2, List[EventType](
//      LocalEvent,
//      SendEvent(3),
//      ReceiveEvent(2),
//      SendEvent(4),
//      SendEvent(5),
//      LocalEvent,
//      SendEvent(7),
//      ReceiveEvent(10)
//    ))
//
//
//    val process3 = new ProcessEventChain(3, List[EventType](
//      ReceiveEvent(3),
//      SendEvent(2),
//      SendEvent(6),
//      LocalEvent,
//      ReceiveEvent(8),
//      SendEvent(9),
//      ReceiveEvent(4)
//    ))
//
//    val process4 = new ProcessEventChain(4, List[EventType](
//      LocalEvent,
//      ReceiveEvent(7),
//      ReceiveEvent(6),
//      ReceiveEvent(9),
//      SendEvent(10)
//    ))
//
//    val system = new LamportTimestampSystem(List[ProcessEventChain] (
//      process1,
//      process2,
//      process3,
//    process4
//    ))
//
//    // when
//    val evaluatedProcesses: Seq[EvaluatedProcess] = system.evaluateTimeStamps()
//
//    // then
//
//    println(evaluatedProcesses(0).eventChain.map(e => { e.lamportTimestamp }))
//    println(evaluatedProcesses(1).eventChain.map(e => { e.lamportTimestamp }))
//    println(evaluatedProcesses(2).eventChain.map(e => { e.lamportTimestamp }))
//    println(evaluatedProcesses(3).eventChain.map(e => { e.lamportTimestamp }))
//  }
//
//  test("q15") {
//
//    // given
//    val process1 = new ProcessEventChain(1, List[EventType](
//      SendEvent(1),
//      LocalEvent,
//      ReceiveEvent(7),
//      SendEvent(8),
//      LocalEvent,
//      LocalEvent,
//      ReceiveEvent(10)
//    ))
//
//    val process2 = new ProcessEventChain(2, List[EventType](
//      ReceiveEvent(3),
//      SendEvent(6),
//      SendEvent(7)
//    ))
//
//
//    val process3 = new ProcessEventChain(3, List[EventType](
//      ReceiveEvent(2),
//      SendEvent(4),
//      SendEvent(3),
//      ReceiveEvent(1),
//      SendEvent(5),
//      LocalEvent,
//      ReceiveEvent(8),
//      SendEvent(9),
//      ReceiveEvent(6)
//    ))
//
//    val process4 = new ProcessEventChain(4, List[EventType](
//      SendEvent(2),
//      ReceiveEvent(4),
//      ReceiveEvent(5),
//      LocalEvent,
//      ReceiveEvent(9),
//      SendEvent(10)
//    ))
//
//    val system = new LamportTimestampSystem(List[ProcessEventChain] (
//      process1,
//      process2,
//      process3,
//      process4
//    ))
//
//    // when
//    val evaluatedProcesses: Seq[EvaluatedProcess] = system.evaluateTimeStamps()
//
//    // then
//
//    println(evaluatedProcesses(0).eventChain.map(e => { e.lamportTimestamp }))
//    println(evaluatedProcesses(1).eventChain.map(e => { e.lamportTimestamp }))
//    println(evaluatedProcesses(2).eventChain.map(e => { e.lamportTimestamp }))
//    println(evaluatedProcesses(3).eventChain.map(e => { e.lamportTimestamp }))
//  }
//
//}
