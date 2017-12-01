package fr.ms.lang.reflect;

public class WrapInvocation {

	private Object args;
	private Object invoke;
	private Throwable targetException;

	WrapInvocation(final Object args, final Object invoke, final Throwable targetException) {
		this.args = args;
		this.invoke = invoke;
		this.targetException = targetException;
	}

	public Object getArgs() {
		return args;
	}

	public Object getInvoke() {
		return invoke;
	}

	public Throwable getTargetException() {
		return targetException;
	}

	public void setInvoke(Object invoke) {
		this.invoke = invoke;
	}
}
