package io.itit.smartjdbc.connection;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author skydu
 *
 */
public class Connections {
	//
	private List<Connection> connections;
	//
	private boolean useTransaction;
	//
	public Connections(){
		connections=new ArrayList<>();
	}

	/**
	 * @return the connections
	 */
	public List<Connection> getConnections() {
		return connections;
	}

	/**
	 * @param connections the connections to set
	 */
	public void setConnections(List<Connection> connections) {
		this.connections = connections;
	}

	/**
	 * 
	 * @param index
	 * @return
	 */
	public Connection getConnection(int index) {
		if(connections.size()<=index){
			throw new RuntimeException("index "+index+" is out of range");
		}
		return connections.get(index);
	}

	/**
	 * @return the useTransaction
	 */
	public boolean isUseTransaction() {
		return useTransaction;
	}

	/**
	 * @param useTransaction the useTransaction to set
	 */
	public void setUseTransaction(boolean useTransaction) {
		this.useTransaction = useTransaction;
	}
}
