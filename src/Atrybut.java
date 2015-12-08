import java.util.ArrayList;


public class Atrybut {
	String nazwa;
	String typ;
	ArrayList<String> dziedzina;
	ArrayList<Przedzial> przedzialy;
	
	public Atrybut(String nazwaAtrybutu, String typAtrybutu){
		dziedzina = new ArrayList<String>();
		przedzialy = new ArrayList<Przedzial>();
		nazwa = nazwaAtrybutu;
		typ = typAtrybutu;
		//jezeli typ nie jest liczbowy to wypelnij liste zbior wartosci
		if((!typ.equals("REAL"))&&(!typ.equals("real"))){
			String wartosciEnum = typAtrybutu;
			wartosciEnum = wartosciEnum.replace('{', ' ');
			wartosciEnum = wartosciEnum.replace('}', ' ');
			wartosciEnum = wartosciEnum.trim();
			String[] split = wartosciEnum.split(",");
			for(int i=0; i<split.length; i++)
				dziedzina.add(split[i]);
		}
		//dyskretyzuj(100, 200, 10);
		//piszWszystko();
	}
	
	public String getWartosc(int i){
		if(czyNumeryczny()){
			Przedzial przedzial = przedzialy.get(i);
			String przedzialString = new String(przedzial.dol+" "+przedzial.gora);
			return przedzialString; 
		}
		return dziedzina.get(i);
	}
	
	public void dyskretyzujStalaSzerokoscPrzedzialu(double minValue, double maxValue, int numIntervals){
		//jesli typ jest liczbowy to dyskretyzuj jesli nie to nie rob nic
		if((typ.equals("REAL"))||typ.equals("real")){
			double dlugoscPrzedzialu = (maxValue-minValue)/numIntervals;
			//2 linie ponizej zeby wartosc minValue tez wpadla do ktoregos (dokladniej - najnizszego) przedzialu
			double dol = minValue-10000000;
			double gora = minValue+dlugoscPrzedzialu;
			przedzialy = new ArrayList<Przedzial>();
			przedzialy.add(new Przedzial(dol, gora));
			for(int i=1; i<numIntervals-1; i++){
				dol = minValue+(i*dlugoscPrzedzialu);
				gora = minValue+((i+1)*dlugoscPrzedzialu);
				przedzialy.add(new Przedzial(dol, gora));
			}
			dol = minValue+((numIntervals-1)*dlugoscPrzedzialu);
			gora = minValue+((numIntervals)*dlugoscPrzedzialu)+10000000;
			przedzialy.add(new Przedzial(dol, gora));
		} else {
			;
		}
		//System.out.println("Po dyskretyzacji liczba mozliwych przedzialow = "+przedzialy.size()+" numintervals="+numIntervals);
	}
	
	
//-----------------------------------------------------------------------------------------------------------
	public void dyskretyzujOneR(double[] values, String[] classes, int minObjInPart){
		//jesli typ jest liczbowy to dyskretyzuj jesli nie to nie rob nic
		if((typ.equals("REAL"))||typ.equals("real")){
			//sort
			double minVal;
			int minIndex = 0;
			String tmpString;
			double tmpDouble;
			for(int i=0; i<values.length; i++){
				minVal = Double.MAX_VALUE;
				for(int j=i; j<values.length; j++){
					if(values[j]<=minVal){
						minVal = values[j];
						minIndex = j;
					}	
				}
				//zamieniam i z j (sortuje wartosci jednoczesnie zmieniajac miejsca etykiet klas w classes[])
				tmpDouble = values[i];
				values[i] = values[minIndex];
				values[minIndex] = tmpDouble;
				tmpString = classes[i];
				classes[i] = classes[minIndex];
				classes[minIndex] = tmpString;
			}
			//for(int i=0; i<values.length; i++)
			//	System.out.println("values["+i+"]="+values[i]+"  klasa: "+classes[i]);
			// mam posortowane
			przedzialy = new ArrayList<Przedzial>();
			double dol, gora;
			dol = Double.MIN_VALUE;
			String najliczniejszaKlasa;
			int indexPoczatkuPrzedzialu=0;
			int indexKoncaPrzedzialu=minObjInPart;
			while(indexPoczatkuPrzedzialu<values.length){
				najliczniejszaKlasa = najliczniejszaKlasaPrzedzialu(classes, indexPoczatkuPrzedzialu, indexKoncaPrzedzialu);
				while( (indexKoncaPrzedzialu+1<values.length) && (najliczniejszaKlasa.equals(classes[indexKoncaPrzedzialu+1])) ){
					indexKoncaPrzedzialu++;
					najliczniejszaKlasa = najliczniejszaKlasaPrzedzialu(classes, indexPoczatkuPrzedzialu, indexKoncaPrzedzialu);
				}
				if(indexPoczatkuPrzedzialu != 0)
					dol = values[indexPoczatkuPrzedzialu];
				if(indexKoncaPrzedzialu+minObjInPart > values.length){
					przedzialy.add(new Przedzial(dol, Double.MAX_VALUE));
					break;
				}
				gora = values[indexKoncaPrzedzialu];
				if(dol == gora){
					indexKoncaPrzedzialu++;
				} else {
					przedzialy.add(new Przedzial(dol, gora));
					indexPoczatkuPrzedzialu = indexKoncaPrzedzialu;
					indexKoncaPrzedzialu = indexPoczatkuPrzedzialu+minObjInPart;
				}	
			}
			piszWszystko();

		} else {
			;
		}
		//System.out.println("Po dyskretyzacji liczba mozliwych przedzialow = "+przedzialy.size()+" numintervals="+numIntervals);	
	}
	
	public String najliczniejszaKlasaPrzedzialu(String[] classes, int indexPoczatkuPrzedzialu, int indexKoncaPrzedzialu){
		ArrayList<String> dziedzinaAtrybutuKlasowego = Atrybuty.getAtrybutKlasowy().dziedzina;
		int[] licznosciKlas = new int[dziedzinaAtrybutuKlasowego.size()];
		for(int i=indexPoczatkuPrzedzialu; i<indexKoncaPrzedzialu; i++){
			for(int j=0; j<dziedzinaAtrybutuKlasowego.size(); j++){
				if(classes[i].equals(dziedzinaAtrybutuKlasowego.get(j))){
					licznosciKlas[j]++;
				}
			}		
		}
		int maxLicznosc=0, maxIndex=0;
		for(int i=0; i<licznosciKlas.length; i++){
			if(licznosciKlas[i]>=maxLicznosc){
				maxLicznosc = licznosciKlas[i];
				maxIndex = i;
			}
		}
		//System.out.println("najliczniejsza klasa: "+dziedzinaAtrybutuKlasowego.get(maxIndex)+" wystepuje "+licznosciKlas[maxIndex]+" razy.");
		return dziedzinaAtrybutuKlasowego.get(maxIndex);
	}
	
//------------------------------------------------------------------------------------------------------------------
	
	
	public int getLiczbaMozliwychWartosciLubPrzedzialow(){
		if((typ.equals("REAL"))||(typ.equals("real")))
			return przedzialy.size();
		return dziedzina.size();
	}

	public void piszWszystko(){
		System.out.print("\nNazwa: "+nazwa+", typ: "+typ);
		if((typ.equals("REAL"))||typ.equals("real"))
			for(int i=0; i<przedzialy.size(); i++)
				przedzialy.get(i).piszPrzedzial();
		else
			for(int i=0; i<dziedzina.size(); i++)
				System.out.print(" "+dziedzina.get(i)+" ");
		System.out.println("\n\n");
	}
	
	public boolean czyNumeryczny(){
		if((typ.equals("REAL"))||(typ.equals("real")))
			return true;
		return false;
	}
	
	public Przedzial getPrzedzialDoKtoregoWpada(double a){
		Przedzial przedzial = new Przedzial(0, 0);
		for(int i=0; i<przedzialy.size(); i++){
			przedzial = przedzialy.get(i);
			if(przedzial.czyNalezy(a))
				return przedzial;
		}
		return przedzial;
	}
	
	
}
