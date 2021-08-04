package Graph;

import java.awt.Color;

public class Route {

	private int stationOrigin;
	private int endStation;
	private Color colour;
	private int distance;
	private int duration;
	private int maxPassengers;
	private boolean status; // false close - true open
	private double price;
	
	public Route(int origin, int end, String transportColour, Integer distance, int duration, int maxPassengers, boolean status, double price) {
		this.stationOrigin = origin;
        this.endStation = end;
        this.colour=Color.decode(transportColour);
        this.distance = distance;
        this.duration = duration;
        this.maxPassengers = maxPassengers;
        this.status = status;
		this.price=price;
	}

	public double getPrice() {
		return price;
	}



	public void setPrice(double price) {
		this.price = price;
	}



	public boolean getStatus() {
		return status;
	}



	public void setStatus(boolean status) {
		this.status = status;
	}



	public int getMaxPassengers() {
		return maxPassengers;
	}



	public void setMaxPassengers(int maxPassengers) {
		this.maxPassengers = maxPassengers;
	}



	public int getDuration() {
		return duration;
	}



	public void setDuration(int duration) {
		this.duration = duration;
	}



	public Integer getDistance() {
		return distance;
	}



	public void setDistance(Integer distance) {
		this.distance = distance;
	}



	public Color getColour() {
		return colour;
	}



	public void setColour(Color transportColour) {
		this.colour = transportColour;
	}

	public String getColourString() {
		return String.format("#%02x%02x%02x", this.colour.getRed(), this.colour.getGreen(), this.colour.getBlue());
	}

	public int getEnd() {
		return endStation;
	}



	public void setEnd(int end) {
		this.endStation = end;
	}

	public int getOrigin() {
		return stationOrigin;
	}

	public void setOrigin(int origin) {
		this.stationOrigin = origin;
	}

	/* 	"#000000" ---
		"#0000ff" ---
		"#00ff00" ---
		"#00ffff" ---
		"#8B008B" ---
		"#999999" ---
		"#ff0000" ---
		"#ff6600" ---
		"#FFC0CB" ---
		"#ffff00" ---
		"#ffffff" ---  */

	public String toString() {
		String colour;
		String colourHexString = this.getColourString();
		if (colourHexString.equals("#000000")) {
			colour = "Negro";
		}
		else if (colourHexString.equals("#00ff00")) {
			colour = "Verde";
		}
		else if (colourHexString.equals("#ff0000")) {
			colour = "Rojo";
		}
		else if (colourHexString.equals("#ffff00")) {
			colour = "Amarillo";
		}
		else if (colourHexString.equals("#0000ff")) {
			colour = "Azul";
		}
		else if (colourHexString.equals("#ffc0cb")) {
			colour = "Rosado";
		}
		else if (colourHexString.equals("#ff6600")) {
			colour = "Naranja";
		}
		else if (colourHexString.equals("#8b008b")) {
			colour = "Violeta";
		}
		else if (colourHexString.equals("#999999")) {
			colour = "Gris";
		}
		else if (colourHexString.equals("#00ffff")) {
			colour = "Celeste";
		}
		else {
			colour = "Blanco";
		}
		
		String route = getOrigin()+"->"+colour+"->"+getEnd();
		return route;
	}

	public String getHashString() {
			return this.getOrigin() + getColourString() + getEnd();
		}
	
}