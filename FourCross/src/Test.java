
public class Test {
	public static boolean checkAtDist2(int[][] gameStats, int currentPlayer , int i, int j) {
		
		int x[]={2,-2,0,0};
		int y[]={0,0,-2,2};
		
		int opponent=currentPlayer==Stats.P1 ? Stats.P2 : Stats.P1;
		
		for(int k=0;k<4;k++) {
			int xx= i + x[k];
			int yy= j + y[k];
			System.out.println(" xx : yy << " + xx +" "+yy+" ");
			
			if(xx >=0 && xx <3 && yy >=0 && yy<3 ) {
				int p=(i + xx)/2;
				int q=(j + yy)/2;
				
				System.out.println(" xx : yy : p : q >> " + xx +" "+yy+" "+ p+" "+ q);
				
				if(p>=0 && p<3 && q>=0 && q<3 && gameStats[xx][yy]==Stats.BLANK && 
						gameStats[p][q]==opponent) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	public static void main(String args[]) {
		int gameStats[][]={
				{Stats.P1 , Stats.P2 , Stats.P1},
				{Stats.P2 , Stats.BLANK , Stats.P2},
				{Stats.P1 , Stats.P2 , Stats.P1},
		};
		
		System.out.println(checkAtDist2(gameStats, Stats.P1,2, 2));
	}
}
