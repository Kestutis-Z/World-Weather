package com.haringeymobile.ukweather.data.objects;

import com.haringeymobile.ukweather.R;

public class WeatherConditionFinder {

    /**
     * Obtains the string resource id, associated with the OWM weather condition id. See
     * http://openweathermap.org/weather-conditions for full condition list.
     *
     * @param weatherId OWM weather condition id
     * @return string resource id for weather condition description
     */
    public static int findWeatherConditionStringResourceId(int weatherId) {

        switch (weatherId) {
            case 200:
                return R.string.wc_200;
            case 201:
                return R.string.wc_201;
            case 202:
                return R.string.wc_202;
            case 210:
                return R.string.wc_210;
            case 211:
                return R.string.wc_211;
            case 212:
                return R.string.wc_212;
            case 221:
                return R.string.wc_221;
            case 230:
                return R.string.wc_230;
            case 231:
                return R.string.wc_231;
            case 232:
                return R.string.wc_232;
            case 300:
                return R.string.wc_300;
            case 301:
                return R.string.wc_301;
            case 302:
                return R.string.wc_302;
            case 310:
                return R.string.wc_310;
            case 311:
                return R.string.wc_311;
            case 312:
                return R.string.wc_312;
            case 313:
                return R.string.wc_313;
            case 314:
                return R.string.wc_314;
            case 321:
                return R.string.wc_321;
            case 500:
                return R.string.wc_500;
            case 501:
                return R.string.wc_501;
            case 502:
                return R.string.wc_502;
            case 503:
                return R.string.wc_503;
            case 504:
                return R.string.wc_504;
            case 511:
                return R.string.wc_511;
            case 520:
                return R.string.wc_520;
            case 521:
                return R.string.wc_521;
            case 522:
                return R.string.wc_522;
            case 531:
                return R.string.wc_531;
            case 600:
                return R.string.wc_600;
            case 601:
                return R.string.wc_601;
            case 602:
                return R.string.wc_602;
            case 611:
                return R.string.wc_611;
            case 612:
                return R.string.wc_612;
            case 615:
                return R.string.wc_615;
            case 616:
                return R.string.wc_616;
            case 620:
                return R.string.wc_620;
            case 621:
                return R.string.wc_621;
            case 622:
                return R.string.wc_622;
            case 701:
                return R.string.wc_701;
            case 711:
                return R.string.wc_711;
            case 721:
                return R.string.wc_721;
            case 731:
                return R.string.wc_731;
            case 741:
                return R.string.wc_741;
            case 751:
                return R.string.wc_751;
            case 761:
                return R.string.wc_761;
            case 762:
                return R.string.wc_762;
            case 771:
                return R.string.wc_771;
            case 781:
                return R.string.wc_781;
            case 800:
                return R.string.wc_800;
            case 801:
                return R.string.wc_801;
            case 802:
                return R.string.wc_802;
            case 803:
                return R.string.wc_803;
            case 804:
                return R.string.wc_804;
            case 900:
                return R.string.wc_900;
            case 901:
                return R.string.wc_901;
            case 902:
                return R.string.wc_902;
            case 903:
                return R.string.wc_903;
            case 904:
                return R.string.wc_904;
            case 905:
                return R.string.wc_905;
            case 906:
                return R.string.wc_906;
            case 951:
                return R.string.wc_951;
            case 952:
                return R.string.wc_952;
            case 953:
                return R.string.wc_953;
            case 954:
                return R.string.wc_954;
            case 955:
                return R.string.wc_955;
            case 956:
                return R.string.wc_956;
            case 957:
                return R.string.wc_957;
            case 958:
                return R.string.wc_958;
            case 959:
                return R.string.wc_959;
            case 960:
                return R.string.wc_960;
            case 961:
                return R.string.wc_961;
            case 962:
                return R.string.wc_962;
            default:
                throw new IllegalArgumentException("Unexpected weatherId: " + weatherId);
        }

    }

}
