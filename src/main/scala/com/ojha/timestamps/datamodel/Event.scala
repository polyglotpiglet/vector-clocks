package com.ojha.timestamps.datamodel

/**
 * Created by alexandra on 07/03/15.
 */
case class Event(eventType: EventType, lamportTimestamp: Int)

trait EventType
case object LocalEvent extends EventType
case class SendEvent(messageId: Int) extends EventType
case class ReceiveEvent(messageId: Int) extends EventType
