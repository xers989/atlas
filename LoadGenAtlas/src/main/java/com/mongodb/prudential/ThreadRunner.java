package com.mongodb.prudential;

import com.mongodb.prudential.load.FindCustomer;

public class ThreadRunner extends Thread{
	private int num =  0;
	private int startX = 0;
	private int endX=0;
	private int sleepmi=0;
	private int findloop=0;
	
	public ThreadRunner() {
		this.num = 0;
	}
	
	public ThreadRunner(int num,int startX, int endX, int sleepmi, int findloop) {
		this.num = num;
		this.startX = startX;
		this.endX=endX;
		this.sleepmi=sleepmi;
		this.findloop=findloop;
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

	public int getFindloop() {
		return findloop;
	}

	public void setFindloop(int findloop) {
		this.findloop = findloop;
	}

	@Override
	public void run() {
		System.out.println("Find Thread #"+this.num+ " start");
		
		FindCustomer find = null;
		find = new FindCustomer();
		try {
			int i=0;
			while (i < findloop)
			{
	    		for (int startIdx = startX; startIdx <= endX; startIdx++)
				{
	    			//find = new FindCustomer();
	    			find.findCustomerData(startIdx);
					Thread.sleep(sleepmi);
				}
	    		i++;
			}
		}catch(Exception e)
		{
			System.out.println("Java Thread error : Thread.sleep"+e.toString());
		}
		System.out.println("Find Thread #" + num + " end");
	}

}
