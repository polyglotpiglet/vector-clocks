package com.ojha.timestamps

import com.ojha.timestamps.datamodel._

/**
 * Created by alexandra on 07/03/15.
 */
class LamportTimestampSystem(processes: Seq[ProcessEventChain]) {


  def generateEmptyTimelines(): Map[Int, Array[Int]] = {

    var returnTimelines = Map[Int, Array[Int]]()

    processes.foreach(p => {
      returnTimelines += (p.id -> new Array[Int](p.eventChain.length))
    })

    returnTimelines
  }

  def generatedSenderLookup(): Map[Int, (EventType, Int, ProcessEventChain)] = {

    // event type, index in process chain, processid
    var lookupSender = Map[Int, (EventType, Int, ProcessEventChain)]()

    processes.foreach(p => {
      p.eventChain.zipWithIndex.foreach {
        case (SendEvent(x), i) => lookupSender = lookupSender + (x -> (SendEvent(x), i, p))
        case _ => // do nothing
      }
    })

    lookupSender
  }

  def evaluateTimeStamps(): Map[Int, Array[Int]] = {

    // msg_id => event, index in process chain, process number
    val senderLookup: Map[Int, (EventType, Int, datamodel.ProcessEventChain)] = generatedSenderLookup()
    val processTimelines: Map[Int, Array[Int]] = generateEmptyTimelines()

    processes.foreach( p => {

      val events: Array[Int] = processTimelines(p.id)

      p.eventChain.reverse.zipWithIndex.foreach{ case(e, i) => {

        val index = p.eventChain.length - i - 1
        if (events(index) == 0) {
          val lamportTimestamp = computeLamport(e, index, p, senderLookup)
          events(index) = lamportTimestamp
        }

      }}
    })
    processTimelines
  }

  def basecase(eventType: EventType, process: datamodel.ProcessEventChain, senderLookup: Map[Int, (EventType, Int, datamodel.ProcessEventChain)]): Int = {

    eventType match {
      case LocalEvent => 1
      case SendEvent(x) => 1
      case ReceiveEvent(x) => {
        val senderInfo: (EventType, Int, datamodel.ProcessEventChain) = senderLookup(x)
        1 + computeLamport(senderInfo._1, senderInfo._2, senderInfo._3, senderLookup)
      }
    }
  }

  def computeLamport(eventType: EventType, index: Int, process: datamodel.ProcessEventChain, senderLookup: Map[Int, (EventType, Int, datamodel.ProcessEventChain)]): Int = {

    if (index == 0) return basecase(eventType, process, senderLookup)

    val prevEventInSameProcess = process.eventChain(index - 1)

    eventType match {
      case LocalEvent => 1 + computeLamport(prevEventInSameProcess, index - 1, process, senderLookup)
      case SendEvent(x) => 1 + computeLamport(prevEventInSameProcess, index - 1, process, senderLookup)
      case ReceiveEvent(x) => {
        val senderInfo: (EventType, Int, datamodel.ProcessEventChain) = senderLookup(x)
        val senderLamport = computeLamport(senderInfo._1, senderInfo._2, senderInfo._3, senderLookup)
        
        val prevInChain = computeLamport(prevEventInSameProcess, index - 1, process, senderLookup) 
        math.max(senderLamport, prevInChain) + 1
      }
    }

  }

}
