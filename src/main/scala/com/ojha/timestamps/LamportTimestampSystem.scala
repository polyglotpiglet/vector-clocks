package com.ojha.timestamps

import com.ojha.timestamps.datamodel
import com.ojha.timestamps.datamodel._

/**
 * Created by alexandra on 07/03/15.
 */
class LamportTimestampSystem(processes: Seq[datamodel.Process]) {


  def generatedSenderLookup(): Map[Int, (EventType, Int, datamodel.Process)] = {

    // event type, index in process chain, processid
    var lookupSender = Map[Int, (EventType, Int, datamodel.Process)]()

    processes.foreach(p => {
      p.eventChain.zipWithIndex.foreach {
        case (SendEvent(x), i) => lookupSender = lookupSender + (x -> (SendEvent(x), i, p))
        case _ => // do nothing
      }
    })

    lookupSender
  }

  def evaluateTimeStamps(): Seq[EvaluatedProcess] = {

    // msg_id => event, index in process chain, process number
    val senderLookup: Map[Int, (EventType, Int, datamodel.Process)] = generatedSenderLookup()
    var evaluatedProcesses: List[EvaluatedProcess] = List[EvaluatedProcess]()

    processes.foreach( p => {
      val process = p
      var events: List[Event] = List[Event]()

      process.eventChain.reverse.zipWithIndex.foreach{ case(e, i) => {
        val index = p.eventChain.length - i - 1
        val lamportTimestamp = computeLamport(e, index, process, senderLookup)
        events = new Event(e, lamportTimestamp) :: events
      }}

      evaluatedProcesses = new EvaluatedProcess(process.id, events) :: evaluatedProcesses
    })

    evaluatedProcesses.reverse
  }


  def basecase(eventType: EventType, process: datamodel.Process, senderLookup: Map[Int, (EventType, Int, datamodel.Process)]): Int = {

    eventType match {
      case LocalEvent => 1
      case SendEvent(x) => 1
      case ReceiveEvent(x) => {
        val senderInfo: (EventType, Int, datamodel.Process) = senderLookup(x)
        1 + computeLamport(senderInfo._1, senderInfo._2, senderInfo._3, senderLookup)
      }
    }
  }

  def computeLamport(eventType: EventType, index: Int, process: datamodel.Process, senderLookup: Map[Int, (EventType, Int, datamodel.Process)]): Int = {

    if (index == 0) return basecase(eventType, process, senderLookup)

    val prevEventInSameProcess = process.eventChain(index - 1)

    eventType match {
      case LocalEvent => 1 + computeLamport(prevEventInSameProcess, index - 1, process, senderLookup)
      case SendEvent(x) => 1 + computeLamport(prevEventInSameProcess, index - 1, process, senderLookup)
      case ReceiveEvent(x) => {
        val senderInfo: (EventType, Int, datamodel.Process) = senderLookup(x)
        val senderLamport = computeLamport(senderInfo._1, senderInfo._2, senderInfo._3, senderLookup)
        
        val prevInChain = computeLamport(prevEventInSameProcess, index - 1, process, senderLookup) 
        math.max(senderLamport, prevInChain) + 1
      }
    }

  }

}
