import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
//moge modyfikowac liczbe przedzialow prz ydyskretyzacji w tej chwili podaje tam liczbe klas

public class Main {

	/**
	 * @param args
	 * @throws IOException 
	 */
	
	static ArrayList<Obiekt> dane;
	static int liczbaFoldow;
	static double[] wyniki;
	static boolean czyDaneZdyskretyzowane = false;
	
	public static void main(String[] args) throws IOException {
		System.out.println("Wybierz zbiór danych:\n1 - iris\n2 - wine\n3 - glass\n4 - fertility\n5 - seeds\n\n");
		Scanner in = new Scanner(System.in);
		int choose = in.nextInt();
		String fileName = "";
		switch(choose){
			case 1:
				fileName = "iris.arff";
				break;
			case 2: 
				fileName = "wine.arff";
				break;
			case 3: 
				fileName = "glass.arff";
				break;
			case 4:
				fileName = "fertility.arff";
				break;
			case 5: 
				fileName = "seeds.arff";
				break;
			default:
				break;
		}
		in.close();
		String filePath = "/home/tomek/workspace/systemyUczaceSie/knn/data/"+fileName;
		wczytajDane(filePath);
		//testPoprawnosciWczytania();
/*		
//dyskretyzacja =========================================================================================
		//ustalam liczbe klas - na tyle przedzialow bede dzielil atrybuty numeryczne ile jest klas
		//Atrybut etykietaKlasy = Atrybuty.atrybuty.get(Atrybuty.atrybuty.size()-1);
		//int liczbaKlas = etykietaKlasy.dziedzina.size();
		int liczbaPrzedzialow = 5;
		//dyskretyzujStalaSzerokoscPrzedzialu(liczbaPrzedzialow);
		int minObjectsInPartition = 6;
		dyskretyzujOneR(minObjectsInPartition);
		
//koniec procedury dyskretyzacji metoda dyskretyzacji jest zawarta w klasie atrybut=======================
*/
		int liczbaMiar = 4;
		double[] sumaWynikowCzesciowych = new double[liczbaMiar];
		int liczbaTestow = 100;
		liczbaFoldow = 5;
		int liczbaSasiadow = 5;	
		//String metryka = "manhattan";
		//String metryka = "euclidean";
		//String metryka = "czebyszew";
		String metryka = "mahalanobis";
		//String metodaGlosowania = "rownoprawne_wiekszosciowe";
		String metodaGlosowania = "wazone_odleglosciami";
		
		for(int i=0; i<liczbaTestow; i++){
			Kroswalidacja kroswalidacja = new Kroswalidacja(liczbaFoldow, dane, liczbaSasiadow, metryka, metodaGlosowania);
			wyniki = kroswalidacja.getWynikKroswalidacji();
			for(int j=0; j<liczbaMiar; j++){
				sumaWynikowCzesciowych[j] += wyniki[j];
			}
		}
		for(int i=0; i<liczbaMiar; i++)
			wyniki[i] = sumaWynikowCzesciowych[i] / liczbaTestow;		
		
		
/*//kroswalidacja jeden raz
		liczbaFoldow = 5;
		int liczbaSasiadow = 5;	
		String metryka = "mahalanobis";
		String metodaGlosowania = "rownoprawne_wiekszosciowe";
		Kroswalidacja kroswalidacja = new Kroswalidacja(liczbaFoldow, dane, liczbaSasiadow, metryka, metodaGlosowania);
		wyniki = kroswalidacja.getWynikKroswalidacji();
//koniec kroswalidacji*/
		
/*//uczenie i testowanie na tym samym zbiorze
		int liczbaSasiadow = 5;	
		String metryka = "mahalanobis";
		// sposob glosowania
		String metodaGlosowania = "rownoprawne_wiekszosciowe";
		KlasyfikatorKNN klasyfikator = new KlasyfikatorKNN(liczbaSasiadow, metryka, metodaGlosowania, dane);
		Testowanie testowanie = new Testowanie(klasyfikator, dane);
		wyniki = testowanie.getWynikiKoncowe();
//koniec uczenia i testowania na tym samym zbiorze	*/	
		piszWyniki();
		
	}
	
	public static void wczytajDane(String filePath) throws FileNotFoundException{	
		FileReader fileReader = new FileReader(filePath);	
		Scanner scanner = new Scanner(fileReader);
		String textLine;
		String[] splittedLine;
		String firstWord, nazwaAtrybutu, typAtybutu;
		do {
			textLine = scanner.nextLine();
			splittedLine = textLine.split(" ");
			firstWord = splittedLine[0];
			if((firstWord.equals("@attribute"))||(firstWord.equals("@ATTRIBUTE"))){
				nazwaAtrybutu = splittedLine[1];
				typAtybutu = splittedLine[2];
				Atrybuty.atrybuty.add(new Atrybut(nazwaAtrybutu, typAtybutu));
			}	  
		} while((!firstWord.equals("@data"))&&(!firstWord.equals("@DATA")));
		dane = new ArrayList<Obiekt>();
		do {
			textLine = scanner.nextLine();
			dane.add(new Obiekt(textLine));	  
		} while(scanner.hasNext());
		scanner.close();
	}

	public static void testPoprawnosciWczytania(){
		//test poprawnosci dzialania operacji wczytywania danych z pliku
		Obiekt o;
		System.out.println(dane.size());
		for(int i = 0; i < dane.size(); i++){
			o = dane.get(i);
			o.pisz(i);
		}
	}
	
	public static void piszWyniki(){
		System.out.println("Wyniki końcowe:");
		System.out.println("%Correctly classified instances = "+wyniki[0]*100+"%");
		System.out.println("Precision = "+wyniki[1]);
		System.out.println("Recall = "+wyniki[2]);
		System.out.println("F-measure = "+wyniki[3]);
	}
	
	public static void dyskretyzujStalaSzerokoscPrzedzialu(int lP){
		Atrybuty.dyskretyzujStalaSzerokoscPrzedzialu(lP);
		czyDaneZdyskretyzowane = true;
	}
	
	public static void dyskretyzujOneR(int minObjInPart){
		Atrybuty.dyskretyzujOneR(minObjInPart);
		czyDaneZdyskretyzowane = true;
	}

}
