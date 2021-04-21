package dev.cheos.armorpointspp;

import dev.cheos.armorpointspp.config.ApppConfig;

public enum Suffix {
	NONE ( 0, "" , ""      , ""       ),
	KILO ( 3, "k", " Tsd." , " thous."),
	MEGA ( 6, "M", " Mio." , " mio."  ),
	GIGA ( 9, "G", " Mrd." , " bio."  ),
	TERA (12, "T", " Bio." , " trio." ),
	PETA (15, "P", " Brd." , " quad." ),
	EXA  (18, "E", " Trio.", " quint."),
	ZETTA(21, "Z", " Trd." , " sext." ),
	YOTTA(24, "Y", " Quad.", " sept." );
	
	public final int pow;
	public final String si;
	public final String ger;
	public final String eng;
	
	Suffix(int pow, String si, String ger, String eng) {
		this.pow = pow;
		this.si  = si ;
		this.ger = ger;
		this.eng = eng;
	}
	
	public String getPrefix() {
		switch(ApppConfig.getSuffix()) {
			case SI:
				return si ;
			case GER:
				return ger;
			case ENG:
				return eng;
			default:
				return si ;
		}
	}
	
	public static Suffix byPow(int pow) {
		for(Suffix pre : values())
			if(pre.pow == pow)
				return pre;
		return NONE;
	}

	public static enum Type {
		SI ,
		SCI,
		GER,
		ENG;
		
		public static Type fromName(String name) {
			return valueOf(name.toUpperCase()) == null ? SI : valueOf(name.toUpperCase());
		}
	}
}
