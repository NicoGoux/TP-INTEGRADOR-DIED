--Comentar luego de usar por primera vez
CREATE EXTENSION unaccent;
--
CREATE TABLE colour (
	colourHex varchar(7) CONSTRAINT pk_colour PRIMARY KEY
);
	   
CREATE TABLE station (
	id integer CONSTRAINT pk_station PRIMARY KEY,
	name varchar(20),
	opening varchar(5),
	closing varchar(5),
	operative boolean
);

CREATE TABLE maintenance (
	id integer CONSTRAINT fk_maintenance_station REFERENCES station(id) ON DELETE CASCADE,
	start_date date,
	end_date date,
	comment varchar(200),
	CONSTRAINT pk_maintenance PRIMARY KEY (id,start_date)
);



CREATE TABLE transport (
	id integer CONSTRAINT pk_transport PRIMARY KEY,
	name varchar(20),
	colour varchar(7) CONSTRAINT fK_colour_route REFERENCES colour(colourHex) ON DELETE CASCADE,
	status boolean
);

CREATE TABLE route (
	id_origin integer CONSTRAINT fk_station_origin REFERENCES station(id) ON DELETE CASCADE,
	id_destination integer CONSTRAINT fk_station_destination REFERENCES station(id) ON DELETE CASCADE,
	colour varchar(7) CONSTRAINT fK_colour_route REFERENCES colour(colourHex) ON DELETE CASCADE,
	distance integer,
	duration integer,
	passengers integer,
	status boolean,
	price double precision,
	CONSTRAINT pk_route PRIMARY KEY (id_origin, id_destination, colour)
);

CREATE TABLE ticket (
	id integer CONSTRAINT pk_ticket PRIMARY KEY,
	email varchar(40),
	client_name varchar(20),
	buy_date date,
	id_origin integer CONSTRAINT fk_station_origin REFERENCES station(id) ON DELETE CASCADE,
	id_end integer CONSTRAINT fk_station_end REFERENCES station(id) ON DELETE CASCADE,
	route varchar(200),
	price double precision
);
