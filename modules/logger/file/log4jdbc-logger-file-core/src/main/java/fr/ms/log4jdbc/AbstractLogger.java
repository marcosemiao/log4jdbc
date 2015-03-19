package fr.ms.log4jdbc;

import fr.ms.log4jdbc.message.MessageProcess;
import fr.ms.log4jdbc.thread.ThreadMessageProcess;
import fr.ms.log4jdbc.utils.Log4JdbcProperties;

abstract class AbstractLogger {

  private final static Log4JdbcProperties props = Log4JdbcProperties.getInstance();

  private final MessageProcess messageProcess;

  private final MessageProcess messageProcessThread;

  AbstractLogger(MessageProcess messageProcess) {
    this.messageProcess = messageProcess;
    this.messageProcessThread = new ThreadMessageProcess(messageProcess);
  }

  MessageProcess getInstance() {
    if (props.logProcessThread()) {
      return messageProcessThread;
    }
    return messageProcess;
  }
}
