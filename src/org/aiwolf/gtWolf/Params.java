package org.aiwolf.gtWolf;

public class Params {
	double value;
	double defo;
	double mn;
	double mx;
	String name;
	boolean valid = true;
	double diff;
	Params(double initial, double _mn, double _mx, String _name, double _diff){
		defo = initial;
		value = initial;
		mn = _mn;
		mx = _mx;
		name = _name;
		diff = _diff;
	}
	
	
}
