package com.ojha.timestamps

import com.ojha.timestamps.datamodel._

/**
 * Created by alexandra on 07/03/15.
 */
class LamportTimestampSystem(processes: Seq[ProcessEventChain]) {


  def generateEmptyTimelines(): Map[Int, Array[Int]] = {
    processes.map(p => p.id -> new Array[Int](p.eventChain.length)).toMap
  }

  def generatedSenderLookup(): Map[Int, (ProcessEventChain, Int)] = {

    val eventIndexInProcess = processes.flatMap(p => p.eventChain.zipWithIndex.map(x => (x._1, x._2, p)))

    eventIndexInProcess.flatMap {
      case (SendEvent(x), i, p) => List(x -> (p, i))
      case _ => Nil
    }.toMap
  }

  def evaluateTimeStamps(): Map[Int, Array[Int]] = {

    // msg_id => event, index in process chain, process number
    val senderLookup: Map[Int, (ProcessEventChain, Int)] = generatedSenderLookup()
    val processTimelines: Map[Int, Array[Int]] = generateEmptyTimelines()

    processes.foreach( p => {

      val events: Array[Int] = processTimelines(p.id)

      p.eventChain.reverse.zipWithIndex.foreach{ case(e, i) => {

        val index = p.eventChain.length - i - 1
        val lamportTimestamp = computeLamport(e, index, p, senderLookup)
        events(index) = lamportTimestamp

      }}
    })
    processTimelines
  }

  def basecase(eventType: EventType, process: ProcessEventChain, senderLookup: Map[Int, (ProcessEventChain, Int)]): Int = {

    eventType match {
      case LocalEvent => 1
      case SendEvent(x) => 1
      case ReceiveEvent(x) => {
        val senderInfo= senderLookup(x)
        1 + computeLamport(senderInfo._1.eventChain(senderInfo._2), senderInfo._2, senderInfo._1, senderLookup)
      }
    }
  }

  def computeLamport(eventType: EventType, index: Int, process: datamodel.ProcessEventChain, senderLookup: Map[Int, (ProcessEventChain, Int)]): Int = {

    if (index == 0) return basecase(eventType, process, senderLookup)

    val prevEventInSameProcess = process.eventChain(index - 1)

    eventType match {
      case LocalEvent => 1 + computeLamport(prevEventInSameProcess, index - 1, process, senderLookup)
      case SendEvent(x) => 1 + computeLamport(prevEventInSameProcess, index - 1, process, senderLookup)
      case ReceiveEvent(x) => {
        val senderInfo: (ProcessEventChain, Int) = senderLookup(x)
        val senderLamport = computeLamport(senderInfo._1.eventChain(senderInfo._2), senderInfo._2, senderInfo._1, senderLookup)
        
        val prevInChain = computeLamport(prevEventInSameProcess, index - 1, process, senderLookup) 
        math.max(senderLamport, prevInChain) + 1
      }
    }
  }
}
