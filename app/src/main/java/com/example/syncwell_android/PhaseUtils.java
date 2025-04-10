package com.example.syncwell_android;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class PhaseUtils {

    // Inner class to hold phase details
    public static class PhaseDetails {
        public String phase;
        public String dietTips;
        public String exerciseTips;

        public PhaseDetails(String phase, String dietTips, String exerciseTips) {
            this.phase = phase;
            this.dietTips = dietTips;
            this.exerciseTips = exerciseTips;
        }
    }

    /**
     * Calculates the current cycle phase based on the period start date and cycle length.
     *
     * @param periodStart The start date of the period (Calendar instance).
     * @param cycleLength The total length of the cycle (in days).
     * @return A PhaseDetails object containing phase name and recommendations.
     */
    public static PhaseDetails calculatePhase(Calendar periodStart, int cycleLength) {
        // Get current date
        Calendar today = Calendar.getInstance();

        // Calculate difference in days
        long diffMillis = today.getTimeInMillis() - periodStart.getTimeInMillis();
        long diffDays = TimeUnit.MILLISECONDS.toDays(diffMillis);
        int cycleDay = (int) ((diffDays % cycleLength) + 1);

        // Determine phase and recommendations based on cycle day
        if (cycleDay >= 1 && cycleDay <= 5) {
            return new PhaseDetails(
                    "Menstrual",
                    "Focus on iron-rich foods like spinach, lentils and red meat.",
                    "Rest, gentle yoga, and stretching can be beneficial."
            );
        } else if (cycleDay >= 6 && cycleDay <= 13) {
            return new PhaseDetails(
                    "Follicular",
                    "Include proteins, fresh fruits, and vegetables.",
                    "Great time for strength training and cardio."
            );
        } else if (cycleDay >= 14 && cycleDay <= 16) {
            return new PhaseDetails(
                    "Ovulatory",
                    "Eat zinc-rich and high-fiber foods; stay well hydrated.",
                    "Ideal for high-intensity interval training (HIIT) workouts."
            );
        } else {  // cycleDay 17 to cycleLength
            return new PhaseDetails(
                    "Luteal",
                    "Focus on magnesium-rich foods like bananas, dark chocolate, and leafy greens.",
                    "Opt for low-intensity workouts like Pilates or yoga."
            );
        }
    }
}


