package com.sampietro.NaiveAAC.activities.history

/**
 * <h1>ToBeRecordedInHistory</h1>
 *
 *
 * **ToBeRecordedInHistory** abstract class for registration in history class.
 * Refer to [www.html.it](https://www.html.it/pag/62281/interfacce-list-e-queue-in-java/)
 * by [Alessandro Zoia](https://www.html.it/author/azoia/)
 *
 * @see History
 *
 * @see ToBeRecordedInHistoryImpl
 *
 * @version     4.0, 09/09/2023
 */
abstract class ToBeRecordedInHistory<T> {
    @JvmField
    var voicesToBeRecordedInHistory: MutableList<T>? = null
    abstract fun add(voiceToBeRecordedInHistory: T)
    abstract fun delete(voiceToBeRecordedInHistory: T)
    abstract fun getNumberOfVoicesToBeRecordedInHistory(): Int
    abstract fun getListVoicesToBeRecordedInHistory(): MutableList<T>?
}