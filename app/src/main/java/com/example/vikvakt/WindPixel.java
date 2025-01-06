package com.example.vikvakt;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class WindPixel {
    private double wind;
    private List<Integer> positions;
    private List<Double> windAtPosition;
    private List<Integer> height;

    public WindPixel(List<Integer> positions, List<Integer> height, double wind) {
        Log.d("WindPixel", "Creating WindPixel with positions: " + positions.toString() +
                ", height: " + height.toString() + ", wind: " + wind);
        this.positions = positions;
        this.height = height;
        this.windAtPosition = new ArrayList<>();
        this.wind = wind;
        windAtPosition = calculateWindEffect(height, wind);
    }

    public double getWind() {
        return wind;
    }

    public List<Integer> getPositions() {
        return positions;
    }

    public List<Double> getWindAtPosition() {
        return windAtPosition;
    }

    public List<Integer> getHeight() {
        return height;
    }

    private void calcWindAtPosition(){

    }
    // Metod för att beräkna vindens påverkan på varje punkt i höjdarrayen
    public static List<Double> calculateWindEffect(List<Integer> elevationData, double windSpeed) {
        int length = elevationData.size();
        List<Double> windEffects = new ArrayList<>();
        // Iterera över varje punkt i höjdarrayen
        for (int i = 0; i < length-1; i++) {
            // Hämta höjd och vindhastighet för denna punkt
            //double elevation = elevationData.get(i);

            // Beräkna dämpningen beroende på höjdskillnader
            double windEffect = calculateLocalWindEffect(i, elevationData, windSpeed);

            // Sätt det beräknade vindpåverkan i resultatarrayen
            windEffects.add(windEffect);
        }
        return windEffects;
    }

    // Metod för att beräkna vindens påverkan på en viss punkt
    private static double calculateLocalWindEffect(int index, List<Integer> elevationData, double windSpeed) {

        int length = elevationData.size();
        double windEffect = windSpeed;

        // Om indexet är första eller sista elementet i arrayen, behöver vi bara titta på en granne
        if (index > 0) {

            // Beräkna höjdskillnaden mellan nuvarande punkt och föregående punkt
            int elevationDifferencePrev = Math.abs(elevationData.get(index) - elevationData.get(index - 1));
            windEffect *= getDampingFactor(elevationDifferencePrev);
        }

        if (index < length - 1) {

            // Beräkna höjdskillnaden mellan nuvarande punkt och nästa punkt
            int elevationDifferenceNext = Math.abs(elevationData.get(index) - elevationData.get(index + 1));
            windEffect *= getDampingFactor(elevationDifferenceNext);
        }

        return windEffect;
    }

    // Funktion som returnerar en dämpningsfaktor baserat på höjdskillnad
    private static double getDampingFactor(int elevationDifference) {
        // Dämpningsfaktor beror på höjdskillnaden, högre skillnad ger mer dämpning
        return 1.0 / (1 + elevationDifference * 0.1); // Justera faktor efter behov
    }
}
