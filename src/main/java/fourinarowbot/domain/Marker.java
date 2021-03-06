package fourinarowbot.domain;

public class Marker {

    private MarkerColor color;
    private Coordinates coordinates;

    private Marker() {
        // For JSON-serialization
    }

    public Marker(final MarkerColor color, final Coordinates coordinates) {
        this.color = color;
        this.coordinates = coordinates;
    }

    public MarkerColor getColor() {
        return color;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }
}
