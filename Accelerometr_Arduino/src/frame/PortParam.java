package frame;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;

import main.Main;


public class PortParam extends JFrame implements WindowListener{
	Font progFont = new Font("Arial", 0, 16);
	ConnectFrame f;
	JComboBox<Integer> s,d,st;
	JComboBox<String> p;
	JCheckBox rts,dtr;
	
	public PortParam(ConnectFrame connectFrame) {
		super("PortParam");
		setLayout(new GridLayout(5, 2));
		setSize(400,200);
		addWindowListener(this);
		f=connectFrame;
		JLabel sp= new JLabel("Speed"),db = new JLabel("dataBits"),sb = new JLabel("stopBits"),pa = new JLabel("parity"),sr = new JLabel("setRTS"),sd = new JLabel("setDTR");
		 s = new JComboBox<>(new Integer[]{50,75,110,150,300,600,1200,2400,4800,9600,19200});
		 d = new JComboBox<>(new Integer[]{5,6,7,8});
		 st = new JComboBox<>(new Integer[]{1,2,3});
		 p = new JComboBox<>(new String[]{"PARITY_NONE","PARITY_ODD","PARITY_EVEN","PARITY_MARK","PARITY_SPACE"});
		rts = new  JCheckBox("setRTS");
		dtr = new JCheckBox("setDTR");
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
		int i=0;
		List<Integer> lst = f.conf;
		if(lst==null||lst.isEmpty()) return;
		switch(lst.get(0)){
		case 50: i=0;
		break;
		case 75: i=1;
		break;
		case 110: i=2;
		break;
		case 150: i=3;
		break;
		case 300: i=4;
		break;
		case 600: i=5;
		break;
		case 1200:i=6;
		break;
		case 2400:i=7;
		break;
		case 4800:i=8;
		break;
		case 9600:i=9;
		break;
		case 19200:i=10;
		break;
		}
		s.setSelectedIndex(i);
		i=0;
		switch(lst.get(1)){
		case 5: i=0;
		break;
		case 6: i=1;
		break;
		case 7: i=2;
		break;
		case 8: i=3;
		break;
		}
		d.setSelectedIndex(i);
		i=0;
		st.setSelectedIndex(lst.get(2)-1);
		p.setSelectedIndex(lst.get(3));
		rts.setSelected(lst.get(4)==1);
		dtr.setSelected(lst.get(5)==1);
		
	}
	@Override
	public void windowClosing(WindowEvent e) {
		List<Integer> lst = f.conf;
		if(lst==null) lst = new ArrayList<>();
		lst.add((Integer)s.getSelectedItem());
		lst.add((Integer)d.getSelectedItem());
		lst.add((Integer)st.getSelectedItem());
		lst.add(p.getSelectedIndex());
		if(rts.isSelected()) lst.add(1);
		else lst.add(0);
		if(dtr.isSelected()) lst.add(1);
		else lst.add(0);
		Main.conf.addObj("ParamsPort", lst);
		
	
		//this.dispose();
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
