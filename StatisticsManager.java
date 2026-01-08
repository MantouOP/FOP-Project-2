import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Provides statistics and analytics for calendar events
 */
public class StatisticsManager {
    private EventManager eventManager;

    public StatisticsManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    /**
     * Display comprehensive event statistics
     */
    public void displayStatistics() {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("                     📊 CALENDAR STATISTICS 📊");
        System.out.println("=".repeat(70));

        List<Event> allEvents = eventManager.getAllEvents();
        
        if (allEvents.isEmpty()) {
            System.out.println("No events to analyze.");
            return;
        }
        
        displayBasicStats(allEvents);
        displayBusiestDayOfWeek(allEvents);
        displayPeakHoursAnalysis(allEvents);
        displayMonthlyStats(allEvents);
        displayEventDurationStats(allEvents);
        displayEventDistributionAnalysis(allEvents);
        displayProductivityInsights(allEvents);
        displayScheduleEfficiency(allEvents);
        displayUpcomingEventsStats(allEvents);

        System.out.println("\n" + "=".repeat(70) + "\n");
    }

    /**
     * Display peak hours analysis
     */
    private void displayPeakHoursAnalysis(List<Event> events) {
        System.out.println("\n--- 🕐 PEAK HOURS ANALYSIS ---");

        Map<Integer, Long> eventsByHour = events.stream()
                .collect(Collectors.groupingBy(
                    e -> e.getStartDateTime().getHour(),
                    Collectors.counting()
                ));

        Integer mostProductiveHour = eventsByHour.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        Integer leastProductiveHour = eventsByHour.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        if (mostProductiveHour != null) {
            System.out.printf("⬆️  Peak Hour: %02d:00 - %02d:59 (%d events)%n",
                mostProductiveHour, mostProductiveHour, eventsByHour.get(mostProductiveHour));
        }

        if (leastProductiveHour != null) {
            System.out.printf("⬇️  Quietest Hour: %02d:00 - %02d:59 (%d events)%n",
                leastProductiveHour, leastProductiveHour, eventsByHour.get(leastProductiveHour));
        }

        System.out.println("\nHourly Distribution:");
        eventsByHour.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    int hour = entry.getKey();
                    long count = entry.getValue();
                    String bar = "█".repeat((int) Math.min(count * 3, 50));
                    System.out.printf("  %02d:00 │ %s (%d)%n", hour, bar, count);
                });
    }

    /**
     * Display event duration statistics with distribution
     */
    private void displayEventDurationStats(List<Event> events) {
        System.out.println("\n--- ⏱️  EVENT DURATION STATISTICS ---");

        List<Double> durationsInMinutes = events.stream()
                .map(e -> (double) java.time.Duration.between(e.getStartDateTime(), e.getEndDateTime()).toMinutes())
                .collect(Collectors.toList());

        if (!durationsInMinutes.isEmpty()) {
            double avgDuration = durationsInMinutes.stream().mapToDouble(Double::doubleValue).average().orElse(0);
            double totalDuration = durationsInMinutes.stream().mapToDouble(Double::doubleValue).sum();
            double minDuration = durationsInMinutes.stream().mapToDouble(Double::doubleValue).min().orElse(0);
            double maxDuration = durationsInMinutes.stream().mapToDouble(Double::doubleValue).max().orElse(0);

            System.out.printf("📊 Average Duration: %.1f minutes (%.1f hours)%n", avgDuration, avgDuration / 60);
            System.out.printf("📊 Total Duration: %.1f hours (%.1f days)%n", totalDuration / 60, totalDuration / 1440);
            System.out.printf("📊 Shortest Event: %.0f minutes%n", minDuration);
            System.out.printf("📊 Longest Event: %.0f minutes%n", maxDuration);

            // Duration distribution
            long shortEvents = durationsInMinutes.stream().filter(d -> d < 30).count();
            long mediumEvents = durationsInMinutes.stream().filter(d -> d >= 30 && d < 120).count();
            long longEvents = durationsInMinutes.stream().filter(d -> d >= 120).count();

            System.out.println("\nDuration Distribution:");
            System.out.printf("  Quick (< 30 min): %d events (%.1f%%)%n", shortEvents, (shortEvents * 100.0) / events.size());
            System.out.printf("  Standard (30-120 min): %d events (%.1f%%)%n", mediumEvents, (mediumEvents * 100.0) / events.size());
            System.out.printf("  Long (> 120 min): %d events (%.1f%%)%n", longEvents, (longEvents * 100.0) / events.size());
        }
    }

    /**
     * Display event distribution analysis
     */
    private void displayEventDistributionAnalysis(List<Event> events) {
        System.out.println("\n--- 📈 EVENT DISTRIBUTION ANALYSIS ---");

        // Time of day distribution
        long morningEvents = events.stream().filter(e -> e.getStartDateTime().getHour() >= 6 && e.getStartDateTime().getHour() < 12).count();
        long afternoonEvents = events.stream().filter(e -> e.getStartDateTime().getHour() >= 12 && e.getStartDateTime().getHour() < 18).count();
        long eveningEvents = events.stream().filter(e -> e.getStartDateTime().getHour() >= 18 && e.getStartDateTime().getHour() < 24).count();
        long nightEvents = events.stream().filter(e -> e.getStartDateTime().getHour() >= 0 && e.getStartDateTime().getHour() < 6).count();

        System.out.println("⏰ Time of Day Distribution:");
        System.out.printf("  🌅 Morning (6AM-12PM): %d events (%.1f%%)%n", morningEvents, (morningEvents * 100.0) / events.size());
        System.out.printf("  ☀️  Afternoon (12PM-6PM): %d events (%.1f%%)%n", afternoonEvents, (afternoonEvents * 100.0) / events.size());
        System.out.printf("  🌆 Evening (6PM-12AM): %d events (%.1f%%)%n", eveningEvents, (eveningEvents * 100.0) / events.size());
        System.out.printf("  🌙 Night (12AM-6AM): %d events (%.1f%%)%n", nightEvents, (nightEvents * 100.0) / events.size());

        // Most active day
        Map<DayOfWeek, Long> eventsByDay = events.stream()
                .collect(Collectors.groupingBy(
                    e -> e.getStartDateTime().getDayOfWeek(),
                    Collectors.counting()
                ));

        DayOfWeek mostActiveDay = eventsByDay.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        if (mostActiveDay != null) {
            System.out.printf("\n📅 Most Active Day: %s (%d events)%n", mostActiveDay, eventsByDay.get(mostActiveDay));
        }
    }

    /**
     * Display schedule efficiency metrics
     */
    private void displayScheduleEfficiency(List<Event> events) {
        System.out.println("\n--- ⚡ SCHEDULE EFFICIENCY ---");

        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();
        LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = weekStart.plusDays(6);

        // Events per day metrics
        Map<LocalDate, Long> eventsByDate = events.stream()
                .collect(Collectors.groupingBy(
                    e -> e.getStartDateTime().toLocalDate(),
                    Collectors.counting()
                ));

        double avgEventsPerDay = (double) events.size() / (eventsByDate.size() > 0 ? eventsByDate.size() : 1);
        double avgEventsPerWeek = events.stream()
                .filter(e -> !e.getStartDateTime().toLocalDate().isBefore(weekStart) &&
                           !e.getStartDateTime().toLocalDate().isAfter(weekEnd))
                .count();

        System.out.printf("📍 Average Events per Day: %.1f%n", avgEventsPerDay);
        System.out.printf("📍 Average Events per Week: %.1f%n", avgEventsPerWeek);
        System.out.printf("📍 Total Days with Events: %d%n", eventsByDate.size());

        // Workload assessment
        if (avgEventsPerDay > 8) {
            System.out.println("\n⚠️  WARNING: Very Heavy Schedule!");
            System.out.println("   Recommendation: Consider rescheduling or delegating tasks.");
        } else if (avgEventsPerDay > 5) {
            System.out.println("\n✓ BUSY: Your schedule is quite full.");
            System.out.println("  Recommendation: Ensure adequate breaks between events.");
        } else if (avgEventsPerDay > 2) {
            System.out.println("\n✓ BALANCED: Good schedule balance.");
            System.out.println("  Recommendation: Well-managed schedule.");
        } else {
            System.out.println("\n✓ LIGHT: Your schedule has room for more events.");
            System.out.println("  Recommendation: Good opportunity for focused work.");
        }

        // Consecutive events analysis
        long consecutiveEventDays = eventsByDate.values().stream()
                .filter(count -> count >= 2)
                .count();

        System.out.printf("\n📊 Days with Multiple Events: %d%n", consecutiveEventDays);
    }

    /**
     * Display productivity insights
     */
    private void displayProductivityInsights(List<Event> events) {
        System.out.println("\n--- 💡 PRODUCTIVITY INSIGHTS ---");

        LocalDateTime now = LocalDateTime.now();

        // Past vs Upcoming ratio
        long completedEvents = events.stream()
                .filter(e -> e.getEndDateTime().isBefore(now))
                .count();

        long upcomingEvents = events.stream()
                .filter(e -> e.getStartDateTime().isAfter(now))
                .count();

        System.out.printf("✓ Completed Events: %d%n", completedEvents);
        System.out.printf("→ Upcoming Events: %d%n", upcomingEvents);

        if (upcomingEvents > 0) {
            double completionRate = (completedEvents * 100.0) / (completedEvents + upcomingEvents);
            System.out.printf("📊 Completion Rate: %.1f%%%n", completionRate);
        }

        // Most common event duration
        Map<String, Long> durationCategories = events.stream()
                .collect(Collectors.groupingBy(
                    e -> categorizeDuration(java.time.Duration.between(e.getStartDateTime(), e.getEndDateTime())),
                    Collectors.counting()
                ));

        System.out.println("\n📋 Most Common Event Type:");
        durationCategories.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(1)
                .forEach(entry -> System.out.printf("  %s: %d events%n", entry.getKey(), entry.getValue()));
    }

    /**
     * Display basic statistics
     */
    private void displayBasicStats(List<Event> events) {
        System.out.println("\n--- Basic Statistics ---");
        System.out.println("Total Events: " + events.size());
        
        long pastEvents = events.stream()
                .filter(e -> e.getEndDateTime().isBefore(LocalDateTime.now()))
                .count();
        
        long upcomingEvents = events.stream()
                .filter(e -> e.getStartDateTime().isAfter(LocalDateTime.now()))
                .count();
        
        long ongoingEvents = events.stream()
                .filter(e -> e.getStartDateTime().isBefore(LocalDateTime.now()) && 
                           e.getEndDateTime().isAfter(LocalDateTime.now()))
                .count();
        
        System.out.println("Past Events: " + pastEvents);
        System.out.println("Ongoing Events: " + ongoingEvents);
        System.out.println("Upcoming Events: " + upcomingEvents);
    }

    /**
     * Display busiest day of the week analysis
     */
    private void displayBusiestDayOfWeek(List<Event> events) {
        System.out.println("\n--- Busiest Day of the Week ---");
        
        Map<DayOfWeek, Long> eventsByDay = events.stream()
                .collect(Collectors.groupingBy(
                    e -> e.getStartDateTime().getDayOfWeek(),
                    Collectors.counting()
                ));
        
        DayOfWeek busiestDay = eventsByDay.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
        
        if (busiestDay != null) {
            System.out.println("Busiest Day: " + busiestDay + " (" + eventsByDay.get(busiestDay) + " events)");
            
            System.out.println("Events by day:");
            for (DayOfWeek day : DayOfWeek.values()) {
                long count = eventsByDay.getOrDefault(day, 0L);
                System.out.printf("  %s: %d events%n", day, count);
            }
        }
    }

    /**
     * Display monthly statistics
     */
    private void displayMonthlyStats(List<Event> events) {
        System.out.println("\n--- Monthly Statistics ---");
        
        Map<YearMonth, Long> eventsByMonth = events.stream()
                .collect(Collectors.groupingBy(
                    e -> YearMonth.from(e.getStartDateTime()),
                    Collectors.counting()
                ));
        
        eventsByMonth.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> 
                    System.out.printf("%s: %d events%n", entry.getKey(), entry.getValue())
                );
        
        // Find busiest month
        YearMonth busiestMonth = eventsByMonth.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
        
        if (busiestMonth != null) {
            System.out.println("Busiest Month: " + busiestMonth + " (" + eventsByMonth.get(busiestMonth) + " events)");
        }
    }


    /**
     * Display upcoming events statistics
     */
    private void displayUpcomingEventsStats(List<Event> events) {
        System.out.println("\n--- Upcoming Events Analysis ---");
        
        List<Event> upcomingEvents = events.stream()
                .filter(e -> e.getStartDateTime().isAfter(LocalDateTime.now()))
                .collect(Collectors.toList());
        
        if (upcomingEvents.isEmpty()) {
            System.out.println("No upcoming events.");
            return;
        }
        
        // Next event
        Event nextEvent = upcomingEvents.stream()
                .min((e1, e2) -> e1.getStartDateTime().compareTo(e2.getStartDateTime()))
                .orElse(null);
        
        if (nextEvent != null) {
            System.out.println("Next Event: " + nextEvent.getTitle());
            System.out.println("When: " + nextEvent.getStartDateTime());
            
            long hoursUntilNext = java.time.Duration.between(LocalDateTime.now(), nextEvent.getStartDateTime()).toHours();
            System.out.println("Time until next event: " + hoursUntilNext + " hours");
        }
        
        // Events this week
        LocalDate now = LocalDate.now();
        LocalDate weekEnd = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        
        long eventsThisWeek = upcomingEvents.stream()
                .filter(e -> !e.getStartDateTime().toLocalDate().isAfter(weekEnd))
                .count();
        
        System.out.println("Events this week: " + eventsThisWeek);
        
        // Events this month
        long eventsThisMonth = upcomingEvents.stream()
                .filter(e -> YearMonth.from(e.getStartDateTime()).equals(YearMonth.now()))
                .count();
        
        System.out.println("Events this month: " + eventsThisMonth);
    }

    /**
     * Display productivity insights (legacy method - kept for backward compatibility)
     */
    public void displayProductivityInsights() {
        List<Event> allEvents = eventManager.getAllEvents();
        if (!allEvents.isEmpty()) {
            displayProductivityInsights(allEvents);
        }
    }

    /**
     * Helper method to categorize event duration
     */
    private String categorizeDuration(java.time.Duration duration) {
        long minutes = duration.toMinutes();
        if (minutes < 15) {
            return "Quick (<15 min)";
        } else if (minutes < 30) {
            return "Short (15-30 min)";
        } else if (minutes < 60) {
            return "Medium (30-60 min)";
        } else if (minutes < 120) {
            return "Long (1-2 hours)";
        } else {
            return "Very Long (>2 hours)";
        }
    }
}

