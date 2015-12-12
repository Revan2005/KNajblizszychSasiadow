import java.util.ArrayList;
import java.util.Arrays;

public class KlasyfikatorKNN {
	ArrayList<Obiekt> zbiorUczacy;
	int liczbaSasiadow;
	String metryka;
	String metodaGlosowania;
	//do malanobiusa, to powinna byc macierz diagonalna a nie wektor ale tak bedzie prosciej
	double[] wariancjeAtrybutow;
	
	public KlasyfikatorKNN(int liczbaSasiadow, String metryka, String metodaGlosowania, ArrayList<Obiekt> daneUczace){
		zbiorUczacy = daneUczace;
		this.liczbaSasiadow = liczbaSasiadow;
		this.metryka = metryka;
		this.metodaGlosowania = metodaGlosowania;
		wariancjeAtrybutow = getWariancjeAtrybutowWZbiorzeUczacym();
	}
	
	public String getClass(Obiekt obiekt_testowy){
		ArrayList<Obiekt> kopiaZbioruUczacego = new ArrayList<Obiekt>();
		for(int i = 0; i < zbiorUczacy.size(); i++){
			kopiaZbioruUczacego.add(new Obiekt(zbiorUczacy.get(i)));
		}
		ArrayList<Obiekt> najblizsziSasiedzi = new ArrayList<Obiekt>();
		//najblizsi sasiedzi beda ustawieni w kolejnosci od najblizszego do najdalszego
		Obiekt sasiad;
		while(najblizsziSasiedzi.size() < liczbaSasiadow){
			sasiad = getKolejnyNajblizszy(obiekt_testowy, kopiaZbioruUczacego);
			najblizsziSasiedzi.add( sasiad );
		}
		String klasa = wybierzKlasePrzezGlosowanie(obiekt_testowy, najblizsziSasiedzi);
		return klasa;
	}
	
	private Obiekt getKolejnyNajblizszy( Obiekt obiekt_testowy, ArrayList<Obiekt> zbior ){
		Obiekt najblizszySasiadWZbiorze = zbior.get(0);
		int index_najblizszego_obiektu = 0;
		double min_distance = getDistance(obiekt_testowy, zbior.get(0));
		double distance;
		for( int index_obiektu = 0; index_obiektu < zbior.size(); index_obiektu++ ){
			distance = getDistance(obiekt_testowy, zbior.get(index_obiektu));
			if( distance < min_distance ){
				najblizszySasiadWZbiorze = new Obiekt(zbior.get(index_obiektu));
				index_najblizszego_obiektu = index_obiektu;
				min_distance = distance;
			}
		}
		//usuwam zeby nie uwzgledniac w nadtepnym wywolaniu tego obiektu, po to tez kopiowalem zawartosc zbioru uczacego zebym mogl go (te kopie) bezkarnie modyfikowac
		zbior.remove(index_najblizszego_obiektu);
		return najblizszySasiadWZbiorze;
	}
	
	private String wybierzKlasePrzezGlosowanie(Obiekt obiekt_testowy, ArrayList<Obiekt> najblizsiSasiedzi){
		switch(metodaGlosowania){
			case "rownoprawne_wiekszosciowe":
				return glosowanieRownoprawneWiekszosciowe(najblizsiSasiedzi);
			case "wazone_odleglosciami":
				return glosowanieWazoneOdleglosciami(obiekt_testowy, najblizsiSasiedzi);
			default:
				return "";
		}
	}
	
	private String glosowanieRownoprawneWiekszosciowe(ArrayList<Obiekt> najblizsiSasiedzi){
		ArrayList<String> etykietyKlas = Atrybuty.getAtrybutKlasowy().dziedzina;
		int liczbaKlas = etykietyKlas.size();
		int[] liczbaObiektowDanejKlasyWsrodSasiadow = new int[liczbaKlas];
		Obiekt rozwazany_sasiad;
		String rozwazana_etykieta;
		for(int i = 0; i < najblizsiSasiedzi.size(); i++){
			rozwazany_sasiad = najblizsiSasiedzi.get(i);
			for(int j = 0; j < liczbaKlas; j++){
				rozwazana_etykieta = etykietyKlas.get(j);
				if(rozwazany_sasiad.getEtykietaKlasy().equals(rozwazana_etykieta)){
					liczbaObiektowDanejKlasyWsrodSasiadow[j]++;
					break;
				}
			}
		}
		int index_etykiety = getIndexOfMaxValue(liczbaObiektowDanejKlasyWsrodSasiadow);
		String zwracanaKlasa = etykietyKlas.get(index_etykiety);
		return zwracanaKlasa;
	}
	
	private int getIndexOfMaxValue(int[] liczbaObiektowDanejKlasyWsrodSasiadow){
		int maxValue = liczbaObiektowDanejKlasyWsrodSasiadow[0];
		int indexOfMaxValue = 0;
		for(int i = 0; i < liczbaObiektowDanejKlasyWsrodSasiadow.length; i++){
			if(liczbaObiektowDanejKlasyWsrodSasiadow[i] > maxValue){
				maxValue = liczbaObiektowDanejKlasyWsrodSasiadow[i];
				indexOfMaxValue = i;
			}
		}
		return indexOfMaxValue;
	}
	
	private String glosowanieWazoneOdleglosciami(Obiekt obiekt_testowy, ArrayList<Obiekt> najblizsiSasiedzi){
		ArrayList<String> etykietyKlas = Atrybuty.getAtrybutKlasowy().dziedzina;
		double[] sumy_wazonych_glosow = new double[etykietyKlas.size()];
		double waga;
		Obiekt rozwazany_sasiad;
		double licznik_wagi;
		double mianownik_wagi = getMianownikWagi(obiekt_testowy, najblizsiSasiedzi);
		for(int i = 0; i < najblizsiSasiedzi.size(); i++){
			rozwazany_sasiad = najblizsiSasiedzi.get(i);
			String klasa_rozwazanego_sasiada = rozwazany_sasiad.getEtykietaKlasy();
			licznik_wagi = Math.exp( - getDistance(obiekt_testowy, rozwazany_sasiad) );
			waga = licznik_wagi / mianownik_wagi;
			//sprawdzam jakiej klasy jest rozwazany sasiad i do sumyWag dla odpowiedniej klasy dodaje wage *1 -= wazonyglos
			for(int index_klasy = 0; index_klasy < etykietyKlas.size(); index_klasy ++){
				if( etykietyKlas.get(index_klasy).equals(klasa_rozwazanego_sasiada) ){
					sumy_wazonych_glosow[index_klasy] += waga * 1.0; //1 glos razy waga
				}
			}
		}
		int index_klasy_z_najwieksza_wartoscia_glosowania = getIndexOfMaxValue(sumy_wazonych_glosow);
		return etykietyKlas.get(index_klasy_z_najwieksza_wartoscia_glosowania);
	}
	
	private int getIndexOfMaxValue(double[] sumy_wazonych_glosow){
		double maxValue = sumy_wazonych_glosow[0];
		int indexOfMaxValue = 0;
		for(int i = 0; i < sumy_wazonych_glosow.length; i++){
			if(sumy_wazonych_glosow[i] > maxValue){
				maxValue = sumy_wazonych_glosow[i];
				indexOfMaxValue = i;
			}
		}
		return indexOfMaxValue;
	}
	
	private double getMianownikWagi(Obiekt obiekt_testowy, ArrayList<Obiekt> najblizsiSasiedzi){
		double mianownik = 0;
		for(int i = 0; i < najblizsiSasiedzi.size(); i++){
			mianownik += Math.exp( - getDistance(obiekt_testowy, najblizsiSasiedzi.get(i)) );
		}
		return mianownik;
	}
	
	private double[] getWariancjeAtrybutowWZbiorzeUczacym(){
		//zakladam ze wszystkie atrybuty poza klasowym sa numeryczne
		int liczbaAtrybutowNumerycznych = Atrybuty.getLiczbaAtrybutowZKlasa() - 1;
		Obiekt o;
		double[] sumy = new double[liczbaAtrybutowNumerycznych];
		double[] srednie = new double[liczbaAtrybutowNumerycznych];
		for(int i = 0; i < zbiorUczacy.size(); i++){
			o = zbiorUczacy.get(i);
			for(int atr = 0; atr < liczbaAtrybutowNumerycznych; atr++){
				sumy[atr] += o.wartosciAtrybutowNumerycznych[atr];
			}
		}
		for(int atr = 0; atr < liczbaAtrybutowNumerycznych; atr++){
			srednie[atr] = sumy[atr] / zbiorUczacy.size();
		}
		double[] sumyKwadratowOdchylenOdSredniej = new double[liczbaAtrybutowNumerycznych];
		double odchylenie;
		for(int i = 0; i < zbiorUczacy.size(); i++){
			o = zbiorUczacy.get(i);
			for(int atr = 0; atr < liczbaAtrybutowNumerycznych; atr++){
				odchylenie = srednie[atr] - o.wartosciAtrybutowNumerycznych[atr];
				sumyKwadratowOdchylenOdSredniej[atr] += Math.pow(odchylenie, 2);
			}
		}
		double[] wariancje = new double[liczbaAtrybutowNumerycznych];
		for(int atr = 0; atr < liczbaAtrybutowNumerycznych; atr++){
			wariancje[atr] = sumyKwadratowOdchylenOdSredniej[atr] / zbiorUczacy.size();
		}
		return wariancje;
	}
	
	private double getDistance(Obiekt o1, Obiekt o2){
		switch(metryka){
			//case "minkowski": //minkowski bierze parametr m, dla m=1 mamy manhattan, dla m=2 euklidesa, dla m=infty mamy czebyszewa
			//	return getMinkowskiDistance(o1, o2);
			case "manhattan":
				return getManhattanDistance(o1, o2);
			case "euclidean":
				return getEuclideanDistance(o1, o2);
			case "czebyszew":
				return getCzebyszewDistance(o1, o2);
			case "mahalanobis":
				return getMahalanobisDistance(o1, o2);
			default:
				return 0;
		}
	}
	
	/*private double getMinkowskiDistance(Obiekt o1, Obiekt o2){
	
	}*/
	
	private double getManhattanDistance(Obiekt o1, Obiekt o2){
		//inaczej minkowski z parametrem 1
		//System.out.println("manhattan!!!");
		double distance = 0;
		double attrDifference;
		for(int i = 0; i < Atrybuty.getLiczbaAtrybutowZKlasa(); i++){
			//moznaby bez tego sprawdzenia bo dane sa takie ze maja tylko atr numeryczne poza klasowym a klasowy jest ostatni wiec moznaby
			//itowac do przedostatniego atrybutu i zalozyc ze kazdy jest numeryczny, ale tak bedzie bardziej uniwersalnie z mozliwoscia rozszerzenia na attr enum
			if(Atrybuty.get(i).czyNumeryczny()){
				attrDifference = o1.wartosciAtrybutowNumerycznych[i] - o2.wartosciAtrybutowNumerycznych[i];
				distance += Math.abs(attrDifference);
			}
		}
		return distance;
	}
	
	private double getEuclideanDistance(Obiekt o1, Obiekt o2){
		//inaczej minkowski z parametrem 2
		//System.out.println("euclidean!!!");
		double distance = 0;
		double attrDifference;
		for(int i = 0; i < Atrybuty.getLiczbaAtrybutowZKlasa(); i++){
			//moznaby bez tego sprawdzenia bo dane sa takie ze maja tylko atr numeryczne poza klasowym a klasowy jest ostatni wiec moznaby
			//itowac do przedostatniego atrybutu i zalozyc ze kazdy jest numeryczny, ale tak bedzie bardziej uniwersalnie z mozliwoscia rozszerzenia na attr enum
			if(Atrybuty.get(i).czyNumeryczny()){
				attrDifference = o1.wartosciAtrybutowNumerycznych[i] - o2.wartosciAtrybutowNumerycznych[i];
				distance += Math.pow(attrDifference, 2);
			}
		}
		distance = Math.sqrt(distance);
		return distance;
	}
	
	private double getCzebyszewDistance(Obiekt o1, Obiekt o2){
		//inaczej minkowski z parametrem infinity
		double maxDistanceInOneDimension = 0;
		double attrDifference;
		for(int i = 0; i < Atrybuty.getLiczbaAtrybutowZKlasa(); i++){
			//moznaby bez tego sprawdzenia bo dane sa takie ze maja tylko atr numeryczne poza klasowym a klasowy jest ostatni wiec moznaby
			//itowac do przedostatniego atrybutu i zalozyc ze kazdy jest numeryczny, ale tak bedzie bardziej uniwersalnie z mozliwoscia rozszerzenia na attr enum
			if(Atrybuty.get(i).czyNumeryczny()){
				attrDifference = o1.wartosciAtrybutowNumerycznych[i] - o2.wartosciAtrybutowNumerycznych[i];
				attrDifference = Math.abs(attrDifference);
				if(attrDifference > maxDistanceInOneDimension){
					maxDistanceInOneDimension = attrDifference;
				}
			}
		}
		return maxDistanceInOneDimension;
	}

	private double getMahalanobisDistance(Obiekt o1, Obiekt o2){
		//System.out.println("Mahalanobis!!!");
		double sum = 0;
		double attrDifference = 0;
		double attrDifferencePow = 0;
		double normalizedAttrDifferencePow = 0; //kwadrat odleglosci danego atrybutu podzielony przez jego wariancje w zbiorze uczacym
		for(int atr = 0; atr < Atrybuty.getLiczbaAtrybutowZKlasa(); atr++){
			//moznaby bez tego sprawdzenia bo dane sa takie ze maja tylko atr numeryczne poza klasowym a klasowy jest ostatni wiec moznaby
			//itowac do przedostatniego atrybutu i zalozyc ze kazdy jest numeryczny, ale tak bedzie bardziej uniwersalnie z mozliwoscia rozszerzenia na attr enum
			if(Atrybuty.get(atr).czyNumeryczny()){
				attrDifference = o1.wartosciAtrybutowNumerycznych[atr] - o2.wartosciAtrybutowNumerycznych[atr];
				attrDifferencePow = Math.pow(attrDifference, 2);
				normalizedAttrDifferencePow = attrDifferencePow / wariancjeAtrybutow[atr];
				sum += normalizedAttrDifferencePow;
			}
		}
		double distance = Math.sqrt(sum);
		return distance;
	}



}
