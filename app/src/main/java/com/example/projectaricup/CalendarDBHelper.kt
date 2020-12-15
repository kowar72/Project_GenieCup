package com.example.projectaricup

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.util.*
import kotlin.collections.ArrayList

class CalendarDBHelper(
    context: Context?,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version) {

    override fun onCreate(db: SQLiteDatabase?) {
        var sql_dayitem : String = "CREATE TABLE DAYITEM " + "(date INTEGER PRIMARY KEY,amount INTEGER,daycount INTEGER,cycle INTEGER)"
        var sql_cycle : String = "CREATE TABLE CYCLE " + "(id INTEGER PRIMARY KEY AUTOINCREMENT,start INTEGER,end INTEGER,length INTEGER)"
        db?.execSQL(sql_dayitem)
        db?.execSQL(sql_cycle)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldver: Int, newver: Int) {
        db?.execSQL("DROP TABLE IF EXISTS DAYITEM")
        db?.execSQL("DROP TABLE IF EXISTS CYCLE")
        onCreate(db)
    }

    fun insertDay(d : DayItem, amount : Int, daycount : Int, cycle : Int){
        var db : SQLiteDatabase = this.writableDatabase
        var contents : ContentValues = ContentValues()
        var year : Int = d.year
        var month : Int = d.month
        var date : Int = d.date
        var dateform = year * 10000 + month * 100 + date

        contents.put("date", dateform)
        contents.put("amount", amount)
        contents.put("daycount", daycount)
        contents.put("cycle", cycle)
        System.out.println("insert going")
        db.insert("DAYITEM", null, contents)
        db.close()
    }

    fun insertcycle(start: DayItem, end : DayItem, len : Int){
        var db : SQLiteDatabase = this.writableDatabase
        var contents : ContentValues = ContentValues()
        var year_s : Int = start.year
        var month_s : Int = start.month
        var date_s : Int = start.date
        var start_dateform = year_s * 10000 + month_s * 100 + date_s
        var year_e : Int = end.year
        var month_e : Int = end.month
        var date_e : Int = end.date
        var end_dateform = year_e * 10000 + month_e * 100 + date_e

        contents.put("start", start_dateform)
        contents.put("end", end_dateform)
        contents.put("length", len)

        db.insert("CYCLE", null, contents)
        db.close()
    }

    fun getlatestcycleId() : Int{
        var id : Int = 0
        var db : SQLiteDatabase = this.readableDatabase
        var query : String = "SELECT * FROM sqlite_sequence WHERE name ='CYCLE'"
        var cursor : Cursor = db.rawQuery(query, null)
        if(cursor.count != 0){
            if(cursor.moveToFirst()){
                id = cursor.getInt(cursor.getColumnIndex("seq"))
            }
        }
        db.close()
        cursor.close()
        return id
    }

    fun isAlreadyregistered(d: DayItem) : Boolean {
        var result : Boolean = false
        var db : SQLiteDatabase = this.readableDatabase
        var dateform = d.year * 10000 + d.month * 100 + d.date
        var query : String = "SELECT * FROM DAYITEM WHERE date ='"+dateform+"';"
        var cursor : Cursor = db.rawQuery(query, null)

        if(cursor.count != 0){
            result = true
            System.out.println("등록 불가합니다")
        }else{
            result = false
            System.out.println("등록 가능합니다")
        }
        db.close()
        cursor.close()
        return result
    }

    fun getMonthData(start : DayItem, end : DayItem) : ArrayList<DayItem>{
        var startdate : Int = start.year * 10000 + start.month * 100 + start.date
        var enddate : Int = end.year * 10000 + end.month * 100 + end.date
        var db : SQLiteDatabase = this.readableDatabase
        var query : String = "SELECT * FROM DAYITEM WHERE date BETWEEN '"+startdate+"' AND '"+enddate+"' ORDER BY date ASC;"
        var cursor : Cursor = db.rawQuery(query, null)
        var list = arrayListOf<DayItem>()
        if(cursor.count != 0){
            while(cursor.moveToNext()){
                var date = cursor.getInt(cursor.getColumnIndex("date"))
                var count = cursor.getInt(cursor.getColumnIndex("daycount"))
                var cycle = cursor.getInt(cursor.getColumnIndex("cycle"))
                var amount = cursor.getInt(cursor.getColumnIndex("amount"))
                var item = dateToDayItem(date)
                item.setdayCount(count)
                item.setCycleId(cycle)
                item.setQuantityBlood(amount)
                list.add(item)
            }
        }
        return list
    }

    fun getNearCycleDay(d : DayItem, range : Int) : DayItem?{
        var item : DayItem
        var enddate = d.year * 10000 + d.month * 100 + d.date
        var cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, d.year)
        cal.set(Calendar.MONTH, d.month-1)
        cal.set(Calendar.DATE, d.date)
        cal.add(Calendar.DATE, -range)
        var startdate : Int = (cal.get(Calendar.YEAR) * 10000) + (cal.get(Calendar.MONTH) + 1) * 100 + cal.get(Calendar.DATE)
        var db : SQLiteDatabase = this.readableDatabase
        var query : String = "SELECT * FROM DAYITEM WHERE date BETWEEN '"+startdate+"' AND '"+enddate+"' ORDER BY date DESC;"
        var cursor : Cursor = db.rawQuery(query, null)
        if(cursor.count != 0){
            cursor.moveToFirst()
            var count = cursor.getInt(cursor.getColumnIndex("daycount"))
            var cycle = cursor.getInt(cursor.getColumnIndex("cycle"))
            var amount = cursor.getInt(cursor.getColumnIndex("amount"))
            item = dateToDayItem(cursor.getInt(cursor.getColumnIndex("date")))
            item.setdayCount(count)
            item.setCycleId(cycle)
            item.setQuantityBlood(amount)
            db.close()
            cursor.close()
            return item
        }else{
            return null
        }
    }
    fun getAllDay() : ArrayList<DayItem>?{
        var daylist : ArrayList<DayItem> = arrayListOf()
        var db = this.readableDatabase
        var query = "SELECT * FROM DAYITEM ORDER BY date ASC;"
        var cursor = db.rawQuery(query, null)
        if(cursor.count != 0){
            while(cursor.moveToNext()){
                var date = cursor.getInt(cursor.getColumnIndex("date"))
                var amount = cursor.getInt(cursor.getColumnIndex("amount"))
                var daycount = cursor.getInt(cursor.getColumnIndex("daycount"))
                var cycle = cursor.getInt(cursor.getColumnIndex("cycle"))
                var day = dateToDayItem(date)
                day.cycleid = cycle
                day.quantity = amount
                day.dayCount = daycount
                daylist.add(day)
            }
            db.close()
            cursor.close()
            return daylist
        }
        db.close()
        cursor.close()
        return null
    }


    fun getAllCycle() : ArrayList<CycleItem>?{
        var cycleList : ArrayList<CycleItem> = arrayListOf<CycleItem>()
        var db : SQLiteDatabase = this.readableDatabase
        var query : String = "SELECT * FROM CYCLE ORDER BY start ASC;"
        var cursor : Cursor = db.rawQuery(query, null)
        if(cursor.count != 0){
            while(cursor.moveToNext()){
                var startday = cursor.getInt(cursor.getColumnIndex("start"))
                var endday = cursor.getInt(cursor.getColumnIndex("end"))
                var len = cursor.getInt(cursor.getColumnIndex("length"))
                cycleList.add(CycleItem(startday, endday, len))
            }
            db.close()
            cursor.close()
            return cycleList
        }
        db.close()
        cursor.close()
        return null
    }

    fun updateBlood(day : DayItem, amount : Int){
        var db : SQLiteDatabase = this.writableDatabase
        var contents : ContentValues = ContentValues()
        var date = dayItemToDate(day)
        contents.put("amount", amount)
        db.update("DAYITEM", contents, "date=$date", null)
        db.close()
    }

    fun updateCyclestretch(cycleid : Int, end: DayItem, len : Int){
        var db : SQLiteDatabase = this.writableDatabase
        var contents : ContentValues = ContentValues()

        contents.put("end", dayItemToDate(end))
        contents.put("length", len)
        db.update("CYCLE", contents, "id=$cycleid", null)
        db.close()
    }

    fun removeDayItemGreaterCount(cycleid : Int, count : Int){
        var db : SQLiteDatabase = this.writableDatabase
        var contents : ContentValues = ContentValues()
        db.delete("DAYITEM", "daycount > $count AND cycle = $cycleid", null)
        db.close()
    }


    fun removeCycle(cycleid : Int){
        var db : SQLiteDatabase = this.writableDatabase
        db.delete("DAYITEM", "cycle=$cycleid", null)
        db.delete("CYCLE", "id=$cycleid", null)
        db.close()
    }

    fun isLastDayofCycle(d : DayItem) : Boolean{
        var cycle = d.getCycleId()
        System.out.println("cycle id is $cycle")
        var date = dayItemToDate(d)
        var db : SQLiteDatabase = this.readableDatabase
        var query = "SELECT * FROM CYCLE WHERE id='$cycle';"
        var cursor : Cursor = db.rawQuery(query, null)
        cursor.moveToFirst()
        var lastday = cursor.getInt(cursor.getColumnIndex("end"))
        cursor.close()
        db.close()
        return lastday == date
    }




    private fun dateToDayItem(date : Int) : DayItem{
        var calculate : Int = date
        var year = calculate / 10000
        calculate -= (year * 10000)
        var month = calculate / 100
        calculate -= (month * 100)
        var day = calculate

        return DayItem(year, month, day, false)
    }

    private fun dayItemToDate(d : DayItem) : Int {
        return d.year * 10000 + d.month * 100 + d.date
    }
}