package com.csg.health.web.rest.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.LocalDate;

/**
 * Created by csg on 15/12/22.
 */
public class PointsPerWeek {
    private LocalDate week;
    private Integer points;
    public PointsPerWeek(LocalDate week, Integer points) {
        this.week = week;
        this.points = points;
    }
    public Integer getPoints() {
        return points;
    }
    public void setPoints(Integer points) {
        this.points = points;
    }

    public LocalDate getWeek() {
        return week;
    }
    public void setWeek(LocalDate week) {
        this.week = week;
    }
    @Override
    public String toString() {
        return "PointsThisWeek{"+
                "points="+points+
                ", week="+week+"}   ";
    }
}
