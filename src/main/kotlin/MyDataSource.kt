package org.example

import java.sql.Connection
import javax.sql.DataSource

class MyDataSource(
    private val dataSource: DataSource
) : DataSource by dataSource {

    override fun getConnection(): Connection {
        return MyConnectionImpl(dataSource.connection)
    }

    override fun getConnection(username: String?, password: String?): Connection {
        return MyConnectionImpl(dataSource.getConnection(username, password))
    }
}
