package com.example.team31_personalbest_ms2v2;

import java.util.List;

/**
 * interface that allows observers to update themselves
 * when data has been retrieved by the subject they are observing
 */
public interface IDataRetrieverObserver {
    void onDataRetrieved(String label, List<String> dates, List<Integer> list);
}
