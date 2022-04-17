package com.mongodb.prudential;

import com.mongodb.prudential.load.FindCustomer;
import com.mongodb.prudential.load.InsertCustomer;

public class InsertThreadRunner extends Thread{
	
	private int num =  0;
	private int startX = 0;
	private int endX=0;
	private int sleepmi=0;
	
	public InsertThreadRunner() {
		this.num = 0;
	}
	
	public InsertThreadRunner(int num,int startX, int endX, int sleepmi) {
		this.num = num;
		this.startX = startX;
		this.endX=endX;
		this.sleepmi=sleepmi;
	}
	
	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public int getStartX() {
		return startX;
	}

	public void setStartX(int startX) {
		this.startX = startX;
	}

	public int getEndX() {
		return endX;
	}

	public void setEndX(int endX) {
		this.endX = endX;
	}

	public int getSleepmi() {
		return sleepmi;
	}

	public void setSleepmi(int sleepmi) {
		this.sleepmi = sleepmi;
	}

	@Override
	public void run() {
		
		System.out.println("Insertion Thread #"+this.num+ " start");
		int prefix = (num+1)*100000;
		InsertCustomer insert = null;
		insert = new InsertCustomer();
		
		
		for (int startIdx = startX; startIdx <= endX; startIdx++)
		{
			try {
    			//insert = new InsertCustomer();
    			insert.insertCustomerData(prefix + startIdx);
				Thread.sleep(sleepmi);
			}catch(Exception e)
			{
				System.out.println("Java Thread error : Thread.sleep"+e.toString());
			}
		}
		
		System.out.println("Insertion Thread #" + num + " end");
	}

}
