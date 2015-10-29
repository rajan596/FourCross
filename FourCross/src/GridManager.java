/**
 * @author RAJAN
 * Utility class to manage moves an game stats
 * 
 * */

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

public class GridManager {
	
	/* Returns Point if (x,y) is in any valid 9 locations
	 * Returns postion not co ordinates
	 */
	public static Point getLocation(int xx,int yy,Point P[][],int r) 
	{
		for(int i=0;i<3;i++) {
			for(int j=0;j<3;j++) {
				if(Math.abs(P[i][j].x - xx) <= r && Math.abs(P[i][j].y - yy) <= r) {
					Point p=new Point(i,j);
					return p;
				}
			}
		}
		return null;	
	}

	/* Returns current move is valid or not ?
	 * 
	 * */
	public static boolean isvalidMove(int[][] gameStats, int currentPlayer, Point pre, Point next) {
		
		if(pre==null || next==null)
			return false;
		
		if(pre.x<0 || pre.x>2 || pre.y<0 || pre.y>2 || next.x<0 || next.x>2 || next.y<0 || next.y>2)
			return false;
		
		/* previous selected warrier was not current Player */
		if(gameStats[pre.x][pre.y]!=currentPlayer)
			return false;
		
		/* If next location is not empty */
		if(gameStats[next.x][next.y]!=Stats.BLANK)
			return false;
		
		/* On same X axis */
		if(pre.x==next.x) {
			/* next to each other */
			if(Math.abs(pre.y-next.y)==1) {
				return true;
			}
			/* Check if middle location is having opponents worrier or not */
			else {
				int middle= 1 + Math.min(pre.y, next.y);
				
				if(gameStats[pre.x][middle]==Stats.BLANK || gameStats[pre.x][middle]==currentPlayer)
					return false;
				else
					return true;
			}			
				
		}
		
		if(pre.y==next.y) {
			/* next to each other */
			if(Math.abs(pre.x-next.x)==1) {
				return true;
			}
			/* Check if middle location is having opponents worrier or not */
			else {
				int middle= 1 + Math.min(pre.x, next.x);
				
				if(gameStats[middle][pre.y]==Stats.BLANK || gameStats[middle][pre.y]==currentPlayer)
					return false;
				else
					return true;
			}			
		}
		
		return false;
	}
	
	/*
	 * Moves player from previous selected position tonext position
	 * */
	public static int[][] movePlayer(int[][] gameStats, int currentPlayer, Point pre, Point next) {
		// TODO Auto-generated method stub
		
		int temp=gameStats[pre.x][pre.y];
		gameStats[pre.x][pre.y]=gameStats[next.x][next.y];
		gameStats[next.x][next.y]=temp;
		
		/* If middle one is opponent's warrier remove it */
		if(pre.x==next.x) {
			if(Math.abs(pre.y-next.y)>1) {
				int middle=1 + Math.min(pre.y,next.y);
				gameStats[pre.x][middle]=Stats.BLANK;
			}
		}
		else {
			if(Math.abs(pre.x-next.x)>1) {
				int middle=1 + Math.min(pre.x,next.x);
				gameStats[middle][pre.y]=Stats.BLANK;
			}
		}
		
		return gameStats;
	}

	/*
	 * Game ends in 2 conditions
	 * 1.) All worriers of any 1 player is dead
	 * 2.) current Player do not have valid move
	 * 
	 * */
	public static String gameOver(int[][] gameStats ,int remainingMovesFromDraw , int currentPlayer) {
		
		/*
		 * Checking for remaining worriers
		 * */
		
		if(remainingMovesFromDraw==0) {
			return "DRAW";
		}
		
		boolean player1=false,
				player2=false;
		
		for(int i=0;i<3;i++) {
			for(int j=0;j<3;j++) {
				if(gameStats[i][j]==Stats.P1)
					player1=true;
				
				if(gameStats[i][j]==Stats.P2)
					player2=true;
			}
		}
		
		if(!player1)
			return "Player 2";
		
		if(!player2)
			return "Player 1";
		
		boolean validMoveRemained=checkForValidMoves(gameStats , currentPlayer);
		
		if(!validMoveRemained) {
			String winner;
			if(currentPlayer==Stats.P1) {
				winner="Player 2";
			}
			else {
				winner="Player 1";
			}
			return winner;
		}
		
		//System.out.println(currentPlayer + " have valid moves");
		
		return "GameIsNotOver";
	}
	
	
	private static boolean checkForValidMoves(int[][] gameStats, int currentPlayer) {
		
		boolean move=false;
		
		for(int i=0;i<3;i++) {
			for(int j=0;j<3;j++) {
				if(gameStats[i][j]==currentPlayer) {
					move=checkAtDist1(gameStats, currentPlayer ,i, j);
					
					if(move) {
						return true;
					}
					move=checkAtDist2(gameStats, currentPlayer , i, j );
					
					if(move) {
						return true;
					}
			
				}
			}
		}
		
		return false;
	}

	public static boolean checkAtDist2(int[][] gameStats, int currentPlayer , int i, int j) {
		
		int x[]={2,-2,0,0};
		int y[]={0,0,-2,2};
		
		int opponent=currentPlayer==Stats.P1 ? Stats.P2 : Stats.P1;
		
		for(int k=0;k<4;k++) {
			int xx= i + x[k];
			int yy= j + y[k];
			
			if(xx >=0 && xx <3 && yy >=0 && yy<3 ) {
				int p=(i + xx)/2;
				int q=(j + yy)/2;
				
				if(p>=0 && p<3 && q>=0 && q<3 && gameStats[xx][yy]==Stats.BLANK && 
						gameStats[p][q]==opponent) {
					return true;
				}
			}
		}
		
		return false;
	}

	public static boolean checkAtDist1(int[][] gameStats, int currentPlayer ,int i, int j) {
		
		int x[]={1,-1,0,0};
		int y[]={0,0,-1,1};
		
		for(int k=0;k<4;k++) {
			int xx= i + x[k];
			int yy= j + y[k];
			
			if(xx >=0 && xx <3 && yy >=0 && yy<3 ) {
				if(gameStats[xx][yy]==Stats.BLANK) {
					return true;
				}
			}
		}
		
		return false;
	}

	public static int totalRemainedPlayers(int[][] gameStats, int playerID) {
		
		int total=0;
			
		for(int i=0;i<3;i++) {
			for(int j=0;j<3;j++) {
				if(gameStats[i][j]==playerID) {
					total++;
				}
			}
		}
		
		return total;
	}
	
}
