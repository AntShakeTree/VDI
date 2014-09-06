package com.vdi.vo.res;

public interface Response<T> {
	public Header getHead();

	public void setHead(Header head);

	public T getBody();

	public void setBody(T body);


	
}
