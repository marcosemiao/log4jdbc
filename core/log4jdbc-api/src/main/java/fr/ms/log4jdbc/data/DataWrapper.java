package fr.ms.log4jdbc.data;

public interface DataWrapper {

	Class getType();
	
	Object wrap(Object obj);
}
