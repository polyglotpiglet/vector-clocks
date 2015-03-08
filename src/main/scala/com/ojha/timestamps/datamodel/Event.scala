package com.ojha.timestamps.datamodel

/**
 * Created by alexandra on 07/03/15.
 */

trait Event
case object LocalEvent extends Event
case class Send(messageId: Int) extends Event
case class Receive(messageId: Int) extends Event
