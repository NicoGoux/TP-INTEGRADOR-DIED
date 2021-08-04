package Graph;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Station {
	private static int IdSCounter = 1;
	private int idStation;
	private String name;
    private LocalTime opening;
    private LocalTime closing;
    private boolean status;

	public Station(int idStation, String name, String opening, String closing, boolean status) {
		DateTimeFormatter parseFormat = DateTimeFormatter.ofPattern("H:mm");
		this.idStation = idStation;
		this.name = name;
		this.opening = LocalTime.parse(opening,parseFormat);
		this.closing = LocalTime.parse(closing,parseFormat);
		this.status = status;
	}

	public Station(String name, String opening, String closing) {
		DateTimeFormatter parseFormat = DateTimeFormatter.ofPattern("H:mm");
		this.idStation = IdSCounter++;
		this.name = name;
		this.opening = LocalTime.parse(opening,parseFormat);
		this.closing = LocalTime.parse(closing,parseFormat);
		this.status = true;
	}

	public int getIdStation(){
		return idStation;
	}

	public void setIdStation(int idStation){
		this.idStation=idStation;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name=name;
	}

	public String getOpening() {
		return opening.toString();
	}

	public LocalTime getOpeningTime() {
		return opening;
	}

	public void setOpening(String opening) {
		DateTimeFormatter parseFormat = DateTimeFormatter.ofPattern("H:mm");
		this.opening=LocalTime.parse(opening,parseFormat);;
	}

	public String getClosing() {
		return closing.toString();
	}
	public LocalTime getClosingTime() {
		return closing;
	}

	public void setClosing(String closing) {
		DateTimeFormatter parseFormat = DateTimeFormatter.ofPattern("H:mm");
		this.closing = LocalTime.parse(closing,parseFormat);
	}

	public Status getStringStatus() {
		if (this.status) {
			return Status.OPERATIVE;
		}
		else {
			return Status.IN_MAINTENANCE;
		}
	}

	public boolean getStatus() {
		return this.status;
	}

	public void setStatus(boolean status) {
		this.status=status;
	}

	public boolean getTimeStatus(){
		if (this.closing.compareTo(this.opening)<0) {
			DateTimeFormatter parseFormat = DateTimeFormatter.ofPattern("H:mm:ss");
			if ((this.opening.compareTo(LocalTime.now()) < 0 && LocalTime.now().compareTo(LocalTime.parse("23:59:59",parseFormat)) < 0) ||
				(LocalTime.now().compareTo(this.closing) < 0 && LocalTime.parse("00:00:00",parseFormat).compareTo(LocalTime.now()) < 0)) {
					return true;
			}
			else {
				return false;
			}
		}
		else {
			if (this.opening.compareTo(LocalTime.now()) < 0 && LocalTime.now().compareTo(this.closing) < 0) {
				return true;
			}
			else {
				return false;
			}
		}
	}
	
	public static void setIdSCounter(int newSCounter) {
		IdSCounter = newSCounter+1;
	}
}