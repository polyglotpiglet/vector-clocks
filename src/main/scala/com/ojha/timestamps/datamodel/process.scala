package com.ojha.timestamps.datamodel

/**
 * Created by alexandra on 07/03/15.
 */
case class ProcessEventChain(id: Int, eventChain: Seq[EventType])
case class ProcessTimeline(id: Int, clockSequence: Array[Int])

case class EvaluatedProcess(processId: Int, eventChain: Seq[Event])

