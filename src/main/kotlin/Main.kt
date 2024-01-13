package org.example

import com.mysql.cj.jdbc.MysqlDataSource
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.slf4j.LoggerFactory


/*
 * dns cache down to zero
 * ensure socketTimeout in mysql driver
 *
 */
fun main() {
    val logger = LoggerFactory.getLogger("Main")
    logger.info("hello")

    val mysqlDataSource = MysqlDataSource()
    mysqlDataSource.setURL("jdbc:mysql://localhost:3306/foo?connectTimeout=5000&socketTimeout=30000")
    mysqlDataSource.user = "root"
    mysqlDataSource.password = ""
    val myDataSource = MyDataSource(mysqlDataSource)
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
                    val preparedStatement = connection.prepareStatement(sql).apply {
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
