package com.haringeymobile.ukweather.data.objects;

import com.google.gson.annotations.SerializedName;
import com.haringeymobile.ukweather.R;

public class Wind {

    @SerializedName("deg")
    private int directionInDegrees;

    @SerializedName("speed")
    private double speed;

    public int getDirectionInDegrees() {
        return directionInDegrees;
    }

    public double getSpeed(WindSpeedMeasurementUnit windSpeedMeasurementUnit) {
        return windSpeedMeasurementUnit.convertSpeed(speed);
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void setDirectionInDegrees(int directionInDegrees) {
        this.directionInDegrees = directionInDegrees;
    }

    public int getCardinalDirectionStringResource() {
        if (directionInDegrees <= 11 || directionInDegrees >= 349) {
            return R.string.direction_n;
        } else if (directionInDegrees <= 33) {
            return R.string.direction_nne;
        } else if (directionInDegrees <= 56) {
            return R.string.direction_ne;
        } else if (directionInDegrees <= 78) {
            return R.string.direction_ene;
        } else if (directionInDegrees <= 101) {
            return R.string.direction_e;
        } else if (directionInDegrees <= 123) {
            return R.string.direction_ese;
        } else if (directionInDegrees <= 146) {
            return R.string.direction_se;
        } else if (directionInDegrees <= 168) {
            return R.string.direction_sse;
        } else if (directionInDegrees <= 191) {
            return R.string.direction_s;
        } else if (directionInDegrees <= 213) {
            return R.string.direction_ssw;
        } else if (directionInDegrees <= 236) {
            return R.string.direction_sw;
        } else if (directionInDegrees <= 258) {
            return R.string.direction_wsw;
        } else if (directionInDegrees <= 281) {
            return R.string.direction_w;
        } else if (directionInDegrees <= 303) {
            return R.string.direction_wnw;
        } else if (directionInDegrees <= 326) {
            return R.string.direction_nw;
        } else {
            return R.string.direction_nnw;
        }
    }

}
