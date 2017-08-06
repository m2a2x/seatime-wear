package com.maks.seatimewear.model;

import com.maks.seatimewear.network.PairDataFragment;

import java.util.ArrayList;

public class ConditionCollection {
    public ArrayList<PairDataFragment.ConditionItem> conditions;
    public ArrayList<PairDataFragment.ForecastItem> forecasts;

    public ConditionCollection(ArrayList<PairDataFragment.ConditionItem> _conditions, ArrayList<PairDataFragment.ForecastItem> _forecasts) {
        conditions = _conditions;
        forecasts = _forecasts;
    }
}