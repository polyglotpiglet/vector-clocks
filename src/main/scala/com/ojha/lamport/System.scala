package com.ojha.lamport

/**
 * Created by alexandra on 07/03/15.
 */
class System(processes: Seq[Process]) {


  def generatedSenderLookup(): Map[Int, (EventType, Int, Process)] = {

    // event type, index in process chain, processid
    var lookupSender = Map[Int, (EventType, Int, Process)]()

    processes.foreach(p => {
      p.eventChain.zipWithIndex.foreach {
        case (SendEvent(x), i) => lookupSender = lookupSender + (x -> (SendEvent(x), i, p))
        case _ => // do nothing
      }
    })

    lookupSender
  }

  def evaluateLamportTimestamps(): Seq[EvaluatedProcess] = {

    // msg_id => event, index in process chain, process number
    val senderLookup: Map[Int, (EventType, Int, Process)] = generatedSenderLookup()
    var evaluatedProcesses: List[EvaluatedProcess] = List[EvaluatedProcess]()

    processes.foreach( p => {
      val process = p
      val numberOfEventsInProcess = p.eventChain.length

      var events: List[Event] = List[Event]()

      process.eventChain.reverse.zipWithIndex.foreach{ case(e, i) => {
        val index = numberOfEventsInProcess - i - 1
        val lamportTimestamp = computeLamport(e, index, process, senderLookup)
        events = new Event(e, lamportTimestamp) :: events
      }}

      evaluatedProcesses = new EvaluatedProcess(process.id, events) :: evaluatedProcesses
    })

    evaluatedProcesses.reverse
  }


  def basecase(eventType: EventType, process: Process, senderLookup: Map[Int, (EventType, Int, Process)]): Int = {

    eventType match {
      case LocalEvent => 1
      case SendEvent(x) => 1
      case ReceiveEvent(x) => {
        val senderInfo: (EventType, Int, Process) = senderLookup(x)
        1 + computeLamport(senderInfo._1, senderInfo._2, senderInfo._3, senderLookup)
      }
    }
  }

  def computeLamport(eventType: EventType, index: Int, process: Process, senderLookup: Map[Int, (EventType, Int, Process)]): Int = {

    if (index == 0) return basecase(eventType, process, senderLookup)

    val prevEventInSameProcess = process.eventChain(index - 1)

    eventType match {
      case LocalEvent => 1 + computeLamport(prevEventInSameProcess, index - 1, process, senderLookup)
      case SendEvent(x) => 1 + computeLamport(prevEventInSameProcess, index - 1, process, senderLookup)
      case ReceiveEvent(x) => {
        val senderInfo: (EventType, Int, Process) = senderLookup(x)
        val senderLamport = computeLamport(senderInfo._1, senderInfo._2, senderInfo._3, senderLookup)
        
        val prevInChain = computeLamport(prevEventInSameProcess, index - 1, process, senderLookup) 
        math.max(senderLamport, prevInChain) + 1
      }
    }

  }

}
