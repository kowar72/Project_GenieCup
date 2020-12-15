package com.example.projectaricup

import java.util.*

class CycleItem{
    var startday : Int = 0
    var endday : Int = 0
    var length : Int = 0

    constructor(startday : Int, endday : Int, len : Int){
        this.startday = startday
        this.endday = endday
        this.length = len
    }

    constructor(startday : DayItem, endday : DayItem){
        this.startday = dayItemToDate(startday)
        this.endday = dayItemToDate(endday)
        this.length = getlength(startday, endday)
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

    private fun getlength(startday: DayItem, endday: DayItem) : Int {
        var count : Int = 1
        if(startday.isSameDay(endday)){
            return count
        }
        var cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, startday.year)
        cal.set(Calendar.MONTH, startday.month-1)
        cal.set(Calendar.DATE, startday.date)
        var item = DayItem(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DATE), false)
        while(!endday.isSameDay(item)){
            count++
            cal.add(Calendar.DATE, 1)
            item = DayItem(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DATE), false)
        }
        return count

    }
}