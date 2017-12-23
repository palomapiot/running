package es.udc.fi.dc.fd.race;

public enum RaceType {
	/**
	 * 
	 * En BD se guarda:
	 * 
	 * 0 iron man 1 triathlon 2 marathon 3 halfmarathon 4 popular 5 otro 6 milla 7
	 * velocidad 8 media larga distancia 9 relevos 10 obstáculos 11 natación
	 * individual 12 natación relevos 13 salto ecuestre 14 turf 15 bici asfalto un
	 * dia 16 bici asfalto etapas 17 bici asfalto contrarreloj 18 bici pista
	 * velocidad 19 bici pista persecución 20 bici montaña campo a traves 21 bici
	 * montaña descenso 22 bici montaña doble slalom
	 * 
	 */
	IRON_MAN(226060, "Iron Man"), TRIATHLON(51500, "Triathlon"), MARATHON(42195, "Marathon"), HALF_MARATHON(21097,
			"Half Marathon"), POPULAR_RACE(-1, "Popular Race"), OTHER(-2, "Other"), MILE(1500, "Mile"), SPEED(-103,
					"Speed"), MEDIUM_LONG_DISTANCE(-104, "Medium Long Distance"), RELAYS(-105, "Relays"), OBSTACLES(
							-106, "Obstacles"), INDIVIDUAL_SWIMMING(-107, "Individual Swimming"), RELAY_SWIMMING(-108,
									"Relay Swimming"), EQUESTRIAN_JUMPING(-109, "Equestrian Jumping"), TURF(-101,
											"Turf"), BICYCLE_ASPHALT_ONE_DAY(-11,
													"Bicycle Asphalt One Day"), BICYCLE_ASPHALT_STAGES(-12,
															"Bicycle Asphalt Stages"), BICYCLE_ASPHALT_TIME_TRIALS(-13,
																	"Bicycle Asphalt Time Trials"), CICLOCROSS(-14,
																			"Ciclocross"), BICYCLE_SPEED_TRACK(-150,
																					"Bicycle Speed TRack"), BICYCLE_CHASE_TRACK(
																							-160,
																							"Bicycle Chase Track"), MOUNTAIN_BIKE_FIELD_THROUGH(
																									-3,
																									"Mountain Bike Field Through"), DOWNHILL_MOUNTAIN_BIKE(
																											-4,
																											"Downhill Mountain Bike"), MOUNTAIN_BIKE_DOUBLE_SLALOM(
																													-5,
																													"Mountain Bike Double Slalom");

	private int distance;

	private String text;

	private RaceType(int distance, String text) {
		this.distance = distance;
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public int getDistance() {
		return distance;
	}

	public static RaceType getRaceType(int distance) {
		for (RaceType t : RaceType.values()) {
			if (t.distance == distance)
				return t;
		}
		throw new IllegalArgumentException("Race Type not found.");
	}
}