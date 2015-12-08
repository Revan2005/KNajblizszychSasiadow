import java.util.ArrayList;


public abstract class Atrybuty {
	
	static ArrayList<Atrybut> atrybuty = new ArrayList<Atrybut>();
	
	public static void add(Atrybut a){
		atrybuty.add(a);
	}
	
	public static Atrybut get(int i){
		return atrybuty.get(i);
	}
	
	public static int getLiczbaAtrybutowZKlasa(){
		return atrybuty.size();
	}
	
	public static Atrybut getAtrybutKlasowy(){
		return atrybuty.get(atrybuty.size()-1);
	}
	
	public static int getLiczbaKlas(){
		Atrybut aK = getAtrybutKlasowy();
		return aK.getLiczbaMozliwychWartosciLubPrzedzialow();
	}
	
	public static int getIndeksKlasy(String etykietaKlasy){
		Atrybut aK = getAtrybutKlasowy();
		for(int i=0; i<aK.dziedzina.size(); i++){
			if(aK.dziedzina.get(i).equals(etykietaKlasy))
				return i;
		}
		return -1;
	}
	
	public static void dyskretyzujStalaSzerokoscPrzedzialu(int liczbaPrzedzialow){
		//dla kazdego atrybutu oprocz ostatniego - ostatni to etykieta klasy
		Atrybut a;
		Obiekt o;
		double minValue, maxValue;
		for(int i=0; i<getLiczbaAtrybutowZKlasa()-1; i++){
			a = atrybuty.get(i);
			if(a.czyNumeryczny()){
				o = Main.dane.get(0);
				minValue = o.wartosciAtrybutowNumerycznych[i];
				maxValue = o.wartosciAtrybutowNumerycznych[i];
				for(int j=0; j<Main.dane.size(); j++){
					o = Main.dane.get(j);
					if(o.wartosciAtrybutowNumerycznych[i] <= minValue)
						minValue = o.wartosciAtrybutowNumerycznych[i];
					if(o.wartosciAtrybutowNumerycznych[i] >= maxValue)
						maxValue = o.wartosciAtrybutowNumerycznych[i];
				}
				a.dyskretyzujStalaSzerokoscPrzedzialu(minValue, maxValue, liczbaPrzedzialow);
			}
		}
	}
	
	public static void dyskretyzujOneR(int minObjInPart){
		//dla kazdego atrybutu oprocz ostatniego - ostatni to etykieta klasy
				Atrybut a;
				Obiekt o;
				String[] classes = new String[Main.dane.size()];
				double[] values = new double[Main.dane.size()];
				for(int i=0; i<getLiczbaAtrybutowZKlasa()-1; i++){
					a = atrybuty.get(i);
					if(a.czyNumeryczny()){
						for(int j=0; j<Main.dane.size(); j++){
							o = Main.dane.get(j);
							values[j] = o.wartosciAtrybutowNumerycznych[i];
							classes[j] = o.getEtykietaKlasy();
						}
						a.dyskretyzujOneR(values, classes, minObjInPart);
					}
				}
	}


}
