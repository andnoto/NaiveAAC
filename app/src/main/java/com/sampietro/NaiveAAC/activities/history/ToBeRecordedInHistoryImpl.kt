package com.sampietro.NaiveAAC.activities.history

import java.util.ArrayList

/**
 * <h1>ToBeRecordedInHistory</h1>
 *
 *
 * **ToBeRecordedInHistory** utility class for registration in history class.
 * Refer to [www.html.it](https://www.html.it/pag/62281/interfacce-list-e-queue-in-java/)
 * by [Alessandro Zoia](https://www.html.it/author/azoia/)
 *
 * @see History
 *
 * @see ToBeRecordedInHistory
 *
 * @see VoiceToBeRecordedInHistory
 *
 * @version     4.0, 09/09/2023
 */
class ToBeRecordedInHistoryImpl : ToBeRecordedInHistory<VoiceToBeRecordedInHistory?>() {
    init {
        voicesToBeRecordedInHistory = ArrayList()
    }

    override fun add(voiceToBeRecordedInHistory: VoiceToBeRecordedInHistory?) {
        voicesToBeRecordedInHistory!!.add(voiceToBeRecordedInHistory)
    }

    override fun delete(voiceToBeRecordedInHistory: VoiceToBeRecordedInHistory?) {
        voicesToBeRecordedInHistory!!.remove(voiceToBeRecordedInHistory)
    }

    override fun getNumberOfVoicesToBeRecordedInHistory(): Int {
        return voicesToBeRecordedInHistory!!.size
    }

    override fun getListVoicesToBeRecordedInHistory(): MutableList<VoiceToBeRecordedInHistory?> {
        val list : MutableList<VoiceToBeRecordedInHistory?> =
            voicesToBeRecordedInHistory!!.toList().toMutableList()
        return list
    }

}