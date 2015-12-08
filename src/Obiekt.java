import java.io.BufferedReader;
import java.util.ArrayList;


public class Obiekt {
	//zakladam ze ostatni atrybut stanowi etykiete klasy
	//ArrayList<Atrybut> atrybuty;
	double[] wartosciAtrybutowNumerycznych;
	String[] wartosciAtrybutowEnum;
	int liczbaKlas;
	
	public Obiekt(String textLine){	
		//atrybuty = atr;
		int liczbaAtrybutow = Atrybuty.getLiczbaAtrybutowZKlasa();
		//w tych 2 nie wszystkie pola tablic beda wypelnione ale nie bede sie odwolywal do tych indeksow
		wartosciAtrybutowNumerycznych = new double[liczbaAtrybutow];
		wartosciAtrybutowEnum = new String[liczbaAtrybutow];
		String[] splittedLine = textLine.split(",");
		for(int i=0; i<splittedLine.length; i++){
			if(Atrybuty.get(i).czyNumeryczny())
				wartosciAtrybutowNumerycznych[i] = Double.parseDouble(splittedLine[i]);
			else
				wartosciAtrybutowEnum[i] = splittedLine[i];
		}
	}
	
	public Obiekt(Obiekt o){
		int liczbaAtrybutow = Atrybuty.getLiczbaAtrybutowZKlasa();
		wartosciAtrybutowNumerycznych = new double[liczbaAtrybutow];
		wartosciAtrybutowEnum = new String[liczbaAtrybutow];
		for(int i = 0; i < liczbaAtrybutow; i++){
			wartosciAtrybutowNumerycznych[i] = o.wartosciAtrybutowNumerycznych[i];
			wartosciAtrybutowEnum[i] = o.wartosciAtrybutowEnum[i];
		}
	}
	
	public String getEtykietaKlasy(){
		return wartosciAtrybutowEnum[wartosciAtrybutowEnum.length-1];
	}
	
	public void pisz(int i){
		System.out.println("Obiekt "+(i+1)+":");
		System.out.print("Atrybuty: ");
		for(int j=0; j<Atrybuty.atrybuty.size(); j++){
			System.out.print(Atrybuty.atrybuty.get(j).nazwa+", ");
		}
		System.out.println();
		System.out.print("Typy: ");
		for(int j=0; j<Atrybuty.atrybuty.size(); j++){
			System.out.print(Atrybuty.atrybuty.get(j).typ+", ");
		}
		System.out.println();
		System.out.print("Wartosci: ");
		for(int j=0; j<wartosciAtrybutowEnum.length; j++){
			if(Atrybuty.atrybuty.get(j).czyNumeryczny())
				System.out.print(wartosciAtrybutowNumerycznych[j]+", ");
			else
				System.out.print(wartosciAtrybutowEnum[j]+", ");
		}
		System.out.println("\n\n");
	}

}
