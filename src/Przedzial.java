
public class Przedzial {
	//reprezentuje przedzialy otwarte "z dolu" i zamkniete "z gory"
	
	double dol, gora;
	
	public Przedzial(double a, double b){
		dol = a;
		gora = b;
	}
	
	public boolean czyNalezy(double x){
		if((dol<x)&&(x<=gora))
			return true;
		return false;
	}

	public void piszPrzedzial(){
		System.out.print(" ("+dol+", "+gora+"] ");
	}
}
