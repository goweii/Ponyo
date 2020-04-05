package per.goweii.ponyo.panel.db

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

object DbManager {

    data class Db(
        val name: String,
        val path: String,
        val tables: MutableList<Table> = mutableListOf()
    )

    data class Table(
        val dbName: String,
        val dbPath: String,
        val name: String
    )

    val dbs by lazy { mutableListOf<Db>() }

    fun findAllDb(context: Context) {
        val list = context.databaseList()
        for (name in list) {
            val path = context.getDatabasePath(name)
            try {
                SQLiteDatabase.openDatabase(
                    path.absolutePath,
                    null,
                    SQLiteDatabase.OPEN_READONLY
                ).use { _db ->
                    try {
                        val db = Db(name, path.absolutePath)
                        _db.rawQuery(
                            "SELECT name FROM sqlite_master WHERE type='table' ORDER BY name",
                            null
                        ).use { _cursor ->
                            while (_cursor.moveToNext()) {
                                val tableName = _cursor.getString(0)
                                val table = Table(name, path.absolutePath, tableName)
                                db.tables.add(table)
                            }
                        }
                        dbs.add(db)
                    } catch (e: Exception) {
                    }
                }
            } catch (e: Exception) {
            }
        }
    }

    fun readTable(table: Table): List<List<DbEntity>>? {
        val list = arrayListOf<ArrayList<DbEntity>>()
        SQLiteDatabase.openDatabase(
            table.dbPath,
            null,
            SQLiteDatabase.OPEN_READONLY
        ).use { _db ->
            _db.rawQuery(
                "SELECT * FROM ${table.name}", null
            ).use { _cursor ->
                while (_cursor.moveToNext()) {
                    val dbEntityList = arrayListOf<DbEntity>()
                    for (columnName in _cursor.columnNames) {
                        val columnIndex = _cursor.getColumnIndex(columnName)
                        val value = readValue(_cursor, columnIndex)
                        val dbEntity = DbEntity(columnName, value)
                        dbEntityList.add(dbEntity)
                    }
                    list.add(dbEntityList)
                }
            }
        }
        return list
    }

    private fun readValue(cursor: Cursor, columnIndex: Int): String {
        try {
            val value = cursor.getBlob(columnIndex)
            return String(value)
        } catch (e: Exception) {
        }
        try {
            val value = cursor.getShort(columnIndex)
            return value.toString()
        } catch (e: Exception) {
        }
        try {
            val value = cursor.getInt(columnIndex)
            return value.toString()
        } catch (e: Exception) {
        }
        try {
            val value = cursor.getLong(columnIndex)
            return value.toString()
        } catch (e: Exception) {
        }
        try {
            val value = cursor.getFloat(columnIndex)
            return value.toString()
        } catch (e: Exception) {
        }
        try {
            val value = cursor.getDouble(columnIndex)
            return value.toString()
        } catch (e: Exception) {
        }
        try {
            val value = cursor.getString(columnIndex)
            return value ?: "null"
        } catch (e: Exception) {
        }
        return "null"
    }

}