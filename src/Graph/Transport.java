package Graph;

import java.awt.Color;

public class Transport {
    private static int IdTCounter = 1;
    private int id;
    private String name;
    public Color colour;
    private Boolean status;

    public Transport(String name, String colourHex) {
        this.id=IdTCounter++;
        this.name = name;
        this.colour = Color.decode(colourHex);
        this.status = true;
    }
    
    public Transport(int id, String name, String colourHex,  Boolean status) {
        this.id= id;
        this.name = name;
        this.colour = Color.decode(colourHex);
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public Color getColour() {
        return this.colour;
    }
    
    public String getColourString() {
        return String.format("#%02x%02x%02x", this.colour.getRed(), this.colour.getGreen(), this.colour.getBlue());
    }

    public void setColour(String colour) {
        this.colour = Color.decode(colour);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name=name;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status=status;
    }

    public static void setIdTCounter(int newTCounter) {
		IdTCounter = newTCounter+1;
	}


}
