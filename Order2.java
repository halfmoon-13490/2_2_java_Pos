import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import javax.swing.*;
import javax.swing.table.*;

public class Order2 extends JFrame {
	// 메뉴 정보 및 버튼 
	JButton[] MBtn = new JButton[18];
	int[] menuState = {0, 0, 0, 0,
					   0, 0, 0, 0, 
					   0, 0, 0, 0,
					   0, 0, 0, 0, 0, 0};
	// 계산창 부분 
	JLabel suml = new JLabel("Sum: ");
	JLabel payl = new JLabel("Pay: ");
	JTextField sum = new JTextField(30);
	JTextField pay = new JTextField(30);
	
	DefaultTableModel m;
	JTable jtable;
	
	GregorianCalendar c = new GregorianCalendar();
	SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
	String clock;
	
	//기능 버튼 및 정보 
	JButton[] SBtn = new JButton[4];
	String[] Str = {"Order","Cancel","Cancel All", "Payment"};
	
	String receiptTxt = "";
	
	String count = "1";
	String money = "0";
	
	int sumIndex;
	String ordertxt = "";
	
	class MainPanel extends JPanel{
		MainPanel(int ind, MainPos2 tableThis) {
			setLayout(null);
			setBackground(Color.WHITE);
			MenuBtn mbtn = new MenuBtn(tableThis);
			StrBtn sbtn = new StrBtn();
			Screen sc = new Screen(ind);
			
			suml.setSize(120, 25);
			suml.setLocation(50, 485);
			add(suml);
			sumIndex = ind;
			sum.setText(tableThis.sumPrice[sumIndex]);
			sum.setHorizontalAlignment(JTextField.RIGHT);
			sum.setEditable(false);
			sum.setSize(300, 25);
			sum.setLocation(180, 485);
			add(sum);
			
			payl.setSize(120, 25);
			payl.setLocation(50, 515);
			add(payl);
			pay.setHorizontalAlignment(JTextField.RIGHT);
			pay.setText(money);
			pay.setSize(300, 25);
			pay.setLocation(180, 515);
			add(pay);
			
			sc.setSize(500, 500);
			sc.setLocation(25, 20);
			add(sc);
			
			mbtn.setSize(400, 430);
			mbtn.setLocation(530, 23);
			add(mbtn);
			
			sbtn.setSize(400, 70);
			sbtn.setLocation(530, 480);
			add(sbtn);
		}
		
	}
	
	class Screen extends JPanel{
		Screen(int ind){
			setBackground(Color.WHITE);
			jtable.setRowHeight(50);
			jtable.setFont(new Font("", Font.ITALIC, 15));
			add(new JScrollPane(jtable));
		}
	}
	
	class MenuBtn extends JPanel{
		MenuBtn(MainPos2 tableThis){
			setLayout(new GridLayout(6,3,3,3));
			setBackground(Color.WHITE);
			for(int i=0;i<MBtn.length;i++) {
				MBtn[i]= new JButton("<html><body>" +  tableThis.menu[i] + "<br />" + tableThis.price[i] + "</body></html>");
				add(MBtn[i]);
			}
		}
		
	}
	class StrBtn extends JPanel{
		StrBtn(){
			setBackground(Color.WHITE);
			setLayout(new GridLayout(1,4,3,3));
			
			for(int i=0;i<Str.length;i++) {
				SBtn[i]= new JButton(Str[i]);
				add(SBtn[i]);
			}
		}
	}
	
	
	public Order2(int ind , MainPos2 tableThis) {
		m = tableThis.model[ind];
		jtable = new JTable(m);
		setSize(1000, 600);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle("Order Menu " + (ind + 1));
		setVisible(true);
		
		setContentPane(new MainPanel(ind, tableThis));
		
		// 메뉴추가
		for(int i=0;i < MBtn.length; i++) {
			final int index =i;
			
			MBtn[i].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int tt = 0;
					
					
					if (menuState[index] == 0) {
						m.addRow(new String[]{tableThis.menu[index],count,tableThis.price[index]});
						menuState[index] = 1;
					}
					else {
						for(int j = 0; j < jtable.getRowCount(); j++) {
							if(m.getValueAt(j, 0).equals(tableThis.menu[index])) {
								int quantity = Integer.parseInt(m.getValueAt(j, 1) + "");
								quantity++;
								m.setValueAt(quantity, j, 1);
								m.setValueAt(quantity * Integer.parseInt(tableThis.price[index]) + "", j, 2);
							}
						}
					}
					
					for(int j = 0; j < jtable.getRowCount(); j++) {
						tt += Integer.parseInt((String) jtable.getValueAt(j, 2)) ;
					}
					sum.setText(tt + "");
				}
			});
		}
		
		// 주문 
		SBtn[0].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(Integer.parseInt(sum.getText()) == 0) {
					tableEmpty(ind, tableThis);
				}
				else {
					tableOrder(ind, sum.getText(), tableThis);
					tableThis.tablelast++;
				}
			}
		});
		
		// 선택취소
		SBtn[1].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int tt = 0;
				m.removeRow(jtable.getSelectedRow());
				for(int j = 0; j < jtable.getRowCount(); j++) {
					tt += Integer.parseInt((String)jtable.getValueAt(j, 2));
				}
				sum.setText(tt + "");
			}
		});
		
		
		// 전체취소
		SBtn[2].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int input = JOptionPane.showConfirmDialog(null, "Do you want to canel all order? ", "End", JOptionPane.YES_NO_OPTION);
				if(input == JOptionPane.YES_OPTION) {
					int tt = 0;
					m.setRowCount(0);
					for(int j = 0; j < jtable.getRowCount(); j++) {
						tt += (int) jtable.getValueAt(j, 2);
					}
					sum.setText(tt + "");
				}
			}
		});
		
		// 결제 
		SBtn[3].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(tableThis.sumPrice[ind] != "0") {
					if(Integer.parseInt(pay.getText()) >= Integer.parseInt(sum.getText())) {
						tableDeal(ind, tableThis);
					}
					else {
						JOptionPane.showMessageDialog(null, "Type the correct price!");
					}
				}
				else {
					JOptionPane.showMessageDialog(null, "Order the menues before the payment!");
				}
				
			}
		});
			
		
	}
	
	public void tableOrder(int ind, String total, MainPos2 tableThis) { // 메인 포스의 테이블 정보 세팅
		clock = dateFormat.format(c.getTime());
		ordertxt += "<html>";
		ordertxt += ind + " table";
		ordertxt += "<br />";
		for(int i=0;i<jtable.getRowCount();i++){
			ordertxt += jtable.getValueAt(i,0).toString();//메뉴이름
			ordertxt += " x";
			ordertxt += jtable.getValueAt(i,1).toString();//개수
			ordertxt += "<br />";
		}
		ordertxt += "<br />";
		ordertxt += sum.getText() + "won";
		ordertxt += "<br />";
		ordertxt += clock;
		ordertxt += "</html>";
		
		for(int i=0;i<jtable.getRowCount();i++){
			receiptTxt += jtable.getValueAt(i,0).toString();
			receiptTxt += " x";
			receiptTxt += jtable.getValueAt(i,1).toString();
			receiptTxt += "/";
		}
		tableThis.receiptTxt[ind] = receiptTxt;
		
		tableThis.tableOrderSet(ind, total, ordertxt);
		tableThis.model[ind] = this.m; 
		dispose();
	}
	public void tableEmpty(int ind, MainPos2 tableThis) {
		tableThis.tableEmptySet(ind);
		m.setRowCount(0);
		dispose();
	}
	
	public void tableDeal(int ind, MainPos2 tableThis) {
		String change = Integer.parseInt(pay.getText()) - Integer.parseInt(sum.getText()) + "";
		JOptionPane.showMessageDialog(null, "Change: " + change);
		sum.setText("");
		pay.setText("");
		tableThis.tableDealSet(ind);
		m.setRowCount(0);
		dispose();
	}
}