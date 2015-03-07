package com.ojha.lamport

import scala.collection.mutable

/**
 * Created by alexandra on 07/03/15.
 */
case class Process(id: Int, eventChain: Seq[EventType])
case class EvaluatedProcess(processId: Int, eventChain: Seq[Event])
