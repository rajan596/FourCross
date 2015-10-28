import java.applet.Applet;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Label;
import java.awt.Paint;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Random;

import javax.swing.JOptionPane;

/**
 * @author RAJAN
 * 
 * Random moves from Computer
 * Player 1 : YOU
 * Player 2 : Computer ( Which moves Randomly )
 *
 * */
public class SinglePlayerRandom extends Applet implements MouseListener{
	
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
			
			msg="Remaining Moves From Draw : "+remainingMovesFromDraw;
			g.setColor(Color.BLUE);
			g.setFont(new Font("Calibri", Font.LAYOUT_LEFT_TO_RIGHT, 20));
			g.drawString(msg, 3*x/2 - 20, this.getHeight()-20);
			
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
			}
						
			checkGameStats();
			
			/* Computer's Move */
			if(currentPlayer==Stats.P2 && pre==null && next==null && !gameover) {
				movePlayer2();
			}
			
			checkGameStats();
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

	private void movePlayer2() {
		
		boolean done=false;
		Random random=new Random();
		
		while(!done) {
			//System.out.println("Inside CPU Move");
			
			int n1=random.nextInt(9);
			int n2=random.nextInt(9);
			
			//System.out.println("n1 >> n2 " + n1 + " # "+n2);
			
			Point p1=new Point(n1%3, n1/3);
			Point p2=new Point(n2%3, n2/3);
								
			if(GridManager.isvalidMove(gameStats,currentPlayer,p1,p2)) {
				done=true;
				gameStats=GridManager.movePlayer(gameStats,currentPlayer,p1,p2);
				updateMoveStatus(p1,p2);
				repaint();
				changePlayer();	
				pre=next=null;
			}
		}
		
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
