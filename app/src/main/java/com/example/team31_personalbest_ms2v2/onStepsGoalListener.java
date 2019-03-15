package com.example.team31_personalbest_ms2v2;

public class onStepsGoalListener implements GListener {
    Long goals;

    @Override
    public void onSuccess(Long num) {
        this.goals = num;
    }
}
