import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
 
// 描画した図形を記録する Figure クラス (継承して利用する)
class Figure {
    protected int x, y, width, height;
    protected Color color;
    public Figure(int x, int y, int w, int h, Color c) {
        this.x = x; this.y = y;  // this.x, this.y はインスタンス変数．
        width = w; height = h;   // ローカル変数で同名の変数がある場合は，this
        color = c;               // を付けると，インスタンス変数を指す．
    }
    //大きさを変更するメソッド
    public void setSize(int w, int h) {
        width = w; height = h;
    }
    //位置を変更するメソッド
    public void setLocation(int x, int y) {
        this.x = x; this.y = y;
    }
    //2点の座標によって位置と大きさを設定するメソッドreshape
    public void reshape(int x1, int y1, int x2, int y2) {
        int newx = Math.min(x1, x2);
        int newy = Math.min(y1, y2);
        int neww = Math.abs(x1 - x2);
        int newh = Math.abs(y1 - y2);
        setLocation(newx, newy);
        setSize(neww, newh);
    }
    public void draw(Graphics g) {}
}
//長方形を表すサブクラスRectangleFigure
class RectangleFigure extends Figure {
    public RectangleFigure(int x, int y, int w, int h, Color c) {
        super(x, y, w, h, c);
        // 引数付きのコンストラクタは継承されないので，コンストラクタを定義．
        // superで親のコンストラクタを呼び出すだけ．
    }
    public void draw(Graphics g) {
        g.setColor(color);
        g.drawRect(x, y, width, height);
        //setColorで色を指定
        //drawRectでドラッグ範囲内に長方形を作成
    }
}
//楕円を表すサブクラスOvalFigure
class OvalFigure extends Figure {
    public OvalFigure(int x, int y, int w, int h, Color c) {
		super(x, y, w, h, c);
	}
	public void draw(Graphics g) {
		g.setColor(color);
		g.drawOval(x, y, width, height);
		//drawOvalでドラッグ範囲内に楕円を作成
	}
}
//直線を表すサブクラスLineFigure
class LineFigure extends Figure {
	public LineFigure(int x, int y, int w, int h, Color c) {
		super(x, y, w, h, c);
	}
	public void draw(Graphics g) {
		g.setColor(color);
		g.drawLine(x, y, width, height);
		//drawLineでドラッグ範囲内に直線を描画
	}
}

////////////////////////////////////////////////
// Model (M)
 
// modelは java.util.Observableを継承する．Viewに監視される．
class DrawModel extends Observable {
    protected ArrayList<Figure> fig;
    protected Figure drawingFigure;
    protected Color currentColor;
    protected String currentFigure; //現在の図形の形を入れる
    public DrawModel() {
        fig = new ArrayList<Figure>();
        drawingFigure = null;
        currentColor = Color.red;
        currentFigure = "Rectangle";
    }
    //ArrayListによって図形を記録
    public ArrayList<Figure> getFigures() {
        return fig;
    }
    public Figure getFigure(int idx) {
        return fig.get(idx);
    }
  
    //形を変更するメソッドsetFigure
    void setFigure(String figure) {
	    currentFigure = figure;
    }
  
    //currentFigureによって描画する図形を決定するメソッド
    public void createFigure(int x, int y) {
	    Figure f = new RectangleFigure(x, y, 0, 0, currentColor);
	    if(currentFigure == "Oval") {
		    f = new OvalFigure(x, y, 0, 0, currentColor);
	    } else if(currentFigure == "Line") {
		    f = new LineFigure(x, y, x, y, currentColor);
	    }
	    fig.add(f);
	    drawingFigure = f;
	    setChanged();
	    notifyObservers();
    }
  
    public void reshapeFigure(int x1, int y1, int x2, int y2) {
        if (drawingFigure != null) {
        drawingFigure.reshape(x1, y1, x2, y2);
        setChanged();
        notifyObservers();
        }
    }
    //currentColorを変更するメソッドを挿入
    void setDrawColor(Color c) {
	    currentColor = c;
    }
}
 
////////////////////////////////////////////////
// View (V)
 
// Viewは，Observerをimplementsする．Modelを監視して，
// モデルが更新されたupdateする．実際には，Modelから
// update が呼び出される．
class ViewPanel extends JPanel implements Observer {
    protected DrawModel model;
    public ViewPanel(DrawModel m, DrawController c) {
        this.setBackground(Color.white);
        this.addMouseListener(c);
        this.addMouseMotionListener(c);
        model = m;
        model.addObserver(this);
    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        ArrayList<Figure> fig = model.getFigures();
        for(int i = 0; i < fig.size(); i++) {
            Figure f = fig.get(i);
            f.draw(g);
        }
    }
    public void update(Observable o, Object arg) {
        repaint();
    }
}
 
//////////////////////////////////////////////////
// Main class
//   (GUIを組み立てているので，view の一部と考えてもよい)
class DrawFrame extends JFrame implements ActionListener{
    DrawModel model;
    ViewPanel view;
    DrawController cont;
    //色を変更するパネルとボタンを追加
    private JPanel p1, p2;
    private JButton b1, b2, b3, b4, b5, b6, b7, b8, b9, b10;
    public DrawFrame() {
        model = new DrawModel();
        cont = new DrawController(model);
        view = new ViewPanel(model,cont);
        //パネルとボタンを追加
        p1 = new JPanel();
        p2 = new JPanel();
        b1 = new JButton("Red");       // 赤色に変更するボタン
        b2 = new JButton("Brue");      //青色に変更するボタン
	    b3 = new JButton("Yellow");    //黄色に変更するボタン
	    b4 = new JButton("Green");     //緑色に変更するボタン
	    b5 = new JButton("Black");     //黒色に変更するボタン
	    b6 = new JButton("Orange");    //オレンジ色に変更するボタン
	    b7 = new JButton("Pink");      //ピンク色に変更するボタン
	    b8 = new JButton("Rectangle"); //長方形に変更するボタン
	    b9 = new JButton("Oval");      //楕円に変更するボタン
	    b10 = new JButton("Line");     //直線に変更するボタン
	    b1.addActionListener(this);
	    b2.addActionListener(this);
	    b3.addActionListener(this);
	    b4.addActionListener(this);
	    b5.addActionListener(this);
	    b6.addActionListener(this);
	    b7.addActionListener(this);
	    b8.addActionListener(this);
	    b9.addActionListener(this);
	    b10.addActionListener(this);
	    p1.setLayout(new GridLayout(7, 1));
	    p1.add(b1); p1.add(b2); p1.add(b3); p1.add(b4);
	    p1.add(b5); p1.add(b6); p1.add(b7);  //色を変更するボタンをp1に貼り付ける
	    p2.setLayout(new GridLayout(3, 1));
	    p2.add(b8); p2.add(b9); p2.add(b10); //形を変更するボタンをp2に貼り付ける
	    this.add(p1, BorderLayout.WEST);
	    //左側に色を変更するボタンのパネルを追加
	    this.add(p2, BorderLayout.EAST);
	    //右側に図形を変更するボタンのパネルを追加
        this.setBackground(Color.black);
        this.setTitle("Draw Editor");
        this.setSize(500, 500);
        this.add(view);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }
  
    //ボタンで色と形を変更できるようにactionPerformedを実装
    public void actionPerformed(ActionEvent e) {
	    if(e.getSource() == b1) {
  		    model.setDrawColor(Color.RED);
  	    } else if(e.getSource() == b2) {
  		    model.setDrawColor(Color.BLUE);
  	    } else if(e.getSource() == b3) {
  		    model.setDrawColor(Color.YELLOW);
  	    } else if(e.getSource() == b4) {
  		    model.setDrawColor(Color.GREEN);
  	    } else if(e.getSource() == b5) {
 		    model.setDrawColor(Color.BLACK);
  	    } else if(e.getSource() == b6) {
  		    model.setDrawColor(Color.ORANGE);
  	    } else if(e.getSource() == b7) {
  		    model.setDrawColor(Color.PINK);
  	    } else if(e.getSource() == b8) {
  		    model.setFigure("Rectagnle");
  	    } else if(e.getSource() == b9) {
  		    model.setFigure("Oval");
  	    } else if(e.getSource() == b10) {
  		    model.setFigure("Line");
  	    }
    }
    //メインクラスではDrawFrameを生成するだけ
    public static void main(String[] args) {
        new DrawFrame();
    }
}
 
////////////////////////////////////////////////
// Controller (C)
//マウスが押されたら開始点を記録し、同時に新しく図形を作る
class DrawController implements MouseListener, MouseMotionListener {
    protected DrawModel model;
    DrawFrame frame;
    protected int dragStartX, dragStartY;
    public DrawController(DrawModel a) {
        model = a;
    }
    public void mouseClicked(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {
        dragStartX = e.getX(); dragStartY = e.getY();
        model.createFigure(dragStartX, dragStartY);
    }
    public void mouseDragged(MouseEvent e) {
        model.reshapeFigure(dragStartX, dragStartY, e.getX(), e.getY());
    }
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {}
}