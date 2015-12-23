package com.csg.health.web.rest.dto;

import com.csg.health.domain.Points;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by csg on 15/12/22.
 */
public class PointsPerMonth {
    private LocalDate month;
    private List<Points> points;

    public PointsPerMonth(LocalDate yearWithMonth, List<Points> points) {
        this.month = yearWithMonth;
        this.points = points;
    }


    public LocalDate getMonth() {
        return month;
    }

    public void setMonth(LocalDate month) {
        this.month = month;
    }

    public List<Points> getPoints() {
        return points;
    }

    public void setPoints(List<Points> points) {
        this.points = points;
    }

    @Override
    public String toString() {
        return "PointsPerMonth{" +
                "month=" + month +
                ", points=" + points +
                '}';
    }
}
