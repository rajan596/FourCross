/**
 * @author RAJAN
 * 
 * Player 1 : YOU
 * Player 2 : Computer (With Intellegence)
 *
 * */

import java.applet.Applet;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Random;

public class SinglePlayer extends Applet implements MouseListener{
	
	private static final long serialVersionUID = 1L;

	/* Setting locations and sizes */
	int x=100,   
		y=100,
		w=150,
		h=150,
		r=30;
	
	int currentPlayer=Stats.P1;
	int clickCount=0;
	
	/* When game starts fill 8 points with players */
	boolean fillingProcess=true;
	boolean gameover=false;
	String winner=null;
	
	int filledPlayers=0;
	int remainingMovesFromDraw=Stats.TOTAL_AVAILABLE_MOVES;
	int Tplayer1=4, Tplayer2=4;
	
	/* stores stats */
	int gameStats[][]={
			{Stats.BLANK , Stats.BLANK , Stats.BLANK},
			{Stats.BLANK , Stats.BLANK , Stats.BLANK},
			{Stats.BLANK , Stats.BLANK , Stats.BLANK},
	};
	
	/* Stores co-ordinates of the 9 places */
	Point P[][]=new Point[3][3];
	Point pre=null,next=null;
	
	public void init() {
		this.setSize(500,500);
		this.setBackground(Color.WHITE);
		
		/* Assigns point co ordinates  */
		for(int i=0;i<3;i++) {
			for(int j=0;j<3;j++){
				P[i][j]=new Point(j*w + x- r/2 , i*h + y- r/2);
			}
		}
		
		showStatus(" Put your Warrier ");
		addMouseListener(this);
	}
	
	public void paint(Graphics g) {
		
		/* Player List */
		
		g.setColor(Color.GREEN);
		g.fillOval(x, 25, r ,r);
		g.setFont(new Font("Arial", Font.ITALIC, 20));
		g.drawString(" YOU ", 3*x/2, 50);
		
		g.setColor(Color.ORANGE);
		g.fillOval(3*x, 25, r ,r);
		g.drawString("Computer ", 7*x/2, 50);
		
		g.setColor(Color.BLACK);
		
		/* Drawing grid */
		g.drawRect(x, y, 2*w , 2*h);
		g.drawRect(x, y, w, h);
		g.drawRect(x+w, y+h, w, h);
		
		/* Drawing Players */
		for(int i=0;i<3;i++) {
			for(int j=0;j<3;j++) {
				Color c=Stats.Cblank;
				
				if(gameStats[i][j]==Stats.P1) {
					c=Stats.CP1;
				}
				else if(gameStats[i][j]==Stats.P2) {
					c=Stats.CP2;
				}
				
				g.setColor(c);
				g.fillOval(P[i][j].x, P[i][j].y, r ,r);
				
			}
		}
		
		if(pre!=null) {			
			Color c=currentPlayer==Stats.P1 ? Stats.CP1 : Stats.CP2;
			g.setColor(c);
			g.fillOval(P[pre.x][pre.y].x - 5, P[pre.x][pre.y].y - 5, r + 10 , r + 10 );
		}
		
		if(winner==null || (fillingProcess==false && winner.equals("GameIsNotOver"))) {
			String msg=null;
			if(currentPlayer==Stats.P1) {
				msg=" Your Turn " ;
			}else {
				msg="Computer Thinking....";
			}
			g.setColor(Color.RED);
			g.setFont(new Font("Calibri", Font.CENTER_BASELINE, 25));
			g.drawString(msg, 3*x/2 , this.getHeight()-50);
			
			if(!fillingProcess) {
				msg="Remaining Moves From Draw : "+remainingMovesFromDraw;
				g.setColor(Color.BLUE);
				g.setFont(new Font("Calibri", Font.LAYOUT_LEFT_TO_RIGHT, 20));
				g.drawString(msg, 3*x/2 - 20, this.getHeight()-20);
			}
		}
		else if(!fillingProcess){
			String msg=null;
			if(winner.equals("Player 1")) {
				g.setColor(Color.GREEN);
				msg="YOU WON ";
			}
			else if(winner.equals("Player 2")){
				g.setColor(Color.ORANGE);
				msg="YOU LOSE";
			}
			else {
				g.setColor(Color.darkGray);
				msg="DRAW";
			}	
			
			g.setFont(new Font("Calibri", Font.HANGING_BASELINE, 25));
			g.drawString(msg, 3*x/2, this.getHeight()-50);
		}
		
	}
	
	public void changePlayer() {
		currentPlayer=currentPlayer==Stats.P1 ? Stats.P2 : Stats.P1;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		//System.err.println("clicked...");
		
		int xx=e.getX();
		int yy=e.getY();
		
		//System.out.println(xx);
		
		/* returns null or (i,j) of gameStatus Point */
		Point p=GridManager.getLocation(xx,yy,P,r);
		
		if(p==null) {
			return;
		}
		
		//System.out.println("Point is : " + p.x +" , " +p.y);
		
		/* fill players in the game starting */
		if(fillingProcess) {
			/* draws player at specified point */
			int success=1;
			
			if(gameStats[p.x][p.y]!=Stats.BLANK) {
				success=0;
			}
			else {
				gameStats[p.x][p.y]=currentPlayer;
			}
						
			/* your turn to put worrier */
			if(success==1) {
				fillPlayer1();
			} 
			
			/* Computer's Turn to put worrier */
			if(currentPlayer==Stats.P2) {
				fillPlayer2();
			}
			
		}
		/* Moves */
		else {
			
			/* YOUR MOVE */
			if(currentPlayer==Stats.P1) {
				movePlayer1(p);
				checkGameStats();
			}
			
			/* Computer's Move */
			if(currentPlayer==Stats.P2 && pre==null && next==null && !gameover) {
				movePlayer2();
				checkGameStats();
			}		
		}
		
	}

	private void updateDrawConstraints() {

		int Tp1=GridManager.totalRemainedPlayers(gameStats,Stats.P1);
		int Tp2=GridManager.totalRemainedPlayers(gameStats,Stats.P2);
		
		if(Tplayer1 == Tp1 && Tplayer2==Tp2) {
			remainingMovesFromDraw--;
		}else {
			remainingMovesFromDraw=Stats.TOTAL_AVAILABLE_MOVES;
			Tplayer1=Tp1;
			Tplayer2=Tp2;
		}
		
	}

	private void fillPlayer2() {
		int success=0;
		
		while(success!=1) {
			Random random=new Random();
		
			int n=random.nextInt(9);
		
			int x=n%3;
			int y=n/3;
			
			if(gameStats[x][y]==Stats.BLANK) {
				success=1;
				gameStats[x][y]=Stats.P2;
			}
		}
		
		repaint();
		filledPlayers++;
		changePlayer();
		if(filledPlayers==8) {
			fillingProcess=false;
			this.showStatus("Game Started ....");
			return;
		}
		
	}

	private void fillPlayer1() {

		repaint();
		filledPlayers++;
		changePlayer();
		if(filledPlayers==8) {
			fillingProcess=false;
			this.showStatus("Game Started ....");
			return;
		}
		
		if(currentPlayer==Stats.P1) {
			showStatus(" Put your Warrier ");
		}
		else {
			showStatus("Computer Thinking....");
		}
	}

	private void checkGameStats() {

		winner=GridManager.gameOver(gameStats,remainingMovesFromDraw,currentPlayer);

		if(!winner.equals("GameIsNotOver")) {
			gameover=true;
		}
		
		repaint();
	}

	private void movePlayer1(Point p) {
		
			if(pre==null) { 
				if(gameStats[p.x][p.y]==currentPlayer) {
					pre=p;
					//System.out.println("Previous : " + pre.x +" : "+ pre.y);
				}
			}
			else {
				
				if(gameStats[p.x][p.y]==Stats.BLANK) {
					next=p;
					//System.out.println("Next : " + next.x +" : "+ next.y);
					if(GridManager.isvalidMove(gameStats,currentPlayer,pre,next)) {
						gameStats=GridManager.movePlayer(gameStats,currentPlayer,pre,next);
						updateMoveStatus(pre,next);
						repaint();
						changePlayer();
						updateDrawConstraints();
						pre=next=null;
						//System.out.println("Player Changed");
					}
				}
				
				if(gameStats[p.x][p.y]==currentPlayer) {
					pre=p;
					next=null;
				}
			}
		
	}
	
	/*
	 * Computer's move 
	 * */
	private void movePlayer2() {
		repaint();
		int result=moveAI(currentPlayer , remainingMovesFromDraw , 0);
		
		//System.out.println("Result >> " + result);
		pre=next=null;
		changePlayer();
	}
	
	private int moveAI(int currentPlayer2, int remainingMovesFromDraw2 , int depth) {
		
		/* base Case :: Terminating Conditions */
		String gameresult=GridManager.gameOver(gameStats, remainingMovesFromDraw2, currentPlayer2);
		
		//System.out.println("Inside Move AI");
		
		int opponent=currentPlayer2==Stats.P1?Stats.P2:Stats.P1;
		int remainedCurrentPlayers=GridManager.totalRemainedPlayers(gameStats, currentPlayer2);
		int remainedOpponents=GridManager.totalRemainedPlayers(gameStats, opponent);
		
		/* So current player would try to maximize its players while kill max opponenet's players */
		
		String currentPlayerName=currentPlayer2==Stats.P1 ? "Player 1" : "Player 2";
		String opponentPlayerName=currentPlayer2==Stats.P2 ? "Player 1" : "Player 2";
		
		if(gameresult.equals(currentPlayerName)) {
			return 100000*(remainingMovesFromDraw2 +1);
		}
		else if(gameresult.equals(opponentPlayerName)) {
			return -100000 + 1500/(remainingMovesFromDraw2+1);
		}
		else if(gameresult.equals("DRAW")){
			return 1000*(remainedCurrentPlayers) + 750*(remainedOpponents);
		}
		
		Point m1=new Point();
		Point m2=new Point();
		Point bestm1=new Point();
		Point bestm2=new Point();
		
		/* If Player1 is on move */
		if(currentPlayer2==Stats.P1) {
			//System.out.println("Your Turn");
			int maximumScore=-1000000,score=0;
			
			for(int i=0;i<3;i++) {
				for(int j=0;j<3;j++) {
					if(gameStats[i][j]==currentPlayer2) {
						
						//System.out.println("CP : " + currentPlayer2 + " >> i j >> " + i +" : " + j );
						
						/* Check all possible 8 Movess */
						for(int k=0;k<8;k++) {
							int x[]={0 , 0 , 1 ,-1 , 0 , 0 , 2 ,-2 };
							int y[]={-1, 1 , 0 , 0 , -2, 2 , 0 , 0 }; 
							
							m1.x=i;
							m1.y=j;
							m2.x=i + x[k];
							m2.y=j + y[k];
							
							if(GridManager.isvalidMove(gameStats, currentPlayer2, m1, m2)) {
								/* Store current  gameStats*/
								int gameStatsTemp[][]={
										{gameStats[0][0] , gameStats[0][1] , gameStats[0][2] },
										{gameStats[1][0] , gameStats[1][1] , gameStats[1][2] },
										{gameStats[2][0] , gameStats[2][1] , gameStats[2][2] },
								};
								
								gameStats=GridManager.movePlayer(gameStats, currentPlayer2, m1, m2);
								
								score=moveAI(opponent, remainingMovesFromDraw2 - 1 , depth + 1);
								
			//System.out.println("currentPlayer : " + currentPlayer2 + m1.x +"-"+ m1.y +"-"+ m2.x +"-"+ m2.y+"s >> Score " + score);
								
								if(score > maximumScore) {
									bestm1.x=m1.x;
									bestm1.y=m1.y;
									bestm2.x=m2.x;
									bestm2.y=m2.y;
									maximumScore=score;
								}
								
								/* Reassign Original gameStats */
								for(int p=0;p<3;p++) {
									for(int q=0;q<3;q++) {
										gameStats[p][q]=gameStatsTemp[p][q];
									}
								}
							}
						}
					}
				}
			}
			
			/* Finally move the best possible move */

			gameStats=GridManager.movePlayer(gameStats, currentPlayer2, bestm1, bestm2);

			return maximumScore;
		}
		/* If Computer is on move */
		else {
			//System.out.println("Computer's Turn");
			
			int minimumScore=1000000,score=0;
			
			for(int i=0;i<3;i++) {
				for(int j=0;j<3;j++) {
					if(gameStats[i][j]==currentPlayer2) {
						/* Check all possible 8 Movess */
						for(int k=0;k<8;k++) {
							int x[]={0 , 0 , 1 ,-1 , 0 , 0 , 2 ,-2 };
							int y[]={-1, 1 , 0 , 0 , -2, 2 , 0 , 0 }; 
							
							m1.x=i;
							m1.y=j;
							m2.x=i+ x[k];
							m2.y=j+ y[k];
							
							//System.out.println(m1.x  + " - " + m1.y+ " - " + m2.x+ " - " + m2.y);
							
							if(GridManager.isvalidMove(gameStats, currentPlayer2, m1, m2 )) {
								//System.out.println("Have validmoves");
								
								/* Store current  gameStats*/
								int gameStatsTemp[][]={
										{gameStats[0][0] , gameStats[0][1] , gameStats[0][2] },
										{gameStats[1][0] , gameStats[1][1] , gameStats[1][2] },
										{gameStats[2][0] , gameStats[2][1] , gameStats[2][2] },
								};
								
								gameStats=GridManager.movePlayer(gameStats, currentPlayer2, m1, m2);
								
								score=moveAI(opponent, remainingMovesFromDraw2 - 1 , depth + 1);
		//System.out.println("currentPlayer : " + currentPlayer2 +"=>"+ m1.x +"-"+ m1.y +"-"+ m2.x +"-"+ m2.y+"s >> Score " + score);

								if(score < minimumScore) {
									bestm1.x=m1.x;
									bestm1.y=m1.y;
									bestm2.x=m2.x;
									bestm2.y=m2.y;
									minimumScore=score;
								}
								
								/* Reassign Original gameStats */
								for(int p=0;p<3;p++) {
									for(int q=0;q<3;q++) {
										gameStats[p][q]=gameStatsTemp[p][q];
									}
								}
							}
						}
					}
				}
			}
			
			/* Finally move the best possible move for Player 1 */
			gameStats=GridManager.movePlayer(gameStats, currentPlayer2, bestm1, bestm2);
			//System.out.println("Moved : " + bestm1.x + " " + bestm1.y +" "+ bestm2.x +" "+ bestm2.y);
			return minimumScore;
		}
	}
	
	private void updateMoveStatus(Point p, Point n) {
		// TODO Auto-generated method stub
		if(p==null || n==null)
			return;
		
		String msg="Last move : From " + (3*p.x + p.y + 1)+  " to " + (3*n.x + n.y + 1);
		this.showStatus(msg);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		if(!fillingProcess) {
			winner=GridManager.gameOver(gameStats,remainingMovesFromDraw,currentPlayer);
			//System.out.println("Verdict : " + winner);
		}
		if(!fillingProcess && !winner.equals("GameIsNotOver")) {
			gameover=true;
		}
		
		repaint();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
