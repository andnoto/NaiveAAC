package com.sampietro.NaiveAAC.activities.history;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>ToBeRecordedInHistory</h1>
 *
 * <p><b>ToBeRecordedInHistory</b> utility class for registration in history class.</p>
 * Refer to <a href="https://www.html.it/pag/62281/interfacce-list-e-queue-in-java/">www.html.it</a>
 * by <a href="https://www.html.it/author/azoia/">Alessandro Zoia</a>
 *
 * @see History
 * @see ToBeRecordedInHistory
 * @see VoiceToBeRecordedInHistory
 * @version     1.3, 05/05/22
 */
public class ToBeRecordedInHistoryImpl extends ToBeRecordedInHistory <VoiceToBeRecordedInHistory>  {
    public ToBeRecordedInHistoryImpl() {
        voicesToBeRecordedInHistory = new ArrayList<VoiceToBeRecordedInHistory>();
    }
    @Override
    public void add(VoiceToBeRecordedInHistory voiceToBeRecordedInHistory) {
        voicesToBeRecordedInHistory.add(voiceToBeRecordedInHistory);
    }

    @Override
    public void delete(VoiceToBeRecordedInHistory voiceToBeRecordedInHistory) {
        voicesToBeRecordedInHistory.remove(voiceToBeRecordedInHistory);
    }

    @Override
    public int getNumberOfVoicesToBeRecordedInHistory() {
        return voicesToBeRecordedInHistory.size();
    }

    @Override
    public List<VoiceToBeRecordedInHistory> getVoicesToBeRecordedInHistory() {
        return voicesToBeRecordedInHistory;
    }
}
