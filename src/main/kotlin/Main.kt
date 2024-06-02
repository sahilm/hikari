package org.example

import com.mysql.cj.jdbc.JdbcConnection
import com.mysql.cj.jdbc.MysqlDataSource
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.sql.Connection
import javax.sql.DataSource
import com.mysql.cj.jdbc.ConnectionImpl as MysqlJdbcConnectionImpl
import com.mysql.cj.jdbc.JdbcConnection as MySQLJdbcConnection

val logger: Logger = LoggerFactory.getLogger("Main")

suspend fun main() =
  coroutineScope {
    val connectTimeout = 5000
    val socketTimeout = 30_000
    val mysqlDataSource = MysqlDataSource()
    mysqlDataSource.user = "alice"
    mysqlDataSource.password = "s3cret"
    val jdbcURL = "jdbc:mysql://127.0.0.1:3306/foobar_db?connectTimeout=$connectTimeout&socketTimeout=$socketTimeout"
    mysqlDataSource.setURL(jdbcURL)
    val writableDataSource = WritableDataSource(mysqlDataSource)
    val config = HikariConfig()
    config.dataSource = writableDataSource
    val dataSource = HikariDataSource(config)

    setCounterToZero(dataSource.connection)

    launchWriters(
      dataSource,
      concurrentWriters = 100,
      delayRange = (0..250),
    )

    launchReaders(
      dataSource,
      concurrentReaders = 2,
      delayRange = (1000..3000),
    )
  }

private fun setCounterToZero(connection: Connection) {
  connection.use { conn ->
    conn.prepareStatement(
      """
      insert into
          stuff (id, counter)
      values
          (1, 0)
      on duplicate key
          update counter = 0;
      """.trimIndent(),
    ).use { stmt -> stmt.execute() }
  }
}

fun CoroutineScope.launchReaders(
  dataSource: HikariDataSource,
  concurrentReaders: Int,
  delayRange: IntRange,
) {
  launchAction(
    {
      val counter = selectCounter(it)
      logger.info("counter=$counter")
    },
    dataSource,
    concurrentReaders,
    delayRange,
  )
}

private fun CoroutineScope.launchWriters(
  dataSource: HikariDataSource,
  concurrentWriters: Int,
  delayRange: IntRange,
) {
  launchAction(
    { incrementCounter(it) },
    dataSource,
    concurrentWriters,
    delayRange,
  )
}

private fun CoroutineScope.launchAction(
  action: (Connection) -> Unit,
  dataSource: DataSource,
  concurrency: Int,
  delayRange: IntRange,
) {
  repeat(concurrency) {
    launch {
      while (true) {
        try {
          action(dataSource.connection)
        } catch (e: Exception) {
          logger.debug(e.message)
        }
        val delayMillis = delayRange.random().toLong()
        delay(delayMillis)
      }
    }
  }
}

fun incrementCounter(connection: Connection) {
  connection.use { conn ->
    conn.prepareStatement(
      """
      insert into
          stuff (id, counter)
      values
          (1, 1)
      on duplicate key
          update counter = counter + 1;
      """.trimIndent(),
    ).use { it.executeUpdate() }
  }
}

fun selectCounter(connection: Connection): Long {
  return connection.use { conn ->
    conn.prepareStatement("select counter from stuff where id = 1").use { stmt ->
      stmt.executeQuery().use { rs ->
        rs.next()
        rs.getLong(1)
      }
    }
  }
}

class WritableConnection(
  private val connection: Connection,
) : Connection by connection {
  override fun isValid(timeout: Int): Boolean {
    logger.info("validating...")
    val result = connection.isValid(timeout) && !isReadOnly && !isAnyMySQLVarEnabled(MYSQL_GLOBAL_READ_ONLY_VARIABLES)
    logger.info("isValid=$result")
    return result
  }

  private fun isAnyMySQLVarEnabled(vars: List<String>): Boolean {
    if (!connection.isWrapperFor(JdbcConnection::class.java)) return false
    val mysqlJdbcConnection = connection.unwrap(MySQLJdbcConnection::class.java)
    if (mysqlJdbcConnection !is MysqlJdbcConnectionImpl) return false

    return vars.any { variable ->
      mysqlJdbcConnection.session.queryServerVariable(variable)?.toInt() != 0
    }
  }

  companion object {
    private val MYSQL_GLOBAL_READ_ONLY_VARIABLES =
      listOf(
        "@@global.innodb_read_only",
        "@@global.read_only",
        "@@global.super_read_only",
        "@@global.transaction_read_only",
      )
    private val logger = LoggerFactory.getLogger(WritableConnection::class.java)
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
