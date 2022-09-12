package ca.ubc.cs.cpsc210.translink.ui;

import android.content.Context;
import ca.ubc.cs.cpsc210.translink.BusesAreUs;
import ca.ubc.cs.cpsc210.translink.model.Route;
import ca.ubc.cs.cpsc210.translink.model.RoutePattern;
import ca.ubc.cs.cpsc210.translink.model.Stop;
import ca.ubc.cs.cpsc210.translink.model.StopManager;
import ca.ubc.cs.cpsc210.translink.util.Geometry;
import ca.ubc.cs.cpsc210.translink.util.LatLon;
import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

// A bus route drawer
public class BusRouteDrawer extends MapViewOverlay {
    /**
     * overlay used to display bus route legend text on a layer above the map
     */
    private BusRouteLegendOverlay busRouteLegendOverlay;
    /**
     * overlays used to plot bus routes
     */
    private List<Polyline> busRouteOverlays;

    /**
     * Constructor
     *
     * @param context the application context
     * @param mapView the map view
     */
    public BusRouteDrawer(Context context, MapView mapView) {
        super(context, mapView);
        busRouteLegendOverlay = createBusRouteLegendOverlay();
        busRouteOverlays = new ArrayList<>();
    }

    /**
     * Plot each visible segment of each route pattern of each route going through the selected stop.
     */
    public void plotRoutes(int zoomLevel) {
        updateVisibleArea();
        busRouteLegendOverlay.clear();
        Stop selected = StopManager.getInstance().getSelected();
        if (selected != null && selected.getRoutes() != null) {
            for (Route route : selected.getRoutes()) {
                String num = route.getNumber();
                int i = busRouteLegendOverlay.add(num);
                if (route.getPatterns() != null) {
                    for (RoutePattern pattern : route.getPatterns()) {
                        plotPoints(pattern, zoomLevel, i);
                    }
                }
            }
        }
    }

    public void plotPoints(RoutePattern routePattern, int zoomLevel, int color) {
        for (int i = 0; i < routePattern.getPath().size() - 1; i++) {
            if (routePattern.getPath().get(i) != null && routePattern.getPath().get(i + 1) != null) {
                LatLon latLon1 = new LatLon(routePattern.getPath().get(i).getLatitude(),
                        routePattern.getPath().get(i).getLongitude());
                LatLon latLon2 = new LatLon(routePattern.getPath().get(i + 1).getLatitude(),
                        routePattern.getPath().get(i + 1).getLongitude());
                if (southEast != null && northWest != null) {
                    if (Geometry.rectangleIntersectsLine(northWest, southEast, latLon1, latLon2)) {
                        createPolyLine(zoomLevel, latLon1, latLon2, color);
                    }
                }
            }
        }
    }

    public void createPolyLine(int zoomLevel, LatLon latLon1, LatLon latLon2, int color) {
        Polyline polyline = new Polyline(context);
        polyline.setWidth(getLineWidth(zoomLevel));
        polyline.setVisible(true);
        polyline.setPoints(addGeoPoints(latLon1, latLon2));
        drawRoute(polyline, color);
    }

    public void drawRoute(Polyline polyline, int color) {
        polyline.setColor(color);
        busRouteOverlays.add(polyline);
    }

    public List<GeoPoint> addGeoPoints(LatLon latLon1, LatLon latLon2) {
        List<GeoPoint> geoPoints = new ArrayList<>();
        GeoPoint geoPoint1 = Geometry.gpFromLatLon(latLon1);
        GeoPoint geoPoint2 = Geometry.gpFromLatLon(latLon2);
        geoPoints.add(geoPoint1);
        geoPoints.add(geoPoint2);
        return geoPoints;
    }

    public List<Polyline> getBusRouteOverlays() {
        return Collections.unmodifiableList(busRouteOverlays);
    }

    public BusRouteLegendOverlay getBusRouteLegendOverlay() {
        return busRouteLegendOverlay;
    }

    /**
     * Create text overlay to display bus route colours
     */
    private BusRouteLegendOverlay createBusRouteLegendOverlay() {
        ResourceProxy rp = new DefaultResourceProxyImpl(context);
        return new BusRouteLegendOverlay(rp, BusesAreUs.dpiFactor());
    }

    /**
     * Get width of line used to plot bus route based on zoom level
     *
     * @param zoomLevel the zoom level of the map
     * @return width of line used to plot bus route
     */
    private float getLineWidth(int zoomLevel) {
        if (zoomLevel > 14) {
            return 7.0f * BusesAreUs.dpiFactor();
        } else if (zoomLevel > 10) {
            return 5.0f * BusesAreUs.dpiFactor();
        } else {
            return 2.0f * BusesAreUs.dpiFactor();
        }
    }
}
