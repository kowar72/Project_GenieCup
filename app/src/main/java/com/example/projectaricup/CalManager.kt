package com.example.projectaricup

import java.util.*
import kotlin.collections.ArrayList

class CalManager {
    private lateinit var helper : CalendarDBHelper
    var period : Int = 5
    var cycle : Int = 28
    var time = Calendar.getInstance()

    fun registerDBHelper(help : CalendarDBHelper){
        helper = help
    }

    fun  GetCalArray(year : Int, month : Int) : ArrayList<DayItem>{
        var list : ArrayList<DayItem> = arrayListOf()
        time.set(Calendar.YEAR, year)
        time.set(Calendar.MONTH, month-1)
        time.set(Calendar.DATE, 1)

        val start_day_of_week = time.get(Calendar.DAY_OF_WEEK) //달 1일의 요일 (1:일요일, 7:토요일) -> 달력에서 지난달 표기를 위해 사용
        var last_month_days = start_day_of_week - 1
        while(last_month_days > 0){
            time.add(Calendar.DATE, -last_month_days)
            list.add(DayItem(time.get(Calendar.YEAR), time.get(Calendar.MONTH) + 1, time.get(Calendar.DATE), false))
            time.add(Calendar.DATE, last_month_days)
            last_month_days--
        }
        var end_date = time.getActualMaximum(Calendar.DAY_OF_MONTH) //달의 마지막 일 (30,31 등)
        for(i in 1..end_date){
            list.add(DayItem(year, month, i, true))
        }

        time.set(Calendar.DATE, end_date)
        val end_day_of_week = time.get(Calendar.DAY_OF_WEEK) //달의 말일의 요일 (1:일요일, 7:토요일) -> 달력에서 다음달 표기를 위해 사용
        var after_month_days = 7 - end_day_of_week
        while(after_month_days > 0){
            time.add(Calendar.DATE, 1)
            list.add(DayItem(time.get(Calendar.YEAR),time.get(Calendar.MONTH)+1, time.get(Calendar.DATE), false))
            after_month_days--
        }
        return list
    }

    fun insertDayitem(day : DayItem) : Boolean{
        var result : Boolean
        var cycleId : Int = helper.getlatestcycleId() + 1
        var len : Int = 0
        var start : DayItem = day
        var end : DayItem = day
        time.set(Calendar.YEAR, day.year)
        time.set(Calendar.MONTH, day.month-1)
        time.set(Calendar.DATE, day.date)
        for(i in 1..period){
            var item : DayItem = DayItem(time.get(Calendar.YEAR),time.get(Calendar.MONTH)+1, time.get(Calendar.DATE),false)
            if(!helper.isAlreadyregistered(item)){
                helper.insertDay(item, 0, i, cycleId)
                len = i
                end = item
                time.add(Calendar.DATE, 1)
            }else{
                break
            }
        }
        if(len > 0){
            helper.insertcycle(start, end, len)
            result = true
        }else{
            result = false
        }

        return result
    }

    fun getDataList(list : ArrayList<DayItem>) : ArrayList<DayItem>{
        var newList : ArrayList<DayItem> = list
        var start : DayItem = list[0]
        var end : DayItem = list[list.size - 1]
        var dblist : ArrayList<DayItem> = helper.getMonthData(start, end)
        if(dblist.isEmpty()){
            return list
        }
        var index : Int = 0
        for(i in newList){
            if(dblist[index].isSameDay(i)){
                i.setQuantityBlood(dblist[index].quantity)
                i.setCycleId(dblist[index].getCycleId())
                i.setdayCount(dblist[index].getdayCount())
                index++
                if(index == dblist.size){
                    break
                }
            }
        }

        return newList
    }

    fun findNearCycle(d : DayItem, range : Int) : DayItem?{
        var date : Int = d.year * 10000 + d.month * 100 + d.date
        var nearest = helper.getNearCycleDay(d, 5)
        if(nearest == null){
            return null
        }
        return nearest
    }

    fun stretchCycle(select : DayItem, near : DayItem){
        var lastcount = near.getdayCount()
        var cycleid = near.getCycleId()
        time.set(Calendar.YEAR, near.year)
        time.set(Calendar.MONTH, near.month-1)
        time.set(Calendar.DATE, near.date)
        var item : DayItem = makeDayItem(time)
        while(!select.isSameDay(item)){
            lastcount++
            time.add(Calendar.DATE, 1)
            item = makeDayItem(time)
            helper.insertDay(item,0, lastcount, cycleid)
        }
        helper.updateCyclestretch(cycleid, select, lastcount)
    }

    fun deleteCycle(select : DayItem){
        var cycleid = select.getCycleId()
        helper.removeCycle(cycleid)
    }

    fun reduceCycle(select : DayItem) : Boolean{
        if(helper.isLastDayofCycle(select)){
            return false
        }else{
            var count = select.dayCount
            var cycle = select.getCycleId()
            helper.removeDayItemGreaterCount(cycle , count)
            helper.updateCyclestretch(cycle, select, count)
            return true
        }

    }

    fun updateBloodAmount(d: DayItem, a : Int) : Boolean{
        helper.updateBlood(d, a)
        return true
    }

    private fun makeDayItem(cal : Calendar) : DayItem{
        return DayItem(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DATE), false)
    }

    fun getDayInfo(d : DayItem) : String{
        var cycles = helper.getAllCycle()
        var cyclelen = getMeanCycle()
        var meanlen = getMeanPeriodLen()

        if(cycles == null){
            return ""
        }

        var selected = dayItemToDate(d)
        for(i in cycles){
            if(selected >= i.startday && selected <= i.endday){
                var differ = betweenDay(i.startday,selected )
                return "생리 시작 ${differ+1}일"
            }
        }
        var last : Int = 0
        for(i in cycles){
            if(selected > i.endday){
                 last = i.startday
            }
        }
        if(last == 0){
            last = selected
        }
        var differ = betweenDay(last, selected)
        System.out.println("differ : $differ last : $last")
        if(differ >= cyclelen && differ > 0){
            var value = differ / cyclelen
            value--
            differ -= (value * cyclelen)

            if(differ in cyclelen..(cyclelen+meanlen-1)){
                return "생리 예정일"
            }
            differ = (cyclelen * 2) - differ
            if(differ in 13..15){
                return "배란일"
            }else if(differ >= 16){
                return "배란일 ${differ-15}일전"
            }else if(differ <= 12 && differ > 0){
                return "생리 시작 ${differ}일전"
            }

        }
        else if(differ < cyclelen + meanlen && differ > 0){
                var value = cyclelen - differ
                if(value in 13..15){
                    return "배란일"
                }
                if(value >= 16){
                    return "배란일 ${value-15}일전"
                }
                if(value <= 12 && value > 0){
                    return "생리 시작 ${value}일전"
                }

            }
        return ""

    }

    fun displayCycleInfo(day : DayItem) : String{
        var cycles = helper.getAllCycle()
        if(cycles == null) {
            return ""
        }
        if(cycles.size == 1){
            return ""
        }
        var date = dayItemToDate(day)
        for(i in 0..(cycles.size-1)){
            if(date >= cycles[i].startday && date <= cycles[i].endday){
                if(i == 0){
                    return ""
                }else{
                    var between = betweenDay(cycles[i-1].startday,cycles[i].startday)
                    return "지난달로 부터 ${between}일 주기로 시작했습니다."
                }
            }
        }
        return ""


    }

    fun reportdisplayRenderCircleInfo(day: DayItem) : String{
        var cycles = helper.getAllCycle()
        if(cycles == null){
            return "정보 없음"
        }
        var selected = dayItemToDate(day)
        for(i in cycles){
            if(selected >= i.startday && selected <= i.endday){
                var differ = betweenDay(i.startday,selected )
                return "생리 시작\n${differ+1}일"
            }
        }
        var daycal = dayItemToCalendar(dateToDayItem(cycles[cycles.size-1].startday))
        daycal.add(Calendar.DATE, getMeanCycle())
        var differ = betweenDay(selected, calendarToDate(daycal))
        if(differ >= 16){
            return "배란기\n${differ-15}일 전"
        }else if(differ in 13..15){
            when(differ){
                15->return "배란기\n1일"
                14->return "배란기\n2일"
                13->return "배란기\n3일"
            }
        }else if(differ < 13 && differ >0){
            return "생리 시작\n${differ}일 전"
        }else if(differ == 0){
            return "생리 예정\n당일"
        }
        return "생리 예정\n${-differ}일 늦음"

    }

    fun reportDisplayNextCycle(d : DayItem) : String{
        var today : Int = dayItemToDate(d)
        var cycles = helper.getAllCycle()
        if(cycles == null){
            return "생리 예정일\n정보없음"
        }

        var daycal = dayItemToCalendar(dateToDayItem(cycles[cycles.size-1].startday))
        daycal.add(Calendar.DATE, getMeanCycle())
        var differ = betweenDay(today,calendarToDate(daycal))
        if(differ == 0){
            return "생리 예정일\n예정일 당일"
        }else if(differ < 0){
            return "생리 예정일\n${-differ}일 초과"
        }
        return "생리 예정일\n${differ}일 후"
    }

    fun predictNextPeriod(monthList : ArrayList<DayItem>) : ArrayList<DayItem>{
        var cycleList : ArrayList<CycleItem>? = helper.getAllCycle()
        if(cycleList == null){
            return monthList
        }else{
            var lengthMean = getMeanPeriodLen()
            if(lengthMean == 0){
                return monthList
            }else{
                var cyclelen = getMeanCycle()
                var lastday : Calendar = dateToCalendar(cycleList[cycleList.size-1].startday)
                var nextstartday : Calendar = dateToCalendar(cycleList[0].startday)
                nextstartday.add(Calendar.DATE, cyclelen)

                for(i in monthList){
                    var difference = betweenDay(calendarToDate(lastday),dayItemToDate(i))

                    if(cyclelen > 18 && difference > cyclelen){
                        var value = difference / cyclelen
                        value--
                        difference -= (value * cyclelen)
                        difference = (cyclelen * 2) - difference

                            if(difference in 16..18){
                                i.isBeforeOvulutionday = true
                            }


                            if(difference in 13..15){
                                i.isOvulutionday = true
                            }


                            if(difference in 10..12){
                                i.isAfterOvulutionday = true
                            }

                    }
                    difference = betweenDay(calendarToDate(lastday),dayItemToDate(i))
                    if(cyclelen > 18 && difference < cyclelen && difference > 0){
                        val calculate = cyclelen - difference
                        if(calculate in 10..12){
                            i.isAfterOvulutionday = true
                        }else if(calculate in 13..15){
                            i.isOvulutionday = true
                        }else if(calculate in 16..18){
                            i.isBeforeOvulutionday = true
                        }
                    }

                    if(difference >= cyclelen && difference > 0){
                        if(difference > (cyclelen+lengthMean-1)){
                            var value = difference / cyclelen
                            value--
                            difference -=  (cyclelen * value)
                        }
                        if(difference >= cyclelen && (difference <= cyclelen+lengthMean-1)){
                            i.isCycleExpected = true
                        }
                    }


                }
            }
        }
        return monthList
    }

    fun getMeanPeriodLen() : Int{
        var cycleList : ArrayList<CycleItem>? = helper.getAllCycle()
        if(cycleList == null){
            return 0
        }
        var total : Int = 0
        var mean : Int = 0
        for(i in cycleList){
            total += i.length
        }
        mean = total/cycleList.size
        return mean
    }

    fun getMeanCycle() : Int{
        var cycleList : ArrayList<CycleItem>? = helper.getAllCycle()
        if(cycleList == null){
            return 0
        }

        if(cycleList.size == 1){
            return 28
        }

        var total : Int = 0
        var count : Int = 0
        for(i in 1..(cycleList.size-1)){
            total += betweenDay(cycleList[i-1].startday, cycleList[i].startday)
            count++
        }

        return total / count

    }

    fun getDayList() : ArrayList<DayItem>?{
        var days = helper.getAllDay()
        if(days == null){
            return null
        }
        return days
    }

    fun getReportString(today : Calendar) : String{
        var returnstr = "건강리포트: "
        val normalAmount = 120
        var amountCurrent = 0   //가장 마지막 주기 혈량
        var amountLastbefore = 0    //가장 마지막 주기 - 1 주기 혈량
        var cycles : ArrayList<CycleItem>? = helper.getAllCycle()
        var dayitems : ArrayList<DayItem>? = helper.getAllDay()
        if(cycles == null || dayitems == null){
            return "데이터가 없습니다. 캘린더에 주기를 입력해주세요."
        }
        var last = cycles[cycles.size-1]
        var count = 0

        for(i in dayitems){
            if(dayItemToDate(i) in last.startday..last.endday){
                amountCurrent += i.getQuantityBlood()
                count++
            }

        }
        if(amountCurrent >= 20 && amountCurrent < 120){
            returnstr += "이번 주기의 혈량이 정상범위 이내입니다."
        }
        if(amountCurrent < 20 &&  betweenDay(last.startday, last.endday)+1 == count && amountCurrent > 0){
            returnstr += "혈량이 너무 적습니다. 무배란성 출혈, 여성호르몬 결핍, 다난성난소 증후군 등 " +
                    "건강의 이상징후일 수 있습니다. 여성의학과 방문을 권유드립니다. "
        }
        if(amountCurrent in 120..140){
            returnstr += "혈량이 정상수치보다 높습니다. 건강상태에 유의할 필요가 있습니다."
        }
        if(amountCurrent > 140){
            returnstr += "혈량이 정상수치 이상입니다. 자궁 근종이 의심됩니다.\n여성의학과 방문을 권해드려요."
        }
        var onLastbefore : Boolean = false
        if(cycles.size >= 2 ){
            onLastbefore = true
            var lastbefore = cycles[cycles.size-2]
            for(i in dayitems){
                if(dayItemToDate(i) in lastbefore.startday..lastbefore.endday){
                    amountLastbefore += i.getQuantityBlood()
                }
            }
            if(amountLastbefore - amountCurrent in -20..20 && amountCurrent < 140 && amountCurrent > 20 && onLastbefore){
                returnstr+= "\n주기별 혈량 변동량이 정상수치 이내입니다."
            }
            if(amountLastbefore - amountCurrent < -20 && onLastbefore){
                returnstr += "\n지난 주기에 비해 혈량이 증가했습니다. 건강에 주의가 필요합니다."
            }
        }


        return returnstr

    }



    fun betweenDay(first : Int, second : Int) : Int{
        var day1 = dateToCalendar(first).timeInMillis
        var day2 = dateToCalendar(second).timeInMillis

        val difference = removeTime(day2) - removeTime(day1)

        return (difference / (24 * 60 * 60 * 1000)).toInt()
    }

    private fun removeTime(time : Long) : Long{         //캘린더 instance에서 시간, 분, 초, 밀리초 제외
        return Calendar.getInstance().apply{
            timeInMillis = time
            set(Calendar.HOUR_OF_DAY, 1)
            set(Calendar.MINUTE, 1)
            set(Calendar.SECOND, 1)
            set(Calendar.MILLISECOND,1)
        }.timeInMillis
    }



    private fun dateToCalendar(d : Int) : Calendar{
        var calculate : Int = d
        var year = calculate / 10000
        calculate -= (year * 10000)
        var month = calculate / 100
        calculate -= (month * 100)
        var day = calculate

        return Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month-1)
            set(Calendar.DATE, day)
        }
    }

    private fun calendarToDate(c : Calendar) : Int{
        return c.get(Calendar.YEAR) * 10000 + (c.get(Calendar.MONTH)+1) * 100 + c.get(Calendar.DATE)
    }

    private fun calendarToDayitem(c : Calendar) : DayItem{
        return DayItem(c.get(Calendar.YEAR), (c.get(Calendar.MONTH)+1), c.get(Calendar.DATE), false)
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

    private fun dayItemToCalendar(d : DayItem) : Calendar{
        return Calendar.getInstance().apply{
            set(Calendar.YEAR, d.year)
            set(Calendar.MONTH, d.month-1)
            set(Calendar.DATE, d.date)
        }
    }


}