import java.util.ArrayList;

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
	
	public String getClass(Obiekt o){
		ArrayList<Obiekt> kopiaZbioruUczacego = new ArrayList<Obiekt>();
		for(int i = 0; i < zbiorUczacy.size(); i++){
			kopiaZbioruUczacego.add(new Obiekt(zbiorUczacy.get(i)));
		}
		ArrayList<Obiekt> najblizsziSasiedzi = new ArrayList<Obiekt>();
		//najblizsi sasiedzi beda ustawieni w kolejnosci od najblizszego do najdalszego
		Obiekt sasiad;
		while(najblizsziSasiedzi.size() < liczbaSasiadow){
			sasiad = getKolejnyNajblizszy(o, kopiaZbioruUczacego);
			najblizsziSasiedzi.add( sasiad );
		}
		String klasa = wybierzKlasePrzezGlosowanie(najblizsziSasiedzi);
		return klasa;
	}
	
	private Obiekt getKolejnyNajblizszy( Obiekt o, ArrayList<Obiekt> zbior ){
		Obiekt najblizszySasiadWZbiorze = zbior.get(0);
		int index_najblizszego_obiektu = 0;
		double min_distance = getDistance(o, zbior.get(0));
		double distance;
		for( int index_obiektu = 0; index_obiektu < zbior.size(); index_obiektu++ ){
			distance = getDistance(o, zbior.get(index_obiektu));
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
	
	private String wybierzKlasePrzezGlosowanie(ArrayList<Obiekt> najblizsiSasiedzi){
		switch(metodaGlosowania){
			case "rownoprawne_wiekszosciowe":
				return glosowanieRownoprawneWiekszosciowe(najblizsiSasiedzi);
			case "wazone_odleglosciami":
				return glosowanieWazoneOdleglosciami(najblizsiSasiedzi);
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
	
	private String glosowanieWazoneOdleglosciami(ArrayList<Obiekt> najblizsiSasiedzi){
		return wywal blad;
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
