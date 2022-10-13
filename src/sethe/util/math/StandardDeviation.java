package sethe.util.math;

import sethe.PoI;

public class StandardDeviation {

	public double calVar(int arr[], int m) {
		int[] data = arr;

		// The mean average
		int mean = m;

		// The variance
		double variance = 0;
		for (int i = 0; i < data.length; i++) {
		    variance += Math.pow(data[i] - mean, 2);
		}
		variance /= data.length;

		return variance;
	}

	public double calcSD(int[] arr, int mean) {
		double var = calVar(arr, mean);
		double std = Math.sqrt(var);
		return std;
	}

	public double calcSDPoIs(PoI[] pois) {
		int [] arr = new int[pois.length - 1];
		for(int k=0, i = 1; i < pois.length; i++) {
			arr[k++] = (pois[i].getPosition() - pois[i-1].getPosition());
		}
		return calcSD(arr, 1);
	}
	
	public static void main(String[] args) {
		PoI p1 = new PoI();	p1.setPosition(1);
		PoI p2 = new PoI();	p2.setPosition(2);
		PoI p3 = new PoI();	p3.setPosition(3);
		PoI p4 = new PoI();	p4.setPosition(4);
		PoI p5 = new PoI();	p5.setPosition(7);
		PoI[] pois = {p1,p2,p3,p4,p5};

//		int[] arr = { 12, 32, 11, 55, 10, 23, 14, 30 };
		int[] arr = {1,1,1,3};

		StandardDeviation calsd = new StandardDeviation();
		double res = calsd.calcSD(arr, 1);	
		System.out.format("SD = %.6f\n", res);

		res = calsd.calcSDPoIs(pois);
		System.out.format("Standard Deviation = %.6f\n", res);
		System.out.format("score = %.6f", (((1 * res)/20) -1));

//		StandardDeviation calsd = new StandardDeviation();
//		double res = calsd.calcSD(arr);	
//		System.out.format("Standard Deviation = %.6f\n", res);
//
//		res = calsd.calcSDPoIs(pois);
//		System.out.format("Standard Deviation = %.6f", res);
	}
}