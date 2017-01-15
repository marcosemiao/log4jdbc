package fr.ms.log4jdbc.sqloperation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SqlMessage {

	private final List<SqlOperationMessage> messages = Collections
			.synchronizedList(new ArrayList<SqlOperationMessage>());

	private final static SqlMessage instance = new SqlMessage();

	public final static SqlMessage getInstance() {
		return instance;
	}

	public List<SqlOperationMessage> getSqlMessages() {
		return getSqlMessages(true);
	}

	public List<SqlOperationMessage> getSqlMessages(final boolean erase) {
		final List<SqlOperationMessage> list = new ArrayList<SqlOperationMessage>(messages);
		if (erase) {
			clear();
		}
		return Collections.unmodifiableList(list);

	}

	public void clear() {
		messages.clear();
	}

	void addMessage(final SqlOperationMessage message) {
		messages.add(message);
	}
}
