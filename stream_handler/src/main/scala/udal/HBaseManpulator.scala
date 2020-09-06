package udal

import java.util

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.client._
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.hbase.{HBaseConfiguration, HColumnDescriptor, HConstants, HTableDescriptor, NamespaceDescriptor, TableName}
import utils.ConfigLoader.{ZOOKEEPER_PORT, ZOOKEEPER_QUORUM}

object HBaseManpulator {

  //initialize
  val conf: Configuration = HBaseConfiguration.create()
  conf.set(HConstants.ZOOKEEPER_QUORUM, ZOOKEEPER_QUORUM)
  conf.set(HConstants.ZOOKEEPER_CLIENT_PORT,ZOOKEEPER_PORT)

  val conn: Connection = ConnectionFactory.createConnection(conf)
  val admin: Admin = conn.getAdmin

  //accelerate
  var tableCache = new util.HashMap[String, Table]()


  def tableNameToTable(namespace:String, tableName:String): Option[Table] = {
    val tableNameObj = TableName.valueOf(namespace + ":" + tableName)
    if(tableCache.containsKey(namespace + ":" + tableName)) {
      Some(tableCache.get(namespace + ":" + tableName))
    } else if (admin.tableExists(tableNameObj)) {
      val table = conn.getTable(tableNameObj)
      tableCache.put(namespace + ":" + tableName, table)
      Some(table)
    } else {
      None
    }
  }

  def createNameSpace(nameSpace:String): Unit = {
    admin.createNamespace(NamespaceDescriptor.create(nameSpace).build())
  }

  def deleteNameSpace(nameSpace:String): Unit = {
    admin.deleteNamespace(nameSpace)
  }

  def createHTable(namespace:String, tableName:String, columnFamilies:String*): Boolean = {
    val tableNameObj = TableName.valueOf(namespace + ":" + tableName)
    if(!admin.tableExists(tableNameObj)) {
      val tableDescriptor = new HTableDescriptor(tableNameObj)
      columnFamilies.foreach(
        columnFamily => tableDescriptor.addFamily(new HColumnDescriptor(columnFamily))
      )
      admin.createTable(tableDescriptor)
      true
    } else {
      println("table [" + namespace + ":" + tableName + "] already exist!")
      false
    }
  }

  def deleteHTable(namespace:String, tableName:String): Boolean = {
    val tableNameObj = TableName.valueOf(namespace + ":" + tableName)
    if(admin.tableExists(tableNameObj)) {
      admin.disableTable(tableNameObj)
      admin.deleteTable(tableNameObj)
      true
    } else {
      println("table [" + namespace + ":" + tableName + "] doesn't exist!")
      false
    }
  }


  def insertData(namespace:String, tableName:String, rowKey:String, columnFamily:String, columnValuePairs:Array[(String,String)]): Boolean = {
    tableNameToTable(namespace, tableName) match {
      case Some(table) =>
        val put = new Put(rowKey.getBytes())
        columnValuePairs.foreach(pair=>{
          val (column, value) = pair
          put.addColumn(columnFamily.getBytes(), column.getBytes(), value.getBytes())
        })
        table.put(put)
        true
      case _ => false
    }
  }


  def insertData(namespace:String, tableName:String, rowKey:String, columnFamily:String, column:String, value:String): Boolean = {
    tableNameToTable(namespace, tableName) match {
      case Some(table) =>
        val put = new Put(rowKey.getBytes())
        put.addColumn(columnFamily.getBytes(), column.getBytes(), value.getBytes())
        table.put(put)
        true
      case _ => false
    }
  }

  def existRow(namespace:String, tableName:String, rowKey:String): Boolean = {
    tableNameToTable(namespace, tableName) match {
      case Some(table) =>
        val get = new Get(rowKey.getBytes())
        val result = table.get(get)
        result.getExists
      case _ => false
    }
  }

  def getRow(namespace:String, tableName:String, rowKey:String): Option[Result] ={
    tableNameToTable(namespace, tableName) match {
      case Some(table) =>
        val get = new Get(rowKey.getBytes())
        if(table.exists(get)) {
          val row = table.get(get)
          Some(row)
        } else {
          None
        }
      case _ => None
    }
  }

  def getCellData(namespace:String, tableName:String, rowKey:String, columnFamily:String, column:String): Option[String] ={
    tableNameToTable(namespace, tableName) match {
      case Some(table) =>
        val get = new Get(rowKey.getBytes())
        val result = table.get(get)
        if(result.getExists) {
          Some(Bytes.toString(result.getValue(columnFamily.getBytes(), column.getBytes())))
        } else {
          None
        }
      case _ => None
    }
  }

  def deleteRow(namespace:String, tableName:String, rowKey:String): Boolean = {
    tableNameToTable(namespace, tableName) match {
      case Some(table) =>
        val delete = new Delete(rowKey.getBytes())
        //delete.addColumn()
        table.delete(delete)
        true
      case _ => false
    }
  }

  def filterWithinRowKey(namespace:String, tableName:String, startRowKey:String, stopRowKey:String): Option[util.Iterator[Result]] = {
    val scan = new Scan()
    scan.setStartRow(startRowKey.getBytes())
    scan.setStopRow(stopRowKey.getBytes())

    tableNameToTable(namespace, tableName) match {
      case Some(table) =>
        val results = table.getScanner(scan)
        val iter = results.iterator()
        Some(iter)
      case _ => None
    }
  }

  def allRow(namespace:String, tableName:String):Option[util.Iterator[Result]] = {
    val scan = new Scan()
    tableNameToTable(namespace, tableName) match {
      case Some(table) =>
        val results = table.getScanner(scan)
        val iter = results.iterator()
        Some(iter)
      case _ => None
    }
  }

  //def filterByColumn(namespace:String, tableName:String): Unit = {}


}
