package services

import java.security.MessageDigest

import javax.inject.Inject
import play.api.db.Database

class Auth @Inject() (db: Database){

  //mail username userType
  def login(mail:String, password:String): Option[(String,String,String)] = {
    val conn = db.getConnection()
    val state = conn.createStatement()
    val hashedPassword = MD5(MD5(password))
    val result = state.executeQuery("select * from auth where mail = '{}' and password = '{}' ".format(mail,hashedPassword))
    if(result.getFetchSize==1) {
      val userType = result.getString("type")
      val username = result.getString("username")
      Some(mail,username,userType)
    } else {
      None
    }
  }

  def MD5(input: String): String = {
    var md5: MessageDigest = null
    try {
      md5 = MessageDigest.getInstance("MD5")
    }
    catch {
      case e: Exception => {
        e.printStackTrace()
        println(e.getMessage)
      }
    }
    val byteArray: Array[Byte] = input.getBytes
    val md5Bytes: Array[Byte] = md5.digest(byteArray)
    var hexValue: String = ""
    var i: Integer = 0
    for (i <- md5Bytes.indices) {
      val str: Int = (md5Bytes(i).toInt) & 0xff
      if (str < 16) {
        hexValue = hexValue + "0"
      }
      hexValue = hexValue + (Integer.toHexString(str))
    }
    hexValue.toString
  }

  def register(mail:String,username:String,password:String):Boolean = {
    val conn = db.getConnection()
    val state = conn.createStatement()
    val hashedPassword = MD5(MD5(password))
    val result = state.executeQuery("insert auth (mail,username,password) values ('{}','{}','{}')".format(mail,username,hashedPassword))
    true//Todo
  }

  def updateInfo(): Unit = {

  }


}