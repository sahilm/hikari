package org.example

import com.mysql.cj.jdbc.ConnectionImpl
import java.sql.Connection

class MyConnectionImpl(
    private val connection: Connection
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
