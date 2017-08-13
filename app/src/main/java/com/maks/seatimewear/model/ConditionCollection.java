package com.maks.seatimewear.model;

import java.util.ArrayList;

public class ConditionCollection {
    public ArrayList<ConditionItem> conditions;
    public ArrayList<ForecastItem> forecasts;

    public ConditionCollection(ArrayList<ConditionItem> _conditions, ArrayList<ForecastItem> _forecasts) {
        conditions = _conditions;
        forecasts = _forecasts;
    }
}