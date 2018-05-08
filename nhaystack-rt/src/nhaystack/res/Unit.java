//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   01 Feb 2013  Mike Jarmy Creation
//
package nhaystack.res;

import javax.baja.nre.util.TextUtil;

/**
  * Unit models a single entry found in 'units.txt'.
  */
public class Unit
{
    public Unit(String quantity, String name, String symbol)
    {
        this.quantity = quantity;
        this.name = name;
        this.symbol = symbol;

        this.hashCode =
            31*31*quantity.hashCode() + 
            31*name.hashCode() +
            symbol.hashCode();
    }

    public String toString()
    {
        return "[Unit " +
            "quantity:" + quantity + ", " +
            "name:" + name + ", " +
            "symbol:" + symbol + ']';
    }

    public String toDisplayString()
    {
        return TextUtil.replace(name, "_", " ") + " (" + symbol + ')';
    }

    public boolean equals(Object obj) 
    {
        if (this == obj) return true;

        if (!(obj instanceof Unit)) return false;

        Unit that = (Unit) obj;
        return 
            this.quantity .equals(that.quantity) &&
            this.name     .equals(that.name)     &&
            this.symbol   .equals(that.symbol);
    }

    public int hashCode() { return hashCode; }

    public final String quantity;
    public final String name;
    public final String symbol;

    private final int hashCode;
}
