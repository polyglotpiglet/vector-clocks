package com.ojha.timestamps

import com.ojha.timestamps.datamodel._

/**
 * Created by alexandra on 08/03/15.
 */
class LamportEvaluator {


  /**
   * @param ps
   * @return Map (message_id -> (Process, index of corresponding send event in process)
   */
  def generatedSenderLookup(ps: Seq[CatProcess]): Map[Int, (CatProcess, Int)] = {

    val eventIndexInProcess = ps.flatMap(p => p.events.zipWithIndex.map(x => (x._1, x._2, p)))

    eventIndexInProcess.flatMap {
      case (Send(x), i, p) => List(x -> (p, i))
      case _ => Nil
    }.toMap
  }

  def lamportify(procs: Seq[CatProcess]): Seq[ResolvedCatProcess]  = {

    val senderLookup: Map[Int, (CatProcess, Int)] = generatedSenderLookup(procs)
    procs.map(p => new ResolvedCatProcess(p.id, evaluateTimestampsForProcess(p, senderLookup)))
  }

  def evaluateTimestampsForProcess(process: CatProcess, senderLookup: Map[Int, (CatProcess, Int)] ): Seq[(Event, CatTimestamp)] = {
    process.events.reverse.zipWithIndex.map {
      case (e, i) => calculateEventTimestamp(process, process.events.length - 1 - i, senderLookup)
    }.reverse
  }

  def calculateEventTimestamp(process: CatProcess, index: Int, senderLookup: Map[Int, (CatProcess, Int)]): (Event, CatLamportTimeStamp) = {

    val event = process.events(index)

    if (index == 0) return basecase(event, process, senderLookup)

    event match {
      case LocalEvent => (event, new CatLamportTimeStamp(1 + calculateEventTimestamp(process, index - 1, senderLookup)._2.value))
      case Send(x) => (event, new CatLamportTimeStamp(1 + calculateEventTimestamp(process, index - 1, senderLookup)._2.value))
      case Receive(x) => {

        val senderInfo: (CatProcess, Int) = senderLookup(x)
        val timestampInMsg = calculateEventTimestamp(senderInfo._1, senderInfo._2, senderLookup)
        val prevInProcessChain = calculateEventTimestamp(process, index - 1, senderLookup)
        val newTimestamp = 1 + math.max(timestampInMsg._2.value, prevInProcessChain._2.value)
        (event, new CatLamportTimeStamp(newTimestamp))
      }
    }
  }

  def basecase(event: Event, process: CatProcess, senderLookup: Map[Int, (CatProcess, Int)]): (Event, CatLamportTimeStamp) = {

    event match {
      case LocalEvent => (event, new CatLamportTimeStamp(1))
      case Send(x) => (event, new CatLamportTimeStamp(1))
      case Receive(x) => {
        val senderInfo: (CatProcess, Int) = senderLookup(x)
        val prevTimestamp = calculateEventTimestamp(senderInfo._1, senderInfo._2, senderLookup)
        (event, new CatLamportTimeStamp(1 + prevTimestamp._2.value))
      }
    }
  }




}



