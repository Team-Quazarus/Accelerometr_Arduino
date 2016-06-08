package frame;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;


public class PortParam extends JFrame implements WindowListener{
	Font progFont = new Font("Arial", 0, 16);
	ConnectFrame f;
	public PortParam(ConnectFrame connectFrame) {
		super("PortParam");
		setLayout(new GridLayout(5, 2));
		setSize(400,200);
		addWindowListener(this);
		f=connectFrame;
		JLabel sp= new JLabel("Speed"),db = new JLabel("dataBits"),sb = new JLabel("stopBits"),pa = new JLabel("parity"),sr = new JLabel("setRTS"),sd = new JLabel("setDTR");
		JComboBox<Integer> s = new JComboBox<>(new Integer[]{50,75,110,150,300,600,1200,2400,4800,9600,19200});
		JComboBox<Integer> d = new JComboBox<>(new Integer[]{5,6,7,8});
		JComboBox<Integer> st = new JComboBox<>(new Integer[]{1,2,3});
		JComboBox<String> p = new JComboBox<>(new String[]{"PARITY_NONE","PARITY_ODD","PARITY_EVEN","PARITY_MARK","PARITY_SPACE"});
		
		JCheckBox rts = new  JCheckBox("setRTS"),dtr = new JCheckBox("setDTR");
		db.setFont(progFont);
		sb.setFont(progFont);
		pa.setFont(progFont);
		sr.setFont(progFont);
		sd.setFont(progFont);
		sp.setFont(progFont);
		s.setFont(progFont);
		d.setFont(progFont);
		st.setFont(progFont);
		p.setFont(progFont);
		rts.setFont(progFont);
		dtr.setFont(progFont);
		
		add(sp);
		add(s);
		add(db);
		add(d);
		add(sb);
		add(st);
		add(pa);
		add(p);
		add(rts);
		add(dtr);
	
		
		
	}
	@Override
	public void windowOpened(WindowEvent e) {
	
		
	}
	@Override
	public void windowClosing(WindowEvent e) {
		f.setVisible(true);
		this.dispose();
	}
	@Override
	public void windowClosed(WindowEvent e) {
		
		
	}
	@Override
	public void windowIconified(WindowEvent e) {
		
		
	}
	@Override
	public void windowDeiconified(WindowEvent e) {
		
		
	}
	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

}
