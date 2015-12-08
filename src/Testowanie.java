import java.util.ArrayList;


public class Testowanie {
	KlasyfikatorKNN klasyfikator;
	ArrayList<Obiekt> daneTestowe;
	
	double tP, tN, fP, fN;
	double correctlyClassified, precision, recall, fmeasure;
	//double[][] wynikiCzesciowe;
	int[][] confusionMatrix;
	double[] wyniki;
	
	public Testowanie(KlasyfikatorKNN klasyfikator, ArrayList<Obiekt> daneTestowe){
		this.klasyfikator = klasyfikator;
		this.daneTestowe = daneTestowe; 
		wyniki = testuj();
	}
	
	public double[] getWynikiKoncowe(){
		return wyniki;
	}
	
	public double[] testuj(){
		//testowanie regul, zwracam wartosci parametrow		
		wyniki = new double[4];
		
		int tP=0,  tN=0, fP=0, fN=0;
		double correctlyClassified=0, precision=0, recall=0, fmeasure=0;
		String przewidywanaKlasa, rzeczywistaKlasa, rozwazanaKlasa;
		Obiekt o;
		Atrybut atrybutKlasowy = Atrybuty.getAtrybutKlasowy();
		int liczbaKlas = atrybutKlasowy.dziedzina.size();
		confusionMatrix = new int[liczbaKlas][liczbaKlas];
		for(int i=0; i<daneTestowe.size(); i++){
			o = daneTestowe.get(i);
			rzeczywistaKlasa = o.getEtykietaKlasy();
			int indexRzeczywistejKlasy=0;
			for(int k=0; k<atrybutKlasowy.dziedzina.size(); k++){
				if(atrybutKlasowy.dziedzina.get(k).equals(rzeczywistaKlasa))
					indexRzeczywistejKlasy = k;
			}
			przewidywanaKlasa = klasyfikator.getClass(o);
			int indexPrzewidywanejKlasy=0;
			for(int k=0; k<atrybutKlasowy.dziedzina.size(); k++){
				if(atrybutKlasowy.dziedzina.get(k).equals(przewidywanaKlasa))
					indexPrzewidywanejKlasy = k;
			}
			confusionMatrix[indexRzeczywistejKlasy][indexPrzewidywanejKlasy]++;
		}
// probuje policzyc precision recal itd na podstawie tylko confusion matrix - mysle ze sie da
		//wszystko dla kazdej klasy licze
		double[][] wynikiDlaKlas = new double[liczbaKlas][8];

		for(int i=0; i<liczbaKlas; i++){
			tP=0;  tN=0; fP=0; fN=0;
			
			tP = confusionMatrix[i][i];
			for(int j=0; j<liczbaKlas; j++)
				if(i!=j)
					fN += confusionMatrix[i][j];
			for(int j=0; j<liczbaKlas; j++)
				if(i!=j)
					fP += confusionMatrix[j][i];
			for(int j=0; j<liczbaKlas; j++)
				for(int k=0; k<liczbaKlas; k++)
					if( (i!=j)&&(i!=k) )
						tN +=confusionMatrix[j][k];
					
			correctlyClassified = (double)tP;
			if((tP+fP) == 0)
				precision = 0;
			else
				precision = (double)tP/(tP+fP);
			if((tP+fN) == 0)
				recall = 0;
			else
				recall = (double)tP/(tP+fN);
			if((precision+recall) == 0)
				fmeasure = 0;
			else
				fmeasure = (double)2*precision*recall/(precision+recall);
			
			wynikiDlaKlas[i][0] = correctlyClassified;
			wynikiDlaKlas[i][1] = precision;
			wynikiDlaKlas[i][2] = recall;
			wynikiDlaKlas[i][3] = fmeasure;
			wynikiDlaKlas[i][4] = tP;
			wynikiDlaKlas[i][5] = tN;
			wynikiDlaKlas[i][6] = fP;
			wynikiDlaKlas[i][7] = fN;		
		}
		
		piszWynikiDlaKlas(wynikiDlaKlas);
		
		//ustalenie wag dla klas
		//najpierw potrzebuje licznosci klas
		String klasa;
		double[] licznosciKlas = new double[liczbaKlas];
		for(int i=0; i<liczbaKlas; i++){
			klasa = atrybutKlasowy.dziedzina.get(i);
			for(int j=0; j<daneTestowe.size(); j++){
				o = daneTestowe.get(j);
				if(o.getEtykietaKlasy().equals(klasa))
					licznosciKlas[i]++;
			}
		}
		double[] wagaDlaKlasy = new double[liczbaKlas];
		for(int i=0; i<liczbaKlas; i++){
			wagaDlaKlasy[i] = licznosciKlas[i]/daneTestowe.size();
			
		}
		
		wyniki = new double[4];
		for(int i=0; i<liczbaKlas; i++){
			wyniki[0] += wynikiDlaKlas[i][0]; // tu tak zeby sie zgadzalo z weka- sprawdzic!!!
			for(int j=1; j<4; j++){
				wyniki[j] += wynikiDlaKlas[i][j]*wagaDlaKlasy[i];
			}	
		}
		//%correctly classified instances
		wyniki[0] /= daneTestowe.size();
		
		piszWyniki(wyniki, confusionMatrix);
		return wyniki;

	}
	
	
	public void piszWyniki(double[] wyniki, int[][] confusionMatrix){
		System.out.println("1-2-3-----<=Przewidywana klasa");
		for(int i=0; i<confusionMatrix.length; i++){
			for(int j=0; j<confusionMatrix[0].length; j++){
				System.out.print(confusionMatrix[i][j]+" ");
			}
			System.out.print(" rzeczywista klasa = "+(i+1));
			System.out.println();
		}
		//System.out.println("\n\n");
		System.out.println("%correctlyClassified: "+wyniki[0]*100+"%");
		System.out.println("Precision: "+wyniki[1]);
		System.out.println("Recall: "+wyniki[2]);
		System.out.println("FMeasure: "+wyniki[3]);
		System.out.println("\n\n");	
	}
	
	public void piszWynikiDlaKlas(double[][] wynikiDlaKlas){
		for(int i=0; i<wynikiDlaKlas.length; i++){
			System.out.println("Wyniki dla klas: Klasa: "+(i+1));
			System.out.println("%correctlyClassified: "+wynikiDlaKlas[i][0]*100+"%");
			System.out.println("Precision: "+wynikiDlaKlas[i][1]);
			System.out.println("Recall: "+wynikiDlaKlas[i][2]);
			System.out.println("FMeasure: "+wynikiDlaKlas[i][3]);
			System.out.println("tP: "+wynikiDlaKlas[i][4]);
			System.out.println("tN: "+wynikiDlaKlas[i][5]);
			System.out.println("fP: "+wynikiDlaKlas[i][6]);
			System.out.println("fN: "+wynikiDlaKlas[i][7]);
			System.out.println("\n\n");	
		}
	}
	
}
