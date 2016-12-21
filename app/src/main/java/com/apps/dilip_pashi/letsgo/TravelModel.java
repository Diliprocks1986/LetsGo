package com.apps.dilip_pashi.letsgo;

/**
 * Created by Dilip_pashi on 04-12-2016.
 */
public class TravelModel {
    private String PlaceName;
    private String StartDate;
    private String EndDate;

    public TravelModel() {
    }

    public TravelModel(String placeName, String startDate, String endDate) {

        PlaceName = placeName;
        StartDate = startDate;
        EndDate = endDate;
    }

    public String getPlaceName() {
        return PlaceName;
    }

    public void setPlaceName(String placeName) {
        PlaceName = placeName;
    }

    public String getStartDate() {
        return StartDate;
    }

    public void setStartDate(String startDate) {
        StartDate = startDate;
    }

    public String getEndDate() {
        return EndDate;
    }

    public void setEndDate(String endDate) {
        EndDate = endDate;
    }


}
