package es.udc.fi.dc.fd.race;

public enum OrderType {

	PRICE(0), PLACE(1), NAME(2), DATE(3), DISTANCE(4);

	private int nOrder;

	private OrderType(int nOrder) {
		this.nOrder = nOrder;
	}

	public int getNOrder() {
		return nOrder;
	}

	public static OrderType getRaceType(int nOrder) {
		for (OrderType t : OrderType.values()) {
			if (t.nOrder == nOrder)
				return t;
		}
		throw new IllegalArgumentException("Order Type not found.");
	}
}