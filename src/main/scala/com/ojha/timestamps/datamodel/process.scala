package com.ojha.timestamps.datamodel

/**
 * Created by alexandra on 07/03/15.
 */

case class CatProcess(id: Int, events: Seq[Event])
case class ResolvedCatProcess(id: Int, events: Seq[(Event, CatTimestamp)])