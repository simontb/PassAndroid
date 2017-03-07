package org.ligi.passandroid.model.comparator

import org.ligi.passandroid.model.pass.Pass
import org.threeten.bp.ZonedDateTime

class UpcomingFirstComparator : PassByTimeComparator() {

    override fun compare(lhs: Pass, rhs: Pass): Int {
        val leftDate = extractPassDate(lhs)
        val rightDate = extractPassDate(rhs)

        /*
        If only one pass is a future event, return it first
        If both are in the future, sort them by date ascending
         */
        if (null != leftDate && isInTheFuture(leftDate)) {
            if (null != rightDate && isInTheFuture(rightDate)) return DirectionAwarePassByTimeComparator(DirectionAwarePassByTimeComparator.DIRECTION_DESC).compare(lhs, rhs)
            else return -1
        }
        if (null != rightDate && isInTheFuture(rightDate)) return 1

        /*
        If neither of the passes are in the future, return non-event passes first,
        then return the pass events by date descending
         */
        if (null == leftDate && null != rightDate) return -1
        if (null == rightDate && null != leftDate) return 1
        return DirectionAwarePassByTimeComparator(DirectionAwarePassByTimeComparator.DIRECTION_ASC).compare(lhs, rhs)
    }

    fun isInTheFuture(date: ZonedDateTime) : Boolean {
        val now = ZonedDateTime.now()
        return now < date
    }

}
