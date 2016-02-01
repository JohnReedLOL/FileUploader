package utilities;

/**
 * Represents an event in the background which would need to be polled for and
 * responded to.
 *
 * @author johnmichaelreed2
 */
public interface BackgroundEvent {

    /**
     * Checks to see if a background event has occurred. If this method returns
     * true, {#respondToEventOccurance() } is called to handle the event.
     *
     * @return whether or not the event has occurred.
     */
    boolean checkForEventOccurance();

    /**
     * This method is triggered when {#checkForEventOccurance() } returns
     * true. This method responds to the background event detected by {#checkForEventOccurance()
     * }
     */
    void respondToEventOccurance();
}
