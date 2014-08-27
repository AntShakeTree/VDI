package com.opzoon.client.opzooncloudservice;

public class RandomTools {
	public static void main(String[] args) {
		StringBuilder stringBuilder= new StringBuilder();
		String lower="qwertyuiopasdfghjklzxcvbnm";
		String upper="POIUYTTREWQASDFGHJKLMNBVCXZ";
		String figuer="1234567890";
		String spical="~!@#$%^&*?#$%^&";
		for (int i = 0; i < 3200; i++) {
			int seed = (int) (Math.random()*4);
			int seed2 ;

			switch (seed) {
			case 0:
				seed2=(int) (Math.random()*lower.length());
				stringBuilder.append(lower.charAt(seed2));
				break;
			case 1:
				seed2=(int) (Math.random()*upper.length());
				stringBuilder.append(upper.charAt(seed2));
				break;
			case 2:
				seed2=(int) (Math.random()*figuer.length());
				stringBuilder.append(figuer.charAt(seed2));

				break;
			case 3:
				seed2=(int) (Math.random()*spical.length());
				stringBuilder.append(spical.charAt(seed2));
				break;
			default:
				break;
			}
		}
		int start =(int) (Math.random()*32);
		System.out.println(stringBuilder.substring(start,start+32));
	
	}
}
