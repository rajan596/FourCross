import java.applet.Applet;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Label;
import java.awt.Paint;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JOptionPane;

public class TwoPlayers extends Applet implements MouseListener{
	
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
		
		showStatus("Player "+ currentPlayer +" : Put your Warrier ");
		addMouseListener(this);
	}
	
	public void paint(Graphics g) {
		
		/* Statistics... */
		if(fillingProcess==false && !gameover) {
			String msg="Player " + currentPlayer + " Your Turn ";
			g.drawString(msg, 50, 50);
		}
		
		if(gameover && !fillingProcess) {
			String msg="GAME OVER :: Winner " + winner;
			g.drawString(msg, 50, 50);
		}
		
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
		
	}
	
	public void changePlayer() {
		currentPlayer=currentPlayer==Stats.P1 ? Stats.P2 : Stats.P1;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		System.err.println("clicked...");
		
		int xx=e.getX();
		int yy=e.getY();
		
		System.out.println(xx);
		
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
			
			//System.out.println("Succ : " + success);
			
			if(success==1) {
				repaint();
				filledPlayers++;
				changePlayer();
				if(filledPlayers==8) {
					fillingProcess=false;
					this.showStatus("Game Started ....");
					return;
				}
				showStatus("Player "+ currentPlayer +" : Put your Warrier ");
			} 
			
			
		}
		/* Moves */
		else {
			if(pre==null) {
				if(gameStats[p.x][p.y]==currentPlayer) {
					pre=p;
					System.out.println("Previous : " + pre.x +" : "+ pre.y);
				}
			}
			else {
				
				if(gameStats[p.x][p.y]==Stats.BLANK) {
					next=p;
					System.out.println("Next : " + next.x +" : "+ next.y);
					if(GridManager.isvalidMove(gameStats,currentPlayer,pre,next)) {
						gameStats=GridManager.movePlayer(gameStats,currentPlayer,pre,next);
						pre=next=null;
						repaint();
						changePlayer();
					}
				}
				
				if(gameStats[p.x][p.y]==currentPlayer) {
					pre=p;
					next=null;
				}
			}
		}
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		winner=GridManager.gameOver(gameStats);
		
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
