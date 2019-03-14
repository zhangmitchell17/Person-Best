package com.example.team31_personalbest_ms2v2;

import java.util.List;

public interface IDataRetrieverObserver {
    void onDataRetrieved(String label, List<Integer> list);
}
