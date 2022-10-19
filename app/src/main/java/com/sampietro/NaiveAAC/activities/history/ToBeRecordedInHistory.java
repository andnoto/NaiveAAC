package com.sampietro.NaiveAAC.activities.history;

import java.util.List;

/**
 * <h1>ToBeRecordedInHistory</h1>
 *
 * <p><b>ToBeRecordedInHistory</b> abstract class for registration in history class.</p>
 * Refer to <a href="https://www.html.it/pag/62281/interfacce-list-e-queue-in-java/">www.html.it</a>
 * by <a href="https://www.html.it/author/azoia/">Alessandro Zoia</a>
 *
 * @see History
 * @see ToBeRecordedInHistoryImpl
 * @version     1.3, 05/05/22
 */
public abstract class ToBeRecordedInHistory<T> {
    protected List<T> voicesToBeRecordedInHistory;


    public abstract void add(T voiceToBeRecordedInHistory);
    public abstract void delete(T voiceToBeRecordedInHistory);


    public abstract int getNumberOfVoicesToBeRecordedInHistory();
    public abstract List<T> getVoicesToBeRecordedInHistory();
}
