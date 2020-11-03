import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.text.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;


public class MainPos2 extends JFrame{
	String[] menu = {	// 메뉴 정보(이름)
			"Americano","Americano(Ice)","Cafe Latte","Caffe Latte(Ice)",
			"Caffe Mocha","Cafe Mocha(Ice)","Vanila Latte","Vanila latte(Ice)",
			"Espresso","Flat White","Affogato","Caramel Macciatto",
			"Caramel Macciatto(Ice)","Green Tea Latte","Green Tea Latte(ICE)", "Herb Tea", "Milk Tea", "Ice Tea"};
	String[] price = {	// 메뉴 정보(가격)
			"2000","2500","2500","3500",
			"3000","3500","3500","4000",
			"2000","3500","4000","3000",
			"3500","3700","4200","3000", "3000", "3000"};
	String sumPrice[] = {"0", "0", "0", "0", "0", "0", "0", "0", "0"};	// 각 테이블이 가지는 현재 테이블 가격 
	JButton[] table = new JButton[9];	// 테이블을 나타내는 버튼 
	
	String [] ColName = {"Menu","Quantity","Price"};	// Order의 jtable을 구현하는 model을 구성하는 정보 
	String [][] Data;
	String [] RName = {"No.", "Time", "Table No.", "Menu", "Price"}; // 영수증 jtable을 구현하는 정보 
	String [][] RData;
	DefaultTableModel rm  = new DefaultTableModel(RData, RName);
	JTable rj = new JTable(rm); // order의 jtable 
	DefaultTableModel model[] = new DefaultTableModel[9];
	int rcount = 1;
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");	// 마감, 영수증 출력시사용 

	// info 클래스의 요소 
	JLabel date = new JLabel("Date: ");	
	JLabel tableCount = new JLabel("Table: ");
	JLabel uncPrice = new JLabel("Price: ");
	JTextField tfdate = new JTextField();
	JTextField tftableCount = new JTextField();
	JTextField tfuncPrice = new JTextField();
	int TCtxt = 0;
	int UPtxt = 0;
	
	// FuncBtn클래스의 요소 
	JButton receipt;
	JButton sales;
	JButton manage;
	JButton close;
	 
	// 영수증 정보를 테이블 별로 저장하는 배열 
	String[] receiptTxt = new String[9];
	
	// 매출 정보를 확인하는 요소 
	int soldTT = 0;
	int tablelast = 0;
	int tabledone = 0;
	
	class Info extends JPanel {
		Info() {
			setLocation(751, 0);
			setSize(250, 150);
			setLayout(null);
			tfdate.setEditable(false);
			tftableCount.setEditable(false);
			tfuncPrice.setEditable(false);
			tfdate.setHorizontalAlignment(JTextField.RIGHT);
			tftableCount.setHorizontalAlignment(JTextField.RIGHT);
			tfuncPrice.setHorizontalAlignment(JTextField.RIGHT);
			date.setSize(50, 25);
			date.setLocation(0, 10);
			tfdate.setSize(180, 25);
			tfdate.setLocation(55, 10);
			add(date);
			add(tfdate);
			tableCount.setSize(50, 25);
			tableCount.setLocation(0, 45);
			tftableCount.setSize(180, 25);
			tftableCount.setLocation(55, 45);
			tftableCount.setText("0");
			add(tableCount);
			add(tftableCount);
			uncPrice.setSize(50, 25);
			uncPrice.setLocation(0, 80);
			tfuncPrice.setSize(180, 25);
			tfuncPrice.setLocation(55, 80);
			tfuncPrice.setText("0");
			add(uncPrice);
			add(tfuncPrice);
		}
	}
	class FuncBtn extends JPanel {
		FuncBtn() {
			setLocation(751, 151);
			setSize(250, 550);
			setLayout(new GridLayout(4, 1, 2, 2));
			receipt = new JButton("Receipt");
			receipt.setFont(new Font("San Serif", Font.PLAIN, 15));
			receipt.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					new Receipt();
				}
			});
			sales = new JButton("Checking Sales");
			sales.setFont(new Font("San Serif", Font.PLAIN, 15));
			sales.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					new Sales();
				}
			});
			manage = new JButton("Managing Menu");
			manage.setFont(new Font("San Serif", Font.PLAIN, 15));
			manage.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					new Manage();					
				}
				
			});
			close = new JButton("End Kitchen");
			close.setFont(new Font("San Serif", Font.PLAIN, 15));
			close.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(TCtxt > 0) {
						JOptionPane.showMessageDialog(null, "There is unpaid table! Complete the payment before end kitchen.");
					}
					else {
						int input = JOptionPane.showConfirmDialog(null, "Do you want to end the sale and print sales infomation? ", "End", JOptionPane.YES_NO_OPTION);
						if(input == JOptionPane.YES_OPTION) {
							GregorianCalendar c = new GregorianCalendar();
							SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd");
							String clock = dateFormat.format(c.getTime());
							FileWriter end = null;
							String title = clock + "_Sales_ Information.txt";
							try {
								end = new FileWriter(title);
								end.write("<" + clock + ">");
								end.write(LINE_SEPARATOR);
								for (int i = 0; i < rj.getRowCount(); i++) {
									for(int j = 0; j < rj.getColumnCount(); j++) {
										end.write(rj.getValueAt(i, j).toString() + " ");
									}
									end.write(LINE_SEPARATOR);
								}
								end.write("Total Price: " + soldTT);
							}
							catch (IOException ioe) {
								ioe.printStackTrace();
							}
							finally {
								try {
									end.close();
								}
								catch(Exception ee) {
								}
							}
							System.exit(0);
						}
					}
				}
			});
			
			add(receipt);
			add(sales);
			add(manage);
			add(close);
		}
	}
	
	class Sales extends JFrame {
		JLabel soldTotal = new JLabel("Sold Total: ");
		JLabel soldTable = new JLabel("Sold Table: ");
		JLabel orderTotal = new JLabel("Order Total: ");
		JLabel orderTable = new JLabel("Order Table: ");
		JLabel total = new JLabel("Total: ");
		Sales() {
			soldTotal.setText("Sold Total: " + soldTT);
			soldTable .setText("Sold Table: " + tabledone);
			orderTotal.setText("Order Total: " + UPtxt);
			orderTable.setText("Order Table: " + TCtxt);
			total.setText("Total: " + (soldTT + UPtxt));
			add(soldTotal);
			add(soldTable);
			add(orderTotal);
			add(orderTable);
			add(total);
			setLocation(200, 300);
			setSize(200, 300);
			setLayout(new GridLayout(5, 1, 2, 2));
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			setTitle("Sales Check");
			setVisible(true);
		}
		
	}
	class Manage extends JFrame {
		JButton[] MBtn = new JButton[18];
		JPanel j1 = new JPanel();
		JPanel j2 = new JPanel();
		JLabel name = new JLabel("new name: ");
		JLabel pr = new JLabel("new price: ");
		JTextField tfname = new JTextField(8);
		JTextField tfpr = new JTextField(8);
		int oldInt;
		JButton cha = new JButton("change!");
		Manage() {
			j1.setLayout(new GridLayout(6, 3, 3, 3));
			j2.setLayout(new FlowLayout());
			for(int i=0;i<MBtn.length;i++) {
				final int index = i;
				MBtn[i]= new JButton("<html><body>" +  menu[i] + "<br />" + price[i] + "</body></html>");
				MBtn[i].addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						oldInt = index;						
					}
					
				});
				j1.add(MBtn[i]);
			}
			j2.add(name);
			j2.add(tfname);
			j2.add(pr);
			j2.add(tfpr);
			cha.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					menu[oldInt] = tfname.getText();
					price[oldInt] = tfpr.getText();
					dispose();
				}
				
			});
			j2.add(cha);
			add(j1, BorderLayout.CENTER);
			add(j2, BorderLayout.SOUTH);
			setLocation(200, 300);
			setSize(500, 300);
			
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			setTitle("Menu Management");
			setVisible(true);
		}
	}
	class Receipt extends JFrame {
		JPanel sc = new JPanel();
		JPanel cs = new JPanel();
		JTextField tfselect = new JTextField(10);
		JButton select = new JButton("Print");
		Receipt() {
			rj.setRowHeight(30);
			rj.getColumn("No.").setPreferredWidth(45);
			rj.getColumn("Time").setPreferredWidth(230);
			rj.getColumn("Table No.").setPreferredWidth(90);
			rj.getColumn("Menu").setPreferredWidth(570);
			rj.getColumn("Price").setPreferredWidth(100);
			JScrollPane js = new JScrollPane(rj);
			js.setPreferredSize(new Dimension(750, 250));
			sc.add(js);
			cs.setLayout(new FlowLayout());
			cs.add(tfselect);
			select.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int num = Integer.parseInt(tfselect.getText()) - 1;
					if(num >= rj.getRowCount()) {
						JOptionPane.showMessageDialog(null, "Type correct number of receipt!");
					}
					else {
						String line = "";
						FileWriter w = null;
						String title = (num + 1) + " - Reciept.txt";
						try {
							w = new FileWriter(title);
							for (int i = 0; i < rj.getColumnCount() -1; i++) {
								line += rj.getValueAt(num, i).toString() + " ";
							}
							w.write(line);
							w.write(LINE_SEPARATOR);
							w.write("Total Price: " + rj.getValueAt(num, 4));
						}
						catch (IOException ioe) {
							ioe.printStackTrace();
						}
						finally {
							try {
								w.close();
							}
							catch(Exception ee) {
							}
						}
						dispose();
					}
				}
			});
			cs.add(select);
			add(sc, BorderLayout.CENTER);
			add(cs, BorderLayout.SOUTH);
			setLocation(200, 200);
			setSize(750, 330);
			
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			setTitle("Receipt Print");
			setVisible(true);
		}
	}
	
	class TableSet extends JPanel {
		TableSet() {
			setSize(750, 700);
			setLocation(0, 0);
			setLayout(new GridLayout(3, 3, 5, 5));
			for(int i = 0; i < 9; i++) {
				final int index = i;
				table[i] = new JButton();
				table[i].setBounds(0, 0, 154, 139);
				table[i].setText(i + 1 + "");
				table[i].setFont(new Font("San Serif", Font.PLAIN, 15));
				table[i].addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						new Order2(index, MainPos2.this);
						
					}
				});
				add(table[i]);
			}
		}
	}
	
	class Clock extends Thread {
		public void run() {
			while(true) {
				GregorianCalendar c = new GregorianCalendar();
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy / MM / dd  HH:mm:ss");
				String clock = dateFormat.format(c.getTime());
				tfdate.setText(clock);
				try {
					Thread.sleep(1000);
				}
				catch(InterruptedException e) {
					
				}
			}
		}
	}
	
	public MainPos2() {
		setLayout(null);
		tdataInit();
		Info info = new Info();
		Clock ttt = new Clock();
		FuncBtn btn = new FuncBtn();
		ttt.start();
		TableSet tableSet = new TableSet();
		add(tableSet);
		add(info);
		add(btn);
		setSize(1000, 730);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setTitle("Pos Main");
		setVisible(true);
		
	}

	public void tableDealSet(int ind) {	// 결제버튼시
		GregorianCalendar c = new GregorianCalendar();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd  HH:mm:ss");
		String clock = dateFormat.format(c.getTime());
		rm.addRow(new String[] {rcount + "", clock, (ind + 1)+ "", receiptTxt[ind], this.sumPrice[ind]});
		rcount++;
		table[ind].setText(ind + 1 + "");
		soldTT += Integer.parseInt(sumPrice[ind]);
		this.sumPrice[ind] = "0";
		tablelast--;
		tabledone++;
		TCtxt = 0;
		UPtxt = 0;
		for(int i = 0; i < sumPrice.length; i++) {
			UPtxt += Integer.parseInt(sumPrice[i]);
			if(Integer.parseInt(sumPrice[i]) != 0) {
				TCtxt++;
			}
		}
		tftableCount.setText(TCtxt + "");
		tfuncPrice.setText(UPtxt + "");
	}
	public void tableOrderSet(int ind, String price, String ordertxt) {	// 주문 
		sumPrice[ind] = price;
		table[ind].setText(ordertxt);
		TCtxt = 0;
		UPtxt = 0;
		for(int i = 0; i < sumPrice.length; i++) {
			UPtxt += Integer.parseInt(sumPrice[i]);
			if(Integer.parseInt(sumPrice[i]) != 0) {
				TCtxt++;
			}
		}
		tftableCount.setText(TCtxt + "");
		tfuncPrice.setText(UPtxt + "");
	}
	
	public void tableEmptySet(int ind) {	// 취소버튼시 테이블 버튼  내용 초기화 
		receiptTxt[ind] = "";
		table[ind].setText(ind + 1 + "");
		this.sumPrice[ind] = "0";
		TCtxt = 0;
		UPtxt = 0;
		for(int i = 0; i < sumPrice.length; i++) {
			UPtxt += Integer.parseInt(sumPrice[i]);
			if(Integer.parseInt(sumPrice[i]) != 0) {
				TCtxt++;
			}
		}
		tftableCount.setText(TCtxt + "");
		tfuncPrice.setText(UPtxt + "");
	}
	public void tdataInit() {	// 최초 table데이터 구성 
		for(int i = 0; i < 9; i++) {
			model[i] = new DefaultTableModel(Data,ColName);
		}
	}
}
