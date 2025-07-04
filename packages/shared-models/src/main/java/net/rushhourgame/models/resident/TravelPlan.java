
package net.rushhourgame.models.resident;

public record TravelPlan(
    int origin, // Station ID
    int destination, // Destination Station ID
    float departureTime,
    int preferredRoute
) {}
