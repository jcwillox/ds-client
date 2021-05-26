package main.client.models;

public interface ServerState {
    String INACTIVE = "inactive";
    String BOOTING = "booting";
    String IDLE = "idle";
    String ACTIVE = "active";
    String UNAVAILABLE = "unavailable";
}
