package ca.ubc.cs.cpsc210.translink.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import ca.ubc.cs.cpsc210.translink.R;
import ca.ubc.cs.cpsc210.translink.model.Bus;
import ca.ubc.cs.cpsc210.translink.model.Stop;
import ca.ubc.cs.cpsc210.translink.model.StopManager;
import ca.ubc.cs.cpsc210.translink.util.Geometry;
import ca.ubc.cs.cpsc210.translink.util.LatLon;
import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.List;

// A plotter for bus locations
public class BusLocationPlotter extends MapViewOverlay {
    /**
     * overlay used to display bus locations
     */
    private ItemizedIconOverlay<OverlayItem> busLocationsOverlay;

    /**
     * Constructor
     *
     * @param context the application context
     * @param mapView the map view
     */
    public BusLocationPlotter(Context context, MapView mapView) {
        super(context, mapView);
        busLocationsOverlay = createBusLocnOverlay();
    }

    public ItemizedIconOverlay<OverlayItem> getBusLocationsOverlay() {
        return busLocationsOverlay;
    }

    /**
     * Plot buses serving selected stop
     */
    public void plotBuses() {
        busLocationsOverlay.removeAllItems();
        if (StopManager.getInstance().getSelected() != null) {
            Stop stop = StopManager.getInstance().getSelected();
            if (stop.getBuses() != null) {
                plotBus(stop.getBuses());
            }
        }
    }

    public void plotBus(List<Bus> buses) {
        Drawable busIcon =  context.getResources().getDrawable(R.drawable.bus);
        for (Bus bus: buses) {
            LatLon latLon = bus.getLatLon();
            GeoPoint gp = Geometry.gpFromLatLon(latLon);
            OverlayItem marker = new OverlayItem(bus.getRoute().toString(), "", gp);
            marker.setMarker(busIcon);
            marker.setMarkerHotspot(OverlayItem.HotspotPlace.CENTER);
            busLocationsOverlay.addItem(marker);
        }
    }

    /**
     * Create the overlay for bus markers.
     */
    private ItemizedIconOverlay<OverlayItem> createBusLocnOverlay() {
        ResourceProxy rp = new DefaultResourceProxyImpl(context);

        return new ItemizedIconOverlay<OverlayItem>(
                new ArrayList<OverlayItem>(),
                context.getResources().getDrawable(R.drawable.bus),
                null, rp);
    }
}
