package org.example

import com.mysql.cj.jdbc.ConnectionImpl
import com.mysql.cj.jdbc.MysqlDataSource
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.slf4j.LoggerFactory
import java.sql.Connection
import javax.sql.DataSource

fun main() {
  val logger = LoggerFactory.getLogger("Main")
  val connectTimeout = 5000
  val socketTimeout = 30_000
  val mysqlDataSource = MysqlDataSource()
  mysqlDataSource.user = "alice"
  mysqlDataSource.password = "s3cret"

  val jdbcURL = "jdbc:mysql://127.0.0.1:3306/foobar_db?connectTimeout=$connectTimeout&socketTimeout=$socketTimeout"
  mysqlDataSource.setURL(jdbcURL)
  val myDataSource = WritableDataSource(mysqlDataSource)
  val config = HikariConfig()
  config.dataSource = myDataSource
//    config.keepaliveTime = 30000
//    config.connectionTestQuery = "set session transaction read write"
//    config.connectionInitSql = "select 1 for update"

  val ds = HikariDataSource(config)

  var counter = 3
  while (counter < 100) {
    Thread.sleep(1000)
    try {
      counter++
      val connection = ds.connection
      connection.use {
        val statement = connection.createStatement()
        statement.use {
          val sql = "insert into bar (hi) values (?)"
          val preparedStatement =
            connection.prepareStatement(sql).apply {
              setInt(1, counter)
            }
          val rowsAffected = preparedStatement.executeUpdate()
          logger.info("Rows inserted: $rowsAffected")
        }
      }
    } catch (e: Exception) {
      logger.error(e.message)
    }
  }
}

class WritableConnection(
  private val connection: Connection,
) : Connection by connection {
  override fun isValid(timeout: Int): Boolean {
    return connection.isValid(timeout) && !isReadOnly
  }

  override fun isReadOnly(): Boolean {
    if (connection.isReadOnly) return true

    if (connection is ConnectionImpl) {
      val readOnlySessionVar = connection.session.queryServerVariable("@@global.read_only")
      if (readOnlySessionVar != null) {
        return readOnlySessionVar.toInt() != 0
      }
    }

    return false
  }
}

class WritableDataSource(
  private val dataSource: DataSource,
) : DataSource by dataSource {
  override fun getConnection(): Connection {
    return WritableConnection(dataSource.connection)
  }

  override fun getConnection(
    username: String?,
    password: String?,
  ): Connection {
    return WritableConnection(dataSource.getConnection(username, password))
  }
}
