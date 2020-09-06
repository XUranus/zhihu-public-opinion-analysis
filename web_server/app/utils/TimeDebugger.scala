package utils

import java.util.Date

object TimeDebugger {

  var timerId:Int = 0
  var startTime:Long = 0
  var lastCheck:Long = 0

  def start(): TimeDebugger.type = {
    startTime = new Date().getTime
    timerId += 1
    lastCheck = startTime
    println(s"============ Time Debugger [${timerId}] start ================")
    this
  }

  def enterSection(sectionName:String): Unit = {
    println(s"===============  section: ${sectionName} ====================")
    lastCheck = new Date().getTime
  }

  def exitSection():TimeDebugger.type = {
    val duration:Long = new Date().getTime - lastCheck
    println(s"      duration: ${duration/1000.0 } seconds")
    println("===============================================================")
    this
  }

  def finish():Double = {
    println(s"      Time Debugger [${timerId}] finished  ")
    val totalDuration = (new Date().getTime - startTime)/1000.0
    println(s"      totalCost: ${totalDuration} seconds")
    totalDuration
  }

}
