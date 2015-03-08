package com.ojha.timestamps.datamodel

/**
 * Created by alexandra on 08/03/15.
 */

trait CatTimestamp
case class CatLamportTimeStamp(value: Int) extends CatTimestamp
