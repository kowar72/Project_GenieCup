package com.example.projectaricup

class DayItem (val year: Int, val month: Int, val date: Int, val isthismonth: Boolean){
    var quantity : Int = 0;
    var dayCount : Int = 0;
    var cycleid : Int = 0;
    var isOvulutionday : Boolean = false
    var isBeforeOvulutionday : Boolean = false
    var isAfterOvulutionday : Boolean = false
    var isCycleExpected : Boolean = false


    fun getQuantityBlood() : Int{
        return quantity
    }

    fun setQuantityBlood(quantity : Int) {
        this.quantity = quantity
    }

    fun setdayCount(count : Int){
        dayCount = count
    }

    fun getdayCount() : Int{
        return dayCount
    }

    fun setCycleId(id : Int){
        cycleid = id
    }

    fun getCycleId() : Int{
        return cycleid
    }

    fun isSameDay(day : DayItem) : Boolean{
        var result = false
        if(this.year == day.year && this.month == day.month && this.date == day.date){
            result = true
        }
        return result
    }
}