package Interfaces.Accessories;

import javax.swing.JComboBox;
import Graph.Graph;
import Graph.Station;

public class StationsComboBox extends JComboBox<Integer>{

    public StationsComboBox(String initialString) {
        super();
        this.addItem(0);
        for (Station aStation : Graph.getInstance().getAllStations()) {
            this.addItem(aStation.getIdStation());
        }
        this.setRenderer(new StationSelectorRender(this, initialString));
    }
}
