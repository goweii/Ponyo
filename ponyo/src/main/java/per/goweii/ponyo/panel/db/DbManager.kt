package per.goweii.ponyo.panel.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import per.goweii.ponyo.log.Ponlog

object DbManager {

    data class Db(
        val name: String,
        val path: String,
        val tables: MutableList<Table> = arrayListOf()
    )

    data class Table(
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
                                val table = Table(tableName)
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
        Ponlog.d { dbs }
    }

    fun readTable(table: Table) {
        var db: Db? = null
        for (d in dbs) {
            if (d.tables.contains(table)) {
                db = d
            }
        }
        db ?: return
        val list = arrayListOf<ArrayList<DbEntity>>()
        SQLiteDatabase.openDatabase(
            db.path,
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
                        val value = _cursor.getString(columnIndex)
                        val dbEntity = DbEntity(columnName, value)
                        dbEntityList.add(dbEntity)
                    }
                    list.add(dbEntityList)
                }
            }
        }
        Ponlog.d { list }
    }

}