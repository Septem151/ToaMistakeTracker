package com.toamistaketracker.detector;

import com.toamistaketracker.RaidRoom;
import com.toamistaketracker.RaidState;
import com.toamistaketracker.Raider;
import com.toamistaketracker.ToaMistake;
import lombok.NonNull;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.GraphicsObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.eventbus.EventBus;

import javax.inject.Inject;
import java.util.List;

/**
 * Interface for detecting mistakes during The Tombs of Amascut
 */
public abstract class BaseMistakeDetector {

    @Inject
    protected Client client;

    @Inject
    protected EventBus eventBus;

    @Inject
    protected RaidState raidState;

    /**
     * Used to tell a detector to start listening for events.
     */
    public void startup() {
        cleanup();
        eventBus.register(this);
    }

    /**
     * Shutdown and cleanup state. This is always called when the plugin is shutdown, or when a detector is finished.
     */
    public void shutdown() {
        eventBus.unregister(this);
        cleanup();
    }

    /**
     * Cleanup all relevant state in the detector. This is called during startup to reset state, and shutdown to cleanup
     */
    public abstract void cleanup();

    /**
     * Retrieve the raid room that this detector should startup in. A null value means *all* rooms
     *
     * @return The raid room that the detector should startup in, or null for *all* rooms
     */
    public abstract RaidRoom getRaidRoom();

    /**
     * Detects mistakes for the given raider.
     * This is called during handling the {@link net.runelite.api.events.GameTick} event, each tick.
     *
     * @param raider - The raider to detect mistakes for
     * @return The list of {@link ToaMistake} detected on this tick
     */
    public abstract List<ToaMistake> detectMistakes(@NonNull Raider raider);

    /**
     * This method allows detectors to handle some logic after all detectMistakes methods have been invoked
     * for this {@link net.runelite.api.events.GameTick}. Commonly, this is to cleanup state from after this tick.
     */
    public void afterDetect() {
        cleanup();
    }

    /**
     * Determines whether or not this detector is currently detecting mistakes. Commonly this is by checking the current
     * {@link RaidRoom} in the {@link RaidState}
     *
     * @return True if the detector is detecting mistakes, else false
     */
    public boolean isDetectingMistakes() {
        if (getRaidRoom() == null) { // null means *all* rooms
            return raidState.isInRaid();
        }

        return raidState.getCurrentRoom() == getRaidRoom();
    }

    protected WorldPoint getWorldPoint(Actor actor) {
        return WorldPoint.fromLocal(client, actor.getLocalLocation());
    }

    protected WorldPoint getWorldPoint(GraphicsObject graphicsObject) {
        return WorldPoint.fromLocal(client, graphicsObject.getLocation());
    }
}
