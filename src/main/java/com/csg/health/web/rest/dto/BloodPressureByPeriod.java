package com.csg.health.web.rest.dto;

import com.csg.health.domain.BloodPressure;

import java.util.List;

/**
 * Created by csg on 15/12/22.
 */
public class BloodPressureByPeriod {
    private String period;
    private List<BloodPressure> readings;

    public BloodPressureByPeriod(String period, List<BloodPressure> readings) {
        this.period = period;
        this.readings = readings;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public List<BloodPressure> getReadings() {
        return readings;
    }

    public void setReadings(List<BloodPressure> readings) {
        this.readings = readings;
    }

    @Override
    public String toString() {
        return "BloodPressureByPeriod{" +
                "period='" + period + '\'' +
                ", readings=" + readings +
                '}';
    }
}
