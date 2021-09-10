package Graph;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.Collectors;

public class Graph {
	public static Graph graphInstance;
	private List<Route> routesList;
	private List<Station> stationsList;
	private List<Transport> transportsList;

	public static Graph getInstance(){
		if (graphInstance==null) {
			graphInstance=new Graph();
		}
		return graphInstance;
	}

	public Graph(){
		this.routesList = new ArrayList<Route>();
		this.stationsList = new ArrayList<Station>();
		this.transportsList = new ArrayList<Transport>();
		graphInstance = this;
	}

	// añadir estación
	public Station addStation(String name, String opening, String closing){ 
		Station newStation = new Station(name, opening, closing);
		this.addStation(newStation);
		return newStation;
	}

	public void addStation(Station station){
		this.stationsList.add(station);
	}

	public void removeStation(int idStation){
		this.removeStation(this.getStation(idStation));
	}

	public void removeStation(Station station) {
		this.stationsList.remove(station);
	}

	// Añadir transporte
	public Transport addTransport(String name, String colourHex) { 
		Transport newTransport = new Transport(name, colourHex);
		this.addTransport(newTransport);
		return newTransport;
	}

	public void addTransport(Transport transport){
		this.transportsList.add(transport);
	}

	public void removeTransport(int idTransport) {
		this.removeTransport(this.getTransport(idTransport));
	}

	public void removeTransport(Transport aTransport) {
		this.transportsList.remove(aTransport);
	}

	// Conecta 2 estaciones (Añadir ruta)
	public void connect(Route newRoute){
		this.routesList.add(newRoute);
	}

	//Consulta el estado de una linea de tranporte
	public boolean getColourTransportStatus(String colourHex) {
		for (Transport transport : this.transportsList) {
			if (transport.getColourString().equals(colourHex) && transport.getStatus()){
					return true;
			}
		}
		return false;
	}

	// Eliminacion de rutas
	public void deleteRoute(int origin, int end, String colourHex) {
		this.deleteRoute(this.getRoute(origin,end,colourHex));
    }

	public void deleteRoute(Route aRoute) {
		this.routesList.remove(aRoute);
    }

	public void deleteColourRoutes(String colourHex) {
		this.routesList.removeAll(getColourRoutes(colourHex));
	}

	//Cambia el estado de la ruta segun el valor de "enable"
    public void changeColourRoutesStatus(String colourHex, boolean enable) {
		for (Route aRoute : this.getColourRoutes(colourHex)) {
			aRoute.setStatus(enable);
		}
    }


	// Obtener lista de estaciones
	public List<Station> getAllStations() {
		return this.stationsList;
	}

	// Obtener estacion
	public Station getStation(int id) {
		for (Station s : this.stationsList) {
			if (s.getIdStation() == id) {
				return s;
			}
		}
		return null;
	}

	// Obtener transporte
	public Transport getTransport(int id) {
		for (Transport t : this.transportsList) {
			if (t.getId() == id) {
				return t;
			}
		}
		return null;
	}

	// Obtener ruta
	public Route getRoute(int origin, int end, String colourHex) {
		for (Route r : this.routesList) {
			if (r.getOrigin() == origin && r.getEnd() == end && r.getColourString().equals(colourHex)) {
				return r;
			}
		}
		return null;
	}

	// Obtener lista de rutas de un determinado color
	public List<Route> getColourRoutes (String colourHex) {
		List<Route> colourRoute = new ArrayList<Route>();
		for (Route r : this.routesList) {
			if (r.getColourString().equals(colourHex)){
				colourRoute.add(r);
			}
		}
		return colourRoute;
	} 
	
	// Obtener el origen de un trayecto
	public int getRouteOrigin(String colourHex) {
		for (Route r : this.getColourRoutes(colourHex)) {
			if (this.entryColourGrade(r.getOrigin(),colourHex)==0) {
				return r.getOrigin();
			}
		}
		return -1;
    }

	//Obtener el final de un trayecto
	public int getRouteEnd(String colourHex) {
		for (Route r : this.getColourRoutes(colourHex)) {
			if (this.exitColourGrade(r.getEnd(),colourHex)==0) {
				return r.getEnd();
			}
		}
		return -1;
    }

	// Obtener grado de entrada de una estacion para el color "colourHex"
	public Integer entryColourGrade(int idStation, String colourHex){
        Integer entryGrade = 0;
        for(Route r : this.getColourRoutes(colourHex)){
            if(r.getEnd()==idStation) {
				++entryGrade;
			} 
        }
        return entryGrade;
    }
	
	// Obtener grado de entrada de una estacion
	public Integer entryGrade(int idStation){
        Integer entryGrade = 0;
        for(Route r : this.routesList){
            if(r.getEnd()==idStation) {
				++entryGrade;
			} 
        }
        return entryGrade;
    }

	// Obtener grado de salida de una estacion para el color "colourHex"
	public Integer exitColourGrade(int idStation, String colourHex){
        Integer exitGrade = 0;
        for(Route r : this.getColourRoutes(colourHex)){
            if(r.getOrigin()==idStation) {
				++exitGrade;
			} 
        }
        return exitGrade;
    }

	// Obtener grado de salida de una estacion
	public Integer exitGrade(int idStation){
        Integer exitGrade = 0;
        for(Route r : this.routesList){
            if(r.getOrigin()==idStation) {
				++exitGrade;
			} 
        }
        return exitGrade;
    }

	// Obtener estaciones conectadas a una estacion
	public List<Integer> getNeighbourhood(Integer idStation){ 
		List<Integer> neighbourhoods = new ArrayList<Integer>();
		for(Route aRoute : this.routesList) {
			if( aRoute.getOrigin()==idStation) {
				if (!neighbourhoods.contains(aRoute.getEnd())) {
					neighbourhoods.add(aRoute.getEnd());
				}
			}
		}
		return neighbourhoods;
	}

	// Obtener estaciones que tienen un camino hasta la estacion idStation
	public List<Integer> getEndNeighbourhood(Integer idStation){ 
		List<Integer> neighbourhoods = new ArrayList<Integer>();
		for(Route aRoute : this.routesList) {
			if( aRoute.getEnd()==idStation) {
				if (!neighbourhoods.contains(aRoute.getOrigin())) {
					neighbourhoods.add(aRoute.getOrigin());
				}
			}
		}
		return neighbourhoods;
	}
	
	// Se obtienen todos las rutas directas desde la estacion n1 a la estacion n2
	public List<Route> getEdges(Integer n1, Integer n2){    	
    	List<Route> routes = new ArrayList<Route>();
        for(Route aRoute : this.routesList) {
        		
        	if(aRoute.getOrigin()==n1 && aRoute.getEnd() == n2) 
        		routes.add(aRoute);
        }
        return routes;
    }

    // Devuelve la lista de lista de rutas por las que se puede llegar desde la estacion n1 a la estacion n2
	public List<List<Route>> possiblePaths(Integer n1,Integer n2){
        List<List<Route>> pathList = new ArrayList<List<Route>>();
        List<Route> visited = new ArrayList<Route>();
		if(this.getStation(n1).getStatus() && this.getStation(n1).getTimeStatus()) { 
			possiblePathsAux(n1,n2,visited,pathList);
        	if(pathList.isEmpty()) {
				System.out.println("No hay caminos desde la estacion " + n1 + " hasta estacion " + n2);
			}
			return getNotLoopRoutes(pathList);	
		}
		else {
			System.out.println("No hay caminos desde la estacion " + n1 + " hasta la estacion " + n2);
			return pathList;
		}
    }

	// Metodo auxiliar a possiblePaths
	private void possiblePathsAux(Integer n1,Integer n2, List<Route> visited, List<List<Route>> pathList) { 
        List<Integer> ady = this.getNeighbourhood(n1);
        List<Route> visitedCopy = null;
        for(Integer idStation : ady) {
            visitedCopy = visited.stream().collect(Collectors.toList());
            if(this.getStation(idStation).getStatus() && this.getStation(n1).getTimeStatus()) {

				for (Route aRoute : this.getEdges(n1, idStation)) {
					if (aRoute.getStatus() && this.getColourTransportStatus(aRoute.getColourString())) {
						visitedCopy.add(aRoute);
                    	if(idStation.equals(n2)) {
                        pathList.add(new ArrayList<Route>(visitedCopy));
                    	}
                    	else if(!visited.contains(aRoute)) {
                        	this.possiblePathsAux(aRoute.getEnd(), n2, visitedCopy, pathList);
                		}
						visitedCopy.remove(aRoute);
					}
				}
			}
		}
	}

	// Obtiene todas las estaciones por las que se pasa en un camino completo
	private List<Integer> getStationsFromPath(List<Route> aPath) {
		List<Integer> stationsFromPath = new ArrayList<Integer>();
		for (Route aRoute : aPath) {
			if (stationsFromPath.isEmpty()) {
				stationsFromPath.add(aRoute.getOrigin());
				stationsFromPath.add(aRoute.getEnd());
			}
			else {
				stationsFromPath.add(aRoute.getEnd());
			}
			
		}
		return stationsFromPath;
	}

	public List<Integer> getStationsFromPathList(List<List<Route>> pathList) {
		List<Integer> stationsList = new ArrayList<Integer>();
		for (List<Route> aPath : pathList) {
			for (Integer aStationID : this.getStationsFromPath(aPath)) {
				if (!stationsList.contains(aStationID)) {
					stationsList.add(aStationID);
				}
			}
		}
		return stationsList;
	}

	// Elimina las rutas que contienen bucles, generadas por el metodo possiblePathsAux 
	public List<List<Route>> getNotLoopRoutes (List<List<Route>> pathList){
		List<List<Route>> solution = new ArrayList<List<Route>>();
		for(List<Route> aRouteList : pathList) {
			List<Integer> buffer = this.getStationsFromPath(aRouteList);
			
			if(!this.hasDuplicates(buffer)){
				solution.add(aRouteList);
			}
		}
		return solution;
	}

	// Devuelve True si la lista contiene elementos duplicados, caso contrario devuelve False
	public Boolean hasDuplicates(List<Integer> aStationsList) {
		Set<Integer> set = new HashSet<Integer>();
		for (Integer idStation : aStationsList) {
			if (!set.add(idStation)) {
				return true;
			}
		}
		return false;
	}

	// Devuelve la ruta mas corta
	public List<Route> getShortestRoute(List<List<Route>> pathList) {
		int shortestDistance = Integer.MAX_VALUE;
		if (pathList.isEmpty()) {
			return new ArrayList<>();
		}
		int index = 0;
		for (List<Route> aRouteList : pathList) {
			int distance=0;
			for (Route aRoute : aRouteList) {
				distance+=aRoute.getDistance();
			}
			if (shortestDistance>distance) {
				index = pathList.indexOf(aRouteList);
				shortestDistance=distance;
			}
		}
		return pathList.get(index);
	}

	// Devuelve la ruta mas rapida
	public List<Route> getFastestRoute(List<List<Route>> pathList) {
		int shortestDuration = Integer.MAX_VALUE;
		if (pathList.isEmpty()) {
			return new ArrayList<>();
		}
		int index = 0;
		for (List<Route> aRouteList : pathList) {
			int duration=0;
			for (Route aRoute : aRouteList) {
				duration+=aRoute.getDuration();
			}
			if (shortestDuration>duration) {
				index = pathList.indexOf(aRouteList);
				shortestDuration=duration;
			}
		}
		return pathList.get(index);
	}

	// Devuelve la ruta mas barata
	public List<Route> getCheaperRoute(List<List<Route>> pathList) {
		double cheapestPrice = Double.MAX_VALUE;
		if (pathList.isEmpty()) {
			return new ArrayList<>();
		}
		int index = 0;
		for (List<Route> aRouteList : pathList) {
			double price=0;
			for (Route aRoute : aRouteList) {
				price+=aRoute.getPrice();
			}
			if (cheapestPrice>price) {
				index = pathList.indexOf(aRouteList);
				cheapestPrice=price;
			}
		}
		return pathList.get(index);
	}

	// Devuelve el precio de la ruta
	public Double getPathCost(List<Route> aPath) {
		double cost = 0;
		for (Route aRoute : aPath) {
			cost+=aRoute.getPrice();
		}
		return cost;
	}
    
	// Devuelve el flujo maximo entre dos estaciones
    public Integer getMaxFlow(Integer idStationOrigin, Integer idStationEnd, List<List<Route>> pathList) {
		int maxFlow=0;
		HashMap<String,Integer> flows = new LinkedHashMap<String,Integer>();
    	for(List<Route> aRouteList : pathList) {
			for(Route aRoute : aRouteList){

				if(aRoute.getMaxPassengers() > 0) {

					String key = aRoute.getHashString();
					if(!flows.containsKey(key)){
						flows.put(key, aRoute.getMaxPassengers());
					} 
				}
			}
		}

		for(List<Route> aRouteList : pathList) {
			Integer min = Integer.MAX_VALUE;
			for(Route aRoute : aRouteList) {
				if(min > flows.get(aRoute.getHashString())) {
					min = flows.get(aRoute.getHashString());
					if (min==0) {
						break;
					}
				}
			}
			maxFlow+=min;
			for (Route aRoute : aRouteList) {
				flows.replace(aRoute.getHashString(), flows.get(aRoute.getHashString())-min);
			}
		}
		return maxFlow;
	}

	// Devuelve un hashmap con el valor de prioridad de cada estacion
	public HashMap<Integer,Double> calculatePageRank() {
		HashMap<Integer,Double> pageRank = new HashMap<Integer,Double>(this.stationsList.size());
		HashMap<Integer,Double> previousPageRank = new HashMap<Integer,Double>(this.stationsList.size());
		for (Station aStation : this.stationsList) {
			pageRank.put(aStation.getIdStation(),1.0);
			previousPageRank.put(aStation.getIdStation(),1.0);
		}

		while (finishPageRank(previousPageRank, pageRank)) {
			for (Station aStation : this.stationsList) {
				previousPageRank.put(aStation.getIdStation(), pageRank.get(aStation.getIdStation()));
				this.pageRank(aStation.getIdStation(),pageRank);
			}
		}
		return pageRank;
	}

	private void pageRank (Integer idStation, HashMap<Integer,Double> pageRank) {
		Double factor = 0.5;
		Double pageRankValue = (1-factor);
		Double sumatory = 0.0;
		for (Integer idPreviousStation : this.getEndNeighbourhood(idStation)) {
			sumatory+= (pageRank.get(idPreviousStation))/this.exitGrade(idPreviousStation);
		}
		pageRankValue+=factor*sumatory;
		pageRank.replace(idStation, pageRankValue);
	}

	private boolean finishPageRank(HashMap<Integer,Double> previousPageRank, HashMap<Integer,Double> pageRank) {
		for (Station aStation : this.stationsList) {
			if (pageRank.get(aStation.getIdStation()) - previousPageRank.get(aStation.getIdStation())>0.00000001) {
				return false;
			}
		}
		return true;
	}

	// Devuelve el monticulo de orden de mantenimientos
	public PriorityQueue<Map.Entry<Integer,LocalDate>> nextMaintenance(HashMap<Integer,LocalDate> stationMaintenanceDateMap) {
		//Creamos una cola de prioridad con el par (idStation,lastMaintenance) 
		PriorityQueue<Map.Entry<Integer,LocalDate>> priorityPair = new PriorityQueue<Map.Entry<Integer,LocalDate>>((pair1, pair2)->(pair1.getValue().compareTo(pair2.getValue())));
		
		//Añadimos el hashMap obtenido de la base de datos a la cola de prioridad
		priorityPair.addAll(stationMaintenanceDateMap.entrySet());
		
		//Retornamos la cola de prioridad ordenada segun la fecha del ultimo mantenimiento
		return priorityPair;
	}
}
